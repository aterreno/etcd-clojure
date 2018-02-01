(defproject etcd-clojure "0.2.4"
  :description "etcd client library in clojure"
  :url "https://github.com/aterreno/etcd-clojure"
  :lein-release {:deploy-via :clojars}
  :license {:name "Apache license version 2"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :scm {:url "git@github.com:aterreno/etcd-clojure.git"}
  :main etcd-clojure.core
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [cheshire "5.8.0"]
                 [clj-http "3.7.0"]])
