(ns hctb.clj.sql
 (:require [clojure.data.csv :as csv]
            [java-time :as jt]
            [hctb.clj.utils :as util]
            [hctb.clj.csvs :as hc]
            ))

(def journey-types ["timestamp" "timestamp" "integer" "text"  "integer" "text"
                    "integer" "integer"])

(def station-types ["integer" "integer"  "text"  "text"  "text"  "text"  "text"
                    "text" "integer" "integer" "integer"])
