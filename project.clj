(defproject etcd-clojure "0.1.1"
  :description "etcd client library in clojure"
  :url "https://github.com/aterreno/etcd-clojure"
  :lein-release {:deploy-via :clojars}
  :license {:name "Apache license version 2"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :scm {:url "git@github.com:aterreno/etcd-clojure.git"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cheshire "5.2.0"]
                 [clj-http "0.7.7"]])
