(ns hctb.clj.csvs
  (:require [clojure.data.csv :as csv]
            [java-time :as jt]
            [hctb.clj.utils :as util]))

(defn validate-latitude
  [x]
  (if-let [numa (util/string->double x)]
    (when (<= -180 numa 180) numa)))

(defn validate-logtitude
  [x]
  (if-let [numa (util/string->double x)]
    (when (<= -90 numa 90) numa)))

(defn validate-timestamp
  [x]
  (let [stamp (try util/parse-datetime x
               (catch Exception e false))]
    stamp))

(defn valid-time-sequence?
  [start-time stop-time]
  (jt/after? stop-time start-time)
  )

(defn validate-pos-int
  [x]
  (if-let [numa (util/string->long x nil)]
    (when (pos? numa) numa)))

(defn greater-than-ten
  [x]
  (if-let [numa (util/string->long x nil)]
    (when  (> numa 10) numa)))

(defn process-header-strings
  "Replaces whitespaces and periods in header names with underscores."
  [columns]
  (->> columns
       (map util/periods-to-underscores)
       (mapv util/spaces-to-underscores)))

(defn process-journey-row
  [row-xs]
  (let [[a b c d e f g h ] row-xs
        d-time (validate-timestamp a)
        r-time (validate-timestamp b)
        d-id (validate-pos-int c)
        d-name (not-empty d)
        r-id (validate-pos-int e)
        r-name (not-empty f)
        distance (greater-than-ten g)
        duration (greater-than-ten h)
        journey-vector [d-time r-time d-id d-name r-id r-name distance duration]
        ]
  (when (not-any? boolean? journey-vector)
    (when (valid-time-sequence? d-time r-time) journey-vector))
  ))

(defn process-station-row
  [row-xs]
  (let [[a b c d e f g h i j k l m] row-xs
        fid (validate-pos-int a)
        s-id (validate-pos-int b)
        str-vec (mapv not-empty [c d e f g h i j])
        cap (validate-pos-int k)
        long (validate-logtitude l)
        lat (validate-latitude m)
        station-vector (vec (concat [fid s-id] [str-vec] [cap long lat] ) )
        ;; station-vector [fid s-id c d e f g h i j cap long lat]
        ]
    (when (not-any? boolean? out-vector ) station-vector))
  )
