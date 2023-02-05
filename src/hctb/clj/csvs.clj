(ns hctb.clj.csvs
  "Namespace for processing the output of csv files containing
   bike journey and station data."
  (:require [clojure.data.csv :as csv]
            [java-time :as jt]
            [hctb.clj.utils :as utils]))

        ;;;; helper functions ;;;;

(defn- valid-time-sequence?
  "Returns true if `start-time` timestamp is before `stop-time` timestamp "
  [start-time stop-time]
  (jt/after? stop-time start-time))

(defn- process-timestamp
  "Returns a timestamp from string x or nil if string does not contain
   a valid timestamp."
  [x]
  (utils/ignore-exception (utils/parse-datetime x)))

(defn- process-latitude
  "Returns a latitude from string x or nil if string does not contain
   a valid latitude."
  [x]
  (if-let [numa (utils/string->double x)]
    (when (<= -180 numa 180) numa)))

(defn- process-logtitude
  "Returns a longtitude from string x or nil if string does not contain
   a valid longtitude."
  [x]
  (if-let [numa (utils/string->double x)]
    (when (<= -90 numa 90) numa)))

(defn- process-pos-int
  "Returns a positive integer from string x or nil if string does not contain
   a valid positive integer."
  [x]
  (if-let [numa (utils/string->long x nil)]
    (when (pos? numa) numa)))

(defn- int-str-greater-than-ten
  "Returns an integer greater than 10 from string x or nil if string does not contain
   a valid integer greater than 10."
  [x]
  (if-let [numa (utils/string->long x nil)]
    (when  (> numa 10) numa)))



        ;;;; main functions ;;;;


(defn process-header-strings
   "Returns a vector with characters that conflict with the database in
    header names replaced with underscores."
  [columns]
  (->> columns
       (mapv utils/replace-string-conflicts-with-underscores)))

(defn process-journey-row
  "Ensure that elements in `row-xs` from a 'journey' csv are of the right type,
  if they are, return a vector of the processed strings converted into the
  correct type, else returns nil (rows with distances and durations shorter than
  10 meters and 10 seconds respectively return nil)."
  [row-xs]
  (let [[a b c d e f g h ] row-xs
        d-time (process-timestamp a)
        r-time (process-timestamp b)
        d-id (process-pos-int c)
        d-name (not-empty d)
        r-id (process-pos-int e)
        r-name (not-empty f)
        distance (int-str-greater-than-ten g)
        duration (int-str-greater-than-ten h)
        journey-vector [d-time r-time d-id d-name r-id r-name distance duration]]
    (when (and (not-any? nil? journey-vector)
               (valid-time-sequence? d-time r-time))
      journey-vector)))

(defn process-station-row
  "Ensure that elements in `row-xs` from a 'station' csv are of the right type,
  if they are, return a vector of the processed strings converted into the
  correct type, else returns nil"
  [row-xs]
  (let [[a b c d e f g h i j k l m] row-xs
        fid (process-pos-int a)
        s-id (process-pos-int b)
        str-vec (mapv not-empty [c d e f g h i j])
        cap (process-pos-int k)
        long (process-logtitude l)
        lat (process-latitude m)
        station-vector (vec (concat [fid s-id] str-vec [cap long lat] ))]
    (when (not-any? nil? station-vector) station-vector)))
