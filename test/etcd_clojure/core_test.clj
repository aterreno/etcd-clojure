(ns etcd-clojure.core-test
  (:require [clojure.test :refer :all]
            [etcd-clojure.core :as etcd]))

(defn setup-test []
  (etcd/connect! "http://127.0.0.1:4001"))
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
    (is (= "bar" (get (etcd/set "foo" "bar") "value")))))

(deftest test-create
  (testing "should set a value"
    (is (= "bar" (get (etcd/create "foo" "bar") "value")))))

(deftest test-delete
  (testing "should delete a value"
    (etcd/set "foo" "bar")
    (is (= "delete" (get (etcd/delete "foo") "action")))))

(deftest test-get
  (testing "should get a value"
    (etcd/set "foo" "bar")
    (is (= "bar" (etcd/get "foo")))))
