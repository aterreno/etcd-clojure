(ns etcd-clojure.core)
(:require '[clj-http.client :as client])

(defn set
  [key value]
  (client/put (str "http://127.0.0.1:4001/v1/keys/" key "?value=" value)))

(defn get
  [key]
  (client/get (str "http://127.0.0.1:4001/v1/keys/" key)))

(defn delete
  [key]
  (client/delete (str "http://127.0.0.1:4001/v1/keys/" key)))
