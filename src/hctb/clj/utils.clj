(ns hctb.clj.utils
  (:require [java-time :as jt]
            [clojure.java.io :as jio] )
  )

        ;;;; time ;;;;

(defn parse-datetime
[s]
(jt/local-date-time "yyyy-MM-dd'T'HH:mm:ss" s))

;; 2021-05-31T23:54:48

;; (de)
;;
        ;;;; strings ;;;;
(defn alphanumeric?
  "Test if `string` is completely aphanumeric"
  [string]
  (= string (apply str (re-seq #"[a-z_A-Z0-9]" string))))

(defn spaces-to-underscores
  "Converts spaces in `string` to underscores."
  [string]
  (clojure.string/replace string #"\s" "_"))

(defn periods-to-underscores
  "Converts periods in `string` to underscores."
  [string]
  (clojure.string/replace string #"\." "_"))

        ;;;; i/o ;;;;

(defn list-files
  "Lists only the files in the directory string `dir`."
  [dir]
  (->> (file-seq (jio/file dir))
       (remove #(.isDirectory ^java.io.File %))))

(defn list-subdirectories
  "Lists only the subdirectorys of the directory string `dir`"
  [dir]
  (->> (file-seq (jio/file dir))
       (filter #(.isDirectory %))
       (remove #(= % (jio/file dir)))))

(defn file-suffix?
  "Checks if `file` has a suffix `suffix`. Works on file object types."
  [ ^String suffix ^java.io.File file]
  (and (.isFile file)
       (re-find (re-pattern (str ".*\\." suffix "$")) (.getName file))))

(defn list-files-of-type
  "Lists all files in the directory `dir` with the extension `ext`."
  [dir ext]
  (->> (file-seq (jio/file dir))
       (filter (partial file-suffix? ext))))
