(ns hctb.clj.utils
  (:require [java-time :as jt]
            [clojure.java.io :as jio] )
  )


(defmacro ignore-exception
  "Evaluates `forms` and returns result of final form
   or 'nil' if an exception is thrown."
  [& forms]
  `(try ~@forms (catch ~Exception ~'e nil)))

        ;;;; numbers ;;;;

(defn string->double
  "Returns the Double (float) that is contained in `string`,
   otherwise returns nil."
  [string]
    (ignore-exception (Double/parseDouble string)))

(defn string->long
  "Returns the Long (base 10 integer) that is contained in `string`,
   otherwise returns nil.
  `coerce-pred` determines if a float is coereced to a long or not
  (if not, nil is returned for a string containing a float)."
  ([string] (string->long string true))
  ([string coerce-pred]
   (let [x (string->double string)
         coerce-val (when (number? x) (long x))
         nc-val (when (and coerce-val (== coerce-val x)) coerce-val) ]
     (if coerce-pred coerce-val nc-val)))
  ;; [string]
;; (ignore-exception (Long/parseLong string))
)

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

(defn replace-string-conflicts-with-underscores
  "Replaces all hyphens, parenthesis, spaces and periods with underscores."
  [string]
  (clojure.string/replace string #"-|\(|\)|\s|\." "_"))

        ;;;; time ;;;;

(defn parse-datetime
  "Convert a string `s` to a datetime format accepted by the database."
  [s]
  (jt/local-date-time "yyyy-MM-dd'T'HH:mm:ss" s))


        ;;;; I/O ;;;;

(defn list-files
  "Returns a list of just the files in the directory string `dir`."
  [dir]
  ;; (->> (file-seq (jio/file dir))
  (->> (.listFiles (jio/file dir))
       (remove #(.isDirectory ^java.io.File %))))

(defn list-subdirectories
  "Returns a list of just the subdirectories of the directory string `dir`"
  [dir]
  (->> (.listFiles (jio/file dir))
       (filter #(.isDirectory %))))

(defn file-suffix?
  "Checks if `file` has a suffix `suffix`. Works on file object types."
  [ ^String suffix ^java.io.File file]
  (when (.isFile file)
       (clojure.string/ends-with? (.getName file) (str "." suffix))))


(defn list-files-of-type
  "Returns a list of all files in the directory `dir` with the extension `ext`.
   This is not recursive, i.e not from subdirectories"
  [dir ext]
  (->> (.listFiles (jio/file dir))
       (filter (partial file-suffix? ext))))

(defn list-files-of-type-rec
  "Returns a list of all files in the directory `dir` and its sub-diretories
  with the extension `ext`."
  [dir ext]
  (->> (file-seq (jio/file dir))
       (filter (partial file-suffix? ext))))
