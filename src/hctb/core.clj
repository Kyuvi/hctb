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
(defn -main
  ;; "I don't do a whole lot ... yet."
  ;; [& args]
  ([] (-main hs/default-db ))
  ([db]
   (let [csvdir (or (System/getenv "CSVDIR") default-csv-dir)]
     (when  (= csvdir default-csv-dir)
       (println (str "WARNING: No CSVDIR given as an environment variable,\n"
                     "using default directory." default-csv-dir ".")))
     (when-not (hs/db-connection? db)
      (throw (Exception. (str "Unable to connect to DB:" db))))
     (hs/load-csvs-to-db db csvdir)
     (println (format "%nHSL City bikes data in database %s!" (:dbname db)))
)))
