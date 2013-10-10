(ns etcd-clojure.core)
(:require '[clj-http.client :as client])

(def ^:private endpoint (atom "http://127.0.0.1:4001"))

(def ^:private api-version "v1")

(def ^:private base-url
  (str @endpoint "/" api-version))

(defn connect!
  [etcd-server-uri]
  (reset! endpoint etcd-server-uri))

(defn- build-url
  [& {:keys [key value ttl]}]
  (cond-> base-url
          key (str "/keys/" key)
          value (str "?value=" value)
          ttl (str "?ttl=" ttl)))

(defn set
  [key value & {:keys [ttl]}]
  (client/put (build-url :key key :value value :ttl ttl)))

(defn get
  [key]
  (client/get (build-url :key key)))

(defn delete
  [key]
  (client/delete (build-url :key key)))
