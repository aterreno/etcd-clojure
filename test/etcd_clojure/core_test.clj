(ns etcd-clojure.core-test
  (:require [clojure.test :refer :all]
            [etcd-clojure.core :as etcd]))

(deftest test-set
  (testing "should set a value"
    (is (= "bar" (get (etcd/set "foo" "bar") "value")))))

(deftest test-delete
  (testing "should delete a value"
    (etcd/set "foo" "bar")
    (is (= "DELETE" (get (etcd/delete "foo") "action")))))

(deftest test-get
  (testing "should get a value"
    (etcd/set "foo" "bar")
    (is (= "bar" (get (etcd/get "foo") "value")))))
