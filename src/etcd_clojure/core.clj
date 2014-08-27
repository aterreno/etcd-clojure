(ns etcd-clojure.core
  (:use [etcd-clojure.util])
  (:require [clj-http.client :as http])
  (:require [cheshire.core :refer :all])
  (:refer-clojure :exclude [get set]))

(def ^:private admin-endpoint (atom "http://127.0.0.1:7001"))

(def ^:private endpoint (atom "http://127.0.0.1:4001"))

(def ^:private api-version "v2")

(defn ^:private base-url
  []
  (str @endpoint "/" api-version))

(defn ^:private admin-base-url
  []
  (str @admin-endpoint "/" api-version))

(defn connect!
  ([etcd-server-host] (connect! etcd-server-host 4001 7001))
  ([etcd-server-host port] (connect! etcd-server-host port 7001))
  ([etcd-server-host port admin-port]
     [(reset! endpoint (format "http://%s:%s" etcd-server-host port))
      (reset! admin-endpoint (format "http://%s:%s" etcd-server-host admin-port))]))

(defn version
  "Gets the etcd server version"
  []
  (:body (http/get (str @endpoint "/version"))))

(defn set
  "Sets a vaue to key, optional param :ttl"
  [key val & {:keys [ttl prev-value prev-index prev-exist wait]}]
  (let
      [params (compose-params
                :ttl ttl
                :val val
                :prev-value prev-value
                :prev-index prev-index
                :prev-exist prev-exist)
        url (str (base-url) "/keys/" key)]
    (get-in (send-json http/put url {:form-params params}) ["node" "value"])))

(defn get
  "Gets a value"
  [key & {:keys [wait callback]}]
  (let [url (str (base-url) "/keys/" key)]
    (if (nil? wait)
      (get-in (get-json http/get url) ["node" "value"])
      (let [f (future (get-in (get-json http/get (str url "?" (compose-query-string {:wait wait}))) ["node" "value"]))]
        (when-done f #(callback %)) f))
    ))

(defn delete
  "Deletes a value"
  [key & {:keys [prev-value prev-index prev-exist]}]
  (let [query-string (compose-query-string {:prevValue prev-value :prevIndex prev-index})
        url (str (base-url) "/keys/" key)]
    (if (empty? [prev-value prev-index prev-exist])
      (get-in (send-json http/delete url)["node" "key"])
      (get-in (send-json http/delete (str url "?" query-string))["node" "key"]))))

(defn delete-dir
  "Deletes a dir"
  [key]
  (get-in (send-json http/delete (str (base-url) "/keys/" key "?dir=true")) ["node" "key"]))

(defn delete-dir-recur
  "Deletes a directory recursively"
  [key]
  (get-in (send-json http/delete (str (base-url) "/keys/" key "?dir=true&recursive=true")) ["node" "key"]))

(defn create-in-order
  "Creates in order"
  [key value]
  (let [url (str (base-url) "/keys/" key)]
    (get-in (send-json http/post url {:form-params {:value value}}) ["node" "value"])))

(defn list
  "Lists the content of a directory"
  [key & {:keys [recursive]}]
  (let [url (str (base-url) "/keys/" key "?recursive=" recursive)]
    (map #(assoc {} :key (clojure.core/get % "key") :value (clojure.core/get % "value"))
         (get-in (get-json http/get url) ["node" "nodes"]))))

(defn list-in-order
  "Lists the content of a directory recursively and in order"
  [key]
  (let [url (str (base-url) "/keys/" key "?recursive=true&sorted=true")]
    (map #(clojure.core/get % "value") (get-in (get-json http/get url)  ["node" "nodes"]))))

(defn create-dir
  "Creates a dir"
  [key & {:keys [ttl]}]
  (let [params (compose-params
                :ttl ttl
                :dir true)
        url (str (base-url) "/keys/" key)]
    (get-in (send-json http/put url {:form-params params}) ["node" "key"])))

(defn stats
  "Leader stats"
  []
  (get-json http/get (base-url) "/stats/leader"))

(defn self-stats
  "Self Stats"
  []
  (get-json http/get (base-url) "/stats/self"))

(defn store-stats
  "Store stats"
  []
  (get-json http/get (base-url) "/stats/store"))

(defn machines
  "Machines"
  []
  (get-json http/get (admin-base-url) "/admin/machines"))

(defn config
  "Gets the config"
  []
  (get-json http/get (admin-base-url) "/admin/config"))
