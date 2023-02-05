(ns hctb.core
  "Core namespace for hctb(Helsinki city bike app), an application for analysing
   journey and station data about Helsinkis city bikes."
  (:gen-class)
  (:require [hctb.clj.sql :as hs]))


(def db-spec {:dbtype "postgresql"
              :reWriteBatchedInserts true
              :host     (or (System/getenv "POSTGRES_HOST") "localhost")
              :dbname   (or (System/getenv "POSTGRES_DB")  "hctb")
              :user     (or (System/getenv "POSTGRES_USER") "postgres")
              :password (or (System/getenv "POSTGRES_PASS") "kotoba")} )


(defn -main
  ([]
   (-main (System/getenv "CSVDIR") ))
  ([dir]
     (let [csvdir (or (System/getenv "CSVDIR") dir) ;; CSVDIR overides argument
           db db-spec]
    (when-not (and csvdir (string? csvdir))
      (throw
       (Exception.
        "No valid directory provided as an argument or as the CSVDIR environment variable")))
     (when-not (hs/db-connection? db)
       (throw (Exception. (str "Unable to connect to DB:" db))))
     (hs/load-csvs-to-db db csvdir)
     (println (format "%nHSL City bikes data in database %s !%n" (:dbname db)))))
)
