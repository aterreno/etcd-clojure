(defproject etcd-clojure "0.2.1-SNAPSHOT"
  :description "etcd client library in clojure"
  :url "https://github.com/aterreno/etcd-clojure"
  :lein-release {:deploy-via :clojars}
  :license {:name "Apache license version 2"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :scm {:url "git@github.com:aterreno/etcd-clojure.git"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [cheshire "5.4.0"]
                 [clj-http "1.0.1"]])
