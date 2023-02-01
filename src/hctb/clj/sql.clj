(ns hctb.clj.sql
 (:require [clojure.data.csv :as csv]
            [java-time :as jt]
            [clojure.java.io :as jio]
            [clojure.java.jdbc :as sql]
            [hctb.clj.utils :as utils]
            [hctb.clj.csvs :as hc]
            ))

(def default-db {:dbtype "postgresql"
                 :dbname   (or (System/getenv "POSTGERS_DB")  "hctb")
                 :user     (or (System/getenv "POSTGRES_USER") "postgres")
                 :password (or (System/getenv "POSTGRES_PASS") "solita")} )

(def journey-types ["timestamp" "timestamp" "integer" "text"  "integer" "text"
                    "integer" "integer"])

(def station-types ["integer" "integer"  "text"  "text"  "text"  "text"  "text"
                    "text" "text" "text" "integer"
                    "double_precision" "double_precision"])

(defn build-sql-table-commands
  "Returns a vector of strings used to drop an existing table of `table-name`,
  and then create a new table with columns from `header-seq`."
  ([table-name header-seq column-count]
   (build-sql-table-commands table-name header-seq column-count ""))
  ([table-name header-seq column-count ending-string]
   (let [journey-prep (= column-count 8)
         ;; table-name (if journey-prep file-name "Hsl_bike_stations")
         table-types (if journey-prep journey-types station-types)
         ;; col-defs (apply
         ;;           str (interpose ",\n" (map (fn [col t-type]
         ;;                                       (format"\t%s %s" col t-type ))
         ;;                                     header-seq table-types)))
         col-defs (->> (map (fn [col t-type] (format"\t%s %s" col t-type ))
                            header-seq table-types)
                       (interpose ",\n")
                       (apply str)  )
         ]
     (vec
      (format "DROP TABLE IF EXISTS %s;" table-name)
      (format "CREATE TABLE %s (\n%s %s\n);"
              table-name col-defs ending-string))
     )))

(defn make-sql-table
  "Create a new table `table-name` in database `db`
   with columns from `header-seq`."
  [db table-name header-seq column-count]
  (sql/db-do-commands db (build-sql-table-commands
                          table-name header-seq column-count)))

(defn insert-csv-data
  "Insert data contained in `data-rows` into database `db`
   based on columns from `header-seq`."
  [db table header-seq data-rows column-count]
    (let [chunk-size 1000
          process-fn (if (= column-count 8)
                       hc/process-journey-row
                       hc/process-station-row)]
      (for [chunk-of-rows (partition chunk-size data-rows) ]
        (->> (map process-fn chunk-of-rows)
             (remove nil? )
             (sql/insert-multi! db table header-seq)))
      ))

(defn create-table-insert-file-data
  "Create table `table-name` in database `db` with data from file `csvfile`."
  [db table-name csvfile]
  (with-open [reader (jio/reader csvfile)]
    (let [csv-rows (csv/read-csv reader)
          header (apply hc/process-header-strings (first csv-rows))
          data-rows (rest csv-rows)
          column-count (count header)
          ]
      (if-not (contains? #{8 11} column-count)
        (throw (Exception.
                (format "File %s has an unexpected header %s with %s columns."
                        csvfile header column-count)))
        (do
          (make-sql-table db table-name header column-count)
          (insert-csv-data db table-name header data-rows column-count)
          column-count ;; Return column-count for use by insert-csvs-from-subdirs
        )
      ))))

(defn insert-loose-csvs
  "Insert data from files in `file-list` into database `db`,
   creating new tables based on their filenames."
  [db file-list]
  (for [csvfile file-list
    :let [table-name  (.getName csvfile)]]
    (create-table-insert-file-data db table-name csvfile)
    ;; (with-open [reader (jio/reader csvfile)]
    ;;   (let [csv-rows (csv/read-csv reader)
    ;;         header (apply hc/process-header-strings (first csv-rows))
    ;;         data-rows (rest csv-rows)
    ;;         column-count (count header)
    ;;         ;; process-fn (if (= column-count 8)
    ;;         ;;              hc/process-journey-row
    ;;         ;;              hc/process-station-row)
    ;;         ;; chunk-size 1000
    ;;         ]
    ;;     (make-sql-table db table-name header column-count)
    ;;     (insert-csv-data db table-name header data-rows column-count)
    ;;     ;; (for [chunk-of-rows (partition chunk-size data-rows) ]
    ;;       ;; (->> (remove nil? (map process-fn chunk-of-rows))
    ;;          ;; (sql/insert-multi! db table)))
    ;;     )
    ;; )
))

(defn insert-csvs-from-subdirs
  "Insert data from files in containded in sub-directories `subdir-list`
   into database `db`, creating new tables based on the subdir names.
   If any file is different from the first file in a sub-directory,
   it is passed to the `insert-loose-csvs` function,
   and a new table is made based on that file."
  [db subdir-list]
  (for [subdir subdir-list
    :let [table-name (.getName subdir)
          file-list (utils/list-files-of-type subdir "csv")
          first-file (first file-list)
          other-files (next file-list)
    ;; process first file to get header-list
          ;; column-count (create-table-insert-single-file db table-name first-file)
          ]]
    (when first-file
    ;; process first file to get subdir table column-count
      (let [column-count (create-table-insert-file-data db table-name first-file)]
        (when other-files
          (for [csvfile other-files] ;;  doseq?
            (with-open [reader (jio/reader csvfile)]
              (let [csv-rows (csv/read-csv reader)
                    next-header (apply hc/process-header-strings (first csv-rows))
                    data-rows (rest csv-rows)
                    next-column-count (count next-header)
                    ]
                (if (= column-count next-column-count )
                  ;; skip making table for the rest of the similar files
                  (insert-csv-data db table-name next-header
                                   data-rows next-column-count)
                  ;; if count is different emit warning
                  ;; and process file as a loose file
                  (do (println
                       (format
                        "WARNING: File %s has a different column count than %s,
                   so creating individual table."
                        csvfile first-file))
                      (insert-loose-csvs db (list csvfile))
                      )))
    )))))))


(defn load-csvs-to-db
  "Inserts data from csv files contained in `csvdir` into database `db`,
   creating tables as needed based on either loose filenames or
   sub-directory names. Ideally files containded in sub-directories should
   all have of similar contents."
  [db csvdir]
  (let [subdirs (utils/list-subdirectories csvdir)
        loose-csv-files (utils/list-files-of-type csvdir "csv")
        ;; table-files (remove nil? (concat subdirs loose-files))
        ;; (table-names (map #(.getName %) table-files))
        ]
    (when subdirs (insert-csvs-from-subdirs db subdirs))
    (when loose-csv-files (insert-loose-csvs db loose-csv-files))
  ))

;; sql/db-do-commands
;; sql/insert-multi!
