(ns hctb.clj.sql
 (:require [clojure.data.csv :as csv]
            [java-time :as jt]
            [clojure.java.io :as jio]
            [clojure.java.jdbc :as sql]
            [hctb.clj.utils :as util]
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
  ([table-name header-seq]
   (define-sql-table table-name header-seq ""))
  ([table-name header-seq ending-string]
   (let [journey-prep (= (count header-seq) 8)
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
  [db table-name header-seq]
  (sql/db-do-commands db (build-sql-table-commands table-name header-seq)))
