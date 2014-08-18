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

(defn- build-url
  [& {:keys [key value ttl prev-val]}]
  (cond-> (base-url)
          key (str "/keys/" key)
          value (str "?value=" value)
          prev-val (str "&prevValue=" prev-val)
          ttl (str "&ttl=" ttl)))

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
  [key]
  (parse-string (:body (client/delete (str (base-url) "/keys/" key)))))

(defn delete-dir
  [key]
  (parse-string (:body (client/delete (str (base-url) "/keys/" key "?dir=true")))))

(defn delete-dir-recur
  [key]
  (parse-string (:body (client/delete (str (base-url) "/keys/" key "?dir=true&recursive=true")))))

(defn watch
  [key callback]
  (let [f (future (parse-string (:body (client/get (str (base-url) "/watch/" key)))))]
    (when-done f #(callback %)) f))

(defn stats
  ""
  []
  (parse-string (:body (client/get (str (base-url) "/stats/leader")))))

(defn self-stats
  ""
  []
  (parse-string(:body (client/get (str (base-url) "/stats/self")))))

(defn store-stats
  ""
  []
  (parse-string (:body (client/get (str (base-url) "/stats/store")))))

(defn machines
  []
  (println (str (admin-base-url) api-version "/admin/machines"))
  (parse-string (:body (client/get (str (admin-base-url) "/admin/machines")))))

(defn get-config
  ""
  []
  (parse-string (:body (client/get (str (admin-base-url) "/admin/config")))))

(defn get-version
  ""
  []
  (:body (client/get (str @endpoint "/version"))))
