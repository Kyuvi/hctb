(ns hctb.clj.utils
  (:require [java-time :as jt]
            [clojure.java.io :as jio] )
  )



        ;;;; numbers ;;;;


(defn string->bigdec
  "Returns the BigDecimal that is contained in `string`, otherwise returns nil."
  [string]
  (if (re-matches (re-pattern "[-+]?([0-9]*[.])?[0-9]+") string)
    (bigdec string)
    nil))

(defn string->long
  "Returns the Long (base 10 integer) that is contained in `string`,
   otherwise returns nil.
  `coerce-pred` determines if a float is coereced to a long or not
  (if not, nil is returned for a string containing a float)."
  ([string] (string->long string true))
  ([string coerce-pred]
   (let [x (string->bigdec string)
         coerce-val (when (number? x) (long x))
         nc-val (when (and coerce-val (== coerce-val x)) coerce-val) ]
     (if coerce-pred coerce-val nc-val))))

(defn string->double
  "Returns the Double (float) that is contained in `string`,
   otherwise returns nil."
  [string]
  (let [x (string->bigdec string)]
    (when (number? x ) (double x))))

        ;;;; strings ;;;;

(defn alphanumeric?
  "Test if `string` is completely aphanumeric."
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

        ;;;; time ;;;;

(defn parse-datetime
  "Convert a string `s` to a datetime format accepted by the database."
  [s]
  (jt/local-date-time "yyyy-MM-dd'T'HH:mm:ss" s))

;; 2021-05-31T23:54:48

;; (jt/after?)

        ;;;; I/O ;;;;

(defn list-files
  "Returns a list of only the files in the directory string `dir`."
  [dir]
  ;; (->> (file-seq (jio/file dir))
  (->> (.listFiles (jio/file dir))
       (remove #(.isDirectory ^java.io.File %))))

(defn list-subdirectories
  "Returns a list of only the subdirectorys of the directory string `dir`"
  [dir]
  ;; (->> (file-seq (jio/file dir))
  ;;      (filter #(.isDirectory %))
  ;;      (remove #(= % (jio/file dir)))))
  (->> (.listFiles (jio/file dir))
       (filter #(.isDirectory %))))

(defn file-suffix?
  "Checks if `file` has a suffix `suffix`. Works on file object types."
  [ ^String suffix ^java.io.File file]
  (when (.isFile file)
       ;; (re-find (re-pattern (str ".*\\." suffix "$")) (.getName file))))
       (clojure.string/ends-with? (.getName file) (str "." suffix))))


(defn list-files-of-type
  "Returns a list of all files in the directory `dir` with the extension `ext`."
  [dir ext]
  ;; (->> (file-seq (jio/file dir))
  (->> (.listFiles (jio/file dir))
       (filter (partial file-suffix? ext))))
