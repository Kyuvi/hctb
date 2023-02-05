(ns hctb.clj.csv-test
  (:require  [hctb.clj.csvs :as csv]
             [clojure.test :as t ]))

   ;; due to time constraints and the stateful nature of the database files
   ;; I decided these were the main functions that needed to be tested


(t/deftest  csvs-process-header-strings
  (t/is (= (csv/process-header-strings
            ["test-string"  "test_string"  "test.string" "(test-string"
              "test-string)"  "(test-string with.all)"])
           ["test_string"  "test_string"  "test_string"  "_test_string"
             "test_string_"   "_test_string_with_all_"]))
)

(t/deftest csvs-process-journey-row
  (t/is (= (drop 2 (csv/process-journey-row
                  [ "2021-05-31T23:57:25" "2021-06-01T00:05:46" "094"
                   "Laajalahden aukio" "100" "Teljäntie" "2043" "500" ]))
           ;; timestamps always different so dropped
         '(94 "Laajalahden aukio" 100 "Teljäntie" 2043 500)))
  (t/is (= (csv/process-journey-row ;; return before departure
          [ "2021-06-01T00:05:46" "2021-05-31T23:57:25" "094"
           "Laajalahden aukio" "100" "Teljäntie" "2043" "500" ])
         nil))
  (t/is (= (csv/process-journey-row
          [ nil "2021-06-01T00:05:46"  "094"
           "Laajalahden aukio" "100" "Teljäntie" "2043" "500" ])
         nil))
  (t/is (= (csv/process-journey-row
          [ "2021-05-31T23:57:25" nil "094"
           "Laajalahden aukio" "100" "Teljäntie" "2043" "500" ])
         nil))
  (t/is (= (csv/process-journey-row
          [ "2021-05-31T23:57:25" "2021-06-01T00:05:46"  nil
           "Laajalahden aukio" "100" "Teljäntie" "2043" "500" ])
         nil))
  (t/is (= (csv/process-journey-row
          [ "2021-05-31T23:57:25" "2021-06-01T00:05:46" "094"
           "" "100" "Teljäntie" "2043" "500" ])
         nil))
  (t/is (= (csv/process-journey-row
          [ "2021-05-31T23:57:25" "2021-06-01T00:05:46" "094"
           "Laajalahden aukio" nil "Teljäntie" "2043" "500" ])
         nil))
  (t/is (= (csv/process-journey-row
          [ "2021-05-31T23:57:25" "2021-06-01T00:05:46" "094"
           "Laajalahden aukio" "100" "" nil "500" ])
         nil))
  (t/is (= (csv/process-journey-row
          [ "2021-05-31T23:57:25" "2021-06-01T00:05:46" "094"
           "Laajalahden aukio" "100" "Teljäntie" "2043" nil ])
         nil)))


(t/deftest csvs-process-station-row
  (t/is (= (csv/process-station-row
          ["1" "501" "Hanasaari" "Hanaholmen" "Hanasaari" "Hanasaarenranta 1"
           "Hanaholmsstranden 1" "Espoo" "Esbo" "CityBike Finland" "10"
           "24.840319" "60.16582"])
         [1 501 "Hanasaari" "Hanaholmen" "Hanasaari" "Hanasaarenranta 1"
          "Hanaholmsstranden 1" "Espoo" "Esbo" "CityBike Finland"
          10 24.840319 60.16582]))
  (t/is (= (csv/process-station-row
          [nil "501" "Hanasaari" "Hanaholmen" "Hanasaari" "Hanasaarenranta 1"
           "Hanaholmsstranden 1" "Espoo" "Esbo" "CityBike Finland" "10"
           "24.840319" "60.16582"])
         nil))
  (t/is (= (csv/process-station-row
          ["1" nil "Hanasaari" "Hanaholmen" "Hanasaari" "Hanasaarenranta 1"
           "Hanaholmsstranden 1" "Espoo" "Esbo" "CityBike Finland" "10"
           "24.840319" "60.16582"])
         nil))
  (t/is (= (csv/process-station-row
          ["1" "501" "" "Hanaholmen" "Hanasaari" "Hanasaarenranta 1"
           "Hanaholmsstranden 1" "Espoo" "Esbo" "CityBike Finland" "10"
           "24.840319" "60.16582"])
         nil))
  (t/is (= (csv/process-station-row
          ["1" "501" "Hanasaari" "" "Hanasaari" "Hanasaarenranta 1"
           "Hanaholmsstranden 1" "Espoo" "Esbo" "CityBike Finland" "10"
           "24.840319" "60.16582"])
         nil))
  (t/is (= (csv/process-station-row
          ["1" "501" "Hanasaari" "Hanaholmen" "" "Hanasaarenranta 1"
           "Hanaholmsstranden 1" "Espoo" "Esbo" "CityBike Finland" "10"
           "24.840319" "60.16582"])
         nil))
  (t/is (= (csv/process-station-row
          ["1" "501" "Hanasaari" "Hanaholmen" "Hanasaari" ""
           "Hanaholmsstranden 1" "Espoo" "Esbo" "CityBike Finland" "10"
           "24.840319" "60.16582"])
         nil))
  (t/is (= (csv/process-station-row
          ["1" "501" "Hanasaari" "Hanaholmen" "Hanasaari" "Hanasaarenranta 1"
           "" "Espoo" "Esbo" "CityBike Finland" "10"
           "24.840319" "60.16582"])
         nil))
  (t/is (= (csv/process-station-row
          ["1" "501" "Hanasaari" "Hanaholmen" "Hanasaari" "Hanasaarenranta 1"
           "Hanaholmsstranden 1" "" "Esbo" "CityBike Finland" "10"
           "24.840319" "60.16582"])
         nil))
  (t/is (= (csv/process-station-row
          ["1" "501" "Hanasaari" "Hanaholmen" "Hanasaari" "Hanasaarenranta 1"
           "Hanaholmsstranden 1" "Espoo" "" "CityBike Finland" "10"
           "24.840319" "60.16582"])
         nil))
  (t/is (= (csv/process-station-row
          ["1" "501" "Hanasaari" "Hanaholmen" "Hanasaari" "Hanasaarenranta 1"
           "Hanaholmsstranden 1" "Espoo" "Esbo" "" "10"
           "24.840319" "60.16582"])
         nil))
  (t/is (= (csv/process-station-row
          ["1" "501" "Hanasaari" "Hanaholmen" "Hanasaari" "Hanasaarenranta 1"
           "Hanaholmsstranden 1" "Espoo" "Esbo" "CityBike Finland" nil
           "24.840319" "60.16582"])
         nil))
  (t/is (= (csv/process-station-row
          ["1" "501" "Hanasaari" "Hanaholmen" "Hanasaari" "Hanasaarenranta 1"
           "Hanaholmsstranden 1" "Espoo" "Esbo" "CityBike Finland" "10"
           nil "60.16582"])
         nil))
  (t/is (= (csv/process-station-row
          ["1" "501" "Hanasaari" "Hanaholmen" "Hanasaari" "Hanasaarenranta 1"
           "Hanaholmsstranden 1" "Espoo" "Esbo" "CityBike Finland" "10"
           "24.840319" nil ])
         nil))

)
