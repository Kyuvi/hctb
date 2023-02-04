(defproject hctb "0.1.0-SNAPSHOT"
  :description "Helsiki City Bike App"
  :url "https://github.com/Kyuvi/hctb"
  :license {:name "GPL-3.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.html" }
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.csv "0.1.4"]
                 [org.clojure/java.jdbc "0.7.10"]
                 [org.postgresql/postgresql "42.2.5"]
                 [clojure.java-time "0.3.2"]
                 ]
  :main ^:skip-aot hctb.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
