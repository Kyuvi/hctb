(ns hctb.core
  (:gen-class)
  (:require [hctb.clj.sql :as hs]))


(def default-csv-dir (or (System/getenv "CSVDIR") "/tmp/bike-data" ))

(def default-db {:dbtype "postgresql"
                 :reWriteBatchedInserts true
                 :dbname   (or (System/getenv "POSTGERS_DB")  "hctb")
                 :user     (or (System/getenv "POSTGRES_USER") "postgres")
                 :password (or (System/getenv "POSTGRES_PASS") "test")} )

(def default-arg (merge default-db {:csvdir default-csv-dir} ))

(defn get-arg-key
  "Helper function to get values from the map, issuing a warning if a kii is not
   a string and using defaults."
  [kii arg-map]
  (let [arg-var (kii arg-map)]
    (if (string? arg-var)
      arg-var
      (do (when-not (nil? arg-var)
            (printf "WARNiING: %s is not a string, using program defaults.%n"
                    arg-var) (flush))
            (kii default-arg)))))



(defn -main
  ([]
   (-main default-arg ))
  ([arg]
   (let [read-arg (clojure.edn/read-string arg)
         ;; if argument is not a map, issue warning and use defaults
         arg-map (if (map? read-arg)
                   read-arg
                   (do (printf
                        (str "WARNiING: This Helisinki city bike app accepts"
                             " only one (clojure) map as an argument "
                             " and %s is not a map, trying with defaults.%n")
                        arg)
                       (flush)
                       default-arg))
         ;; build database spec
         db {:dbtype (:dbtype default-db) ;; (get-arg-key :dbtype arg-map)
             :dbname (get-arg-key :dbname arg-map)
             :user (get-arg-key :user arg-map)
             :password (get-arg-key :password arg-map)
             ;; :reWriteBatchedInsert
             ;; (get-arg-key :reWriteBatchedInsert arg-map)
             }
         ;; get csv directory
         csvdir (get-arg-key :csvdir arg-map)]
     ;; main starts here
     (when-not (hs/db-connection? db)
       (throw (Exception. (str "Unable to connect to DB:" db))))
     (hs/load-csvs-to-db db csvdir)
     (println (format "%nHSL City bikes data in database %s !%n" (:dbname db)))))
)
