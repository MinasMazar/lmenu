(defproject lmenu "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [clj-shell "0.2.0"]
                 [org.clojure/clojure "1.8.0"]
                 ]
  :main ^:skip-aot lmenu.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [ [ lein-watch "0.0.2" ] ]
  :watch {
          :rate 500 ;; check file every 500ms ('watchtower' is used internally)
          :watchers {
                     :compile {
                               :watch-dirs ["src"]
                               :file-patterns [#"\.clj"]
                               :tasks ["compile"]}}}
  )
