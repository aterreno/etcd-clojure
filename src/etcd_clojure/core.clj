(ns etcd-clojure.core
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

(defn- when-done [future-to-watch function-to-call]
  (future (function-to-call @future-to-watch)))

(defmacro get-json
  ([method base]
     `(parse-string (:body (~method (str ~base)))))
  ([method base path]
     `(parse-string (:body (~method (str ~base ~path))))))

(defmacro send-json
  ([method base]
     `(parse-string (:body (~method ~base))))
  ([method base data]
     `(parse-string (:body (~method ~base ~data)))))

(defn compose-params
  [& {:keys [ttl prev-value prev-index prev-exist dir val]}]
  (cond->
   {}
   val (assoc :value val)
   dir (assoc :dir dir)
   ttl (assoc :ttl ttl)
   prev-value (assoc :prevValue prev-value)
   prev-index (assoc :prevIndex prev-index)
   (not (nil? prev-exist)) (assoc :prevExist prev-exist)))

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
  [key val & {:keys [ttl prev-value prev-index prev-exist]}]
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
  [key]
  (get-in (get-json http/get (str (base-url) "/keys/" key)) ["node" "value"]))

(defn delete
  "Deletes a value"
  [key]
  (send-json http/delete (str (base-url) "/keys/" key)))

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
  [key & {:keys [ttl prev-value prev-index prev-exist]}]
  (let [params (compose-params
                :ttl ttl
                :prev-value prev-value
                :prev-index prev-index
                :prev-exist prev-exist
                :dir true)
        url (str (base-url) "/keys/" key)]
    (get-in (send-json http/put url {:form-params params}) ["node" "key"])))

(defn watch
  [key callback]
  (let [f (future (get-json http/get (str (base-url) "/watch/" key)))]
    (when-done f #(callback %)) f))

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
