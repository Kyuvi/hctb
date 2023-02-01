(ns hctb.core
  (:gen-class)
  (:require [hctb.clj.sql :as hs]))

(defn -main
  ;; "I don't do a whole lot ... yet."
  ;; [& args]
  ([] (-main hs/default-db ))
  ([db]
   (let [csvdir (System/getenv "CSVDIR")]
     (when-not csvdir
       (throw
        (Exception.
         "No CSVDIR, please specify a directory as the CSVDIR environment variable.")))
     (when-not (hs/db-connection? db)
      (throw (Exception. (str "Unable to connect to DB:" db))))
     (hs/load-csvs-to-db db csvdir)
     (format "HSL City bikes data in database %s !" (:name db-name)))))
