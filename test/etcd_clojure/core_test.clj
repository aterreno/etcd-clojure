(ns etcd-clojure.core-test
  (:require [clojure.test :refer :all]
            [etcd-clojure.core :as etcd]))

(defn setup-test []
  (let [connect (etcd/connect! "127.0.0.1")]
    (is (= "http://127.0.0.1:4001") (first connect))
    (is (= "http://127.0.0.1:7001") (last connect))))

(defn teardown-test []
  (println "teardown"))

(defn once-fixtures [f]
  (setup-test)
  (try
    (f)
    (finally (teardown-test))))

(defn setup [])

(defn teardown []
  (try
    (etcd/delete-dir-recur "test-dir")
    (etcd/delete-dir-recur "queue")
    (catch Exception e)))

(defn each-fixture [f]
  (setup)
  (f)
  (teardown))

(use-fixtures :once once-fixtures)
(use-fixtures :each each-fixture)

(deftest test-version
  (testing "should get some value from the server: note hardcoded version"
    (is (= "etcd 0.4.5" (etcd/version)))))

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

(deftest test-create-dir
  (testing "should create a directory"
    (is (= "/test-dir" (etcd/create-dir "test-dir")))))

(deftest test-delete-dir
  (testing "should delete a directory"
    (etcd/create-dir "test-dir")
    (is (= "/test-dir" (etcd/delete-dir "test-dir")))))

(deftest test-delete-dir-recur
  (testing "should delete a directory recursively"
    (etcd/create-dir "test-dir")
    (etcd/set "test-dir/test-val-on-dir" "bar")
    (is (= "/test-dir" (etcd/delete-dir-recur "test-dir")))))

(deftest test-create-and-list-in-order
  (testing "should be able to create and retrieve values in order"
    (etcd/create-in-order "queue" "1")
    (etcd/create-in-order "queue" "2")
    (etcd/create-in-order "queue" "3")
    (is (= ["1" "2" "3"] (etcd/list-in-order "queue")))))

(deftest test-list-directory
  (testing "should list the content of a directory"
    (etcd/create-dir "test-dir")
    (etcd/set "test-dir/test-val1-on-dir" "1")
    (etcd/set "test-dir/test-val2-on-dir" "2")
    (etcd/set "test-dir/test-val3-on-dir" "3")
    (is (= 3
           (count (etcd/list "test-dir"))))))

(deftest test-stats
  (testing "should retrive stats"
    (is (and
         (contains? (etcd/stats) "leader")
         (contains? (etcd/stats) "followers")))))

(deftest test-self-stats
  (testing "should retrive self stats"
    (is (=
         ["name" "state" "startTime" "leaderInfo" "recvAppendRequestCnt" "sendAppendRequestCnt"]
         (keys (etcd/self-stats))))))

(deftest test-store-stats
  (testing "should retrive store stats"
    (is (not (nil?
              (etcd/store-stats))))))

(deftest test-machines
  (testing "should retrive machines"
    (is (= 1
           (count (etcd/machines))))))

(deftest test-config
  (testing "should retrive config"
    (is (= 3
           (count (keys (etcd/config)))))))
