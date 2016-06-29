(defproject inventory "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha8"]]
  :main ^:skip-aot inventory.core

  :plugins [[lein-ring "0.9.7"]
            [lein-ancient "0.6.10"]]

  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
