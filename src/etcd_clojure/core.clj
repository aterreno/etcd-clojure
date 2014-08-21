(ns etcd-clojure.core
  (:require [clj-http.client :as client])
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
  [etcd-server-host]
  (reset! endpoint (format "http://%s:4001" etcd-server-host))
  (reset! admin-endpoint (format "http://%s:7001" etcd-server-host)))

(defn- when-done [future-to-watch function-to-call]
  (future (function-to-call @future-to-watch)))

(defn set
  "Sets a vaue to key, optional param :ttl"
  [key val & {:keys [ttl]}]
  (let [data {:value val}
        url (str (base-url) "/keys/" key)]
    (if ttl
      (clojure.core/get-in (parse-string (:body (client/put url {:form-params (assoc data :ttl ttl)}))) ["node" "value"])
      (clojure.core/get-in (parse-string (:body (client/put url {:form-params data}))) ["node" "value"]))))

(defn get
  "Gets a value"
  [key]
  (clojure.core/get-in (parse-string (:body (client/get (str (base-url) "/keys/" key)))) ["node" "value"]))

(defn delete
  "Deletes a value"
  [key]
  (parse-string (:body (client/delete (str (base-url) "/keys/" key)))))

(defn delete-dir
  "Deletes a dir"
  [key]
  (parse-string (:body (client/delete (str (base-url) "/keys/" key "?dir=true")))))

(defn delete-dir-recur
  "Deletes a directory recursively"
  [key]
  (parse-string (:body (client/delete (str (base-url) "/keys/" key "?dir=true&recursive=true")))))

(defn create-in-order
  "Creates in order"
  [key value]
  (let [url (str (base-url) "/keys/" key)]
    (clojure.core/get-in (parse-string (:body (client/post url {:form-params {:value value}}))) ["node" "value"])))

(defn list
  "Lists the content of a directory"
  [key & {:keys [recursive]}]
  (let [url (str (base-url) "/keys/" key "?recursive=" recursive)]
    (clojure.core/get-in (parse-string (:body (client/get url ))) ["node"])))

(defn list-in-order
  "Lists the content of a directory recursively and in order"
  [key]
  (let [url (str (base-url) "/keys/" key "?recursive=true&sorted=true")]
    (map #(clojure.core/get % "value") (clojure.core/get-in (parse-string (:body (client/get url ))) ["node" "nodes"]))))

(defn create-dir
  "Creates a dir"
  [key & {:keys [ttl]}]
  (let [data {:value val :dir true}
        url (str (base-url) "/keys/" key)]
    (if ttl
      (clojure.core/get-in (parse-string (:body (client/put url {:form-params (assoc data :ttl ttl)}))) ["node" "key"])
      (clojure.core/get-in (parse-string (:body (client/put url {:form-params data}))) ["node" "key"]))))

(defn watch
  [key callback]
  (let [f (future (parse-string (:body (client/get (str (base-url) "/watch/" key)))))]
    (when-done f #(callback %)) f))

(defn stats
  "Leader stats"
  []
  (parse-string (:body (client/get (str (base-url) "/stats/leader")))))

(defn self-stats
  "Self Stats"
  []
  (parse-string(:body (client/get (str (base-url) "/stats/self")))))

(defn store-stats
  "Store stats"
  []
  (parse-string (:body (client/get (str (base-url) "/stats/store")))))

(defn machines
  "Machines"
  []
  (parse-string (:body (client/get (str (admin-base-url) "/admin/machines")))))

(defn config
  "Gets the config"
  []
  (parse-string (:body (client/get (str (admin-base-url) "/admin/config")))))

(defn version
  "Gets the etcd server version"
  []
  (:body (client/get (str @endpoint "/version"))))
