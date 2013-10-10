(ns etcd-clojure.core)
(:require '[clj-http.client :as client])

(def endpoint (atom "http://127.0.0.1:4001"))

(def api-version "v1")

(defn connect!
  [etcd-server-uri]
  (reset! endpoint etcd-server-uri))

(def base-url
  (str @endpoint "/" api-version))

(defn set
  [key value]
  (client/put (str base-url "/keys/" key "?value=" value)))

(defn get
  [key]
  (client/get (str base-url "/keys/" key)))

(defn delete
  [key]
  (client/delete (str base-url "/keys/" key)))
