(defproject fun.mike/syrup-alpha "0.0.2-SNAPSHOT"
  :description "Flat file parsing and validation library."
  :url "https://github.com/mike706574/syrup"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/spec.alpha "0.1.134"]
                 [fun.mike/pancake "0.0.16"]
                 [fun.mike/tailor "0.0.18"]]
  :profiles {:dev {:source-paths ["dev"]
                   :target-path "target/dev"
                   :dependencies [[org.clojure/clojure "1.9.0-beta2"]
                                  [org.clojure/tools.namespace "0.2.11"]]}}
  :repositories [["releases" {:url "https://clojars.org/repo"
                              :creds :gpg}]]
  :repl-options {:init-ns user})
