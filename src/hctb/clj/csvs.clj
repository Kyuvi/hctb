(ns hctb.clj.csvs
  (:require [clojure.data.csv :as csv]
            [java-time :as jt]
            [hctb.clj.utils :as util]) )

(defn validate-latitude
  [x]
  (if-let [numa (util/string->double x)]
    (when (<= -180 numa 180) numa)))

(defn validate-logtitude
  [x]
  (if-let [numa (util/string->double x)]
    (when (<= -90 x 90) numa) ))

(defn validate-timestamp
  [x]
  (let [stamp (try util/parse-datetime x
               (catch Exception e false))]
    stamp
    )
  )

(defn valid-time-sequence?
  [start-time stop-time]
  (jt/after? stop-time start-time)
  )

(defn validate-station-id
  [x]
  (if-let [numa (util/string->long x nil)]
    (when (pos? numa) numa) ))

(defn greater-than-ten
  [x]
  (if-let [numa (util/string->long x nil)]
    (when  (> numa 10) numa) ))



;; (defn process-journey-row
;;   )

;; (defn process-station-row
;;   )
