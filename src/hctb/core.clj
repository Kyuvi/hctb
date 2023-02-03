(ns hctb.core
  (:gen-class)
  (:require [hctb.clj.sql :as hs]))


(def default-csv-dir "/tmp/bike-data" )

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
