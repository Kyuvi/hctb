(ns hctb.core
  (:gen-class)
  (:require [hctb.clj.sql :as hs]))


(def db-spec {:dbtype "postgresql"
              :reWriteBatchedInserts true
              :host     (or (System/getenv "POSTGRES_HOST") "localhost")
              :dbname   (or (System/getenv "POSTGRES_DB")  "hctb")
              :user     (or (System/getenv "POSTGRES_USER") "postgres")
              :password (or (System/getenv "POSTGRES_PASS") "test")} )


(defn -main
  ([]
   (-main (System/getenv "CSVDIR") ))
  ([dir]
     (let [csvdir dir, db db-spec]
    (when-not csvdir
      (throw
       (Exception.
        "No csv directory provided as an argument or as a CSVDIR environment variable")))
     (when-not (hs/db-connection? db)
       (throw (Exception. (str "Unable to connect to DB:" db))))
     (hs/load-csvs-to-db db csvdir)
     (println (format "%nHSL City bikes data in database %s !%n" (:dbname db)))))
)
