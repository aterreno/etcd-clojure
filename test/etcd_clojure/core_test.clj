(ns etcd-clojure.core-test
  (:require [clojure.test :refer :all]
            [etcd-clojure.core :as etcd]))

(defn setup-test []
  (etcd/connect! "127.0.0.1"))
(defn teardown-test []
  (println "teardown"))

(defn once-fixtures [f]
  (setup-test)
  (try
    (f)
    (finally (teardown-test))))

(use-fixtures :once once-fixtures)

(deftest test-set
  (testing "should set a value"
    (is (= "bar" (etcd/set "foo-test" "bar")))))

(deftest test-get
  (testing "should get a value"
    (etcd/set "foo-test" "bar")
    (is (= "bar" (etcd/get "foo-test")))))

(deftest test-delete
  (testing "should delete a value"
    (etcd/set "foo-test" "bar")
    (is (= "delete" (get (etcd/delete "foo-test") "action")))))
