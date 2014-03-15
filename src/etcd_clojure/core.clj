(ns etcd-clojure.core
  (:require [clj-http.client :as client])
  (:require [cheshire.core :refer :all])
  (:refer-clojure :exclude [get set]))

(def ^:private endpoint (atom "http://127.0.0.1:4001"))

(def ^:private api-version "v1")

(defn ^:private base-url
  []
  (str @endpoint "/" api-version))

(defn connect!
  [etcd-server-uri]
  (reset! endpoint etcd-server-uri))

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
  [key value & {:keys [ttl prev-val]}]
  (try (parse-string (:body (client/put (build-url :key key :value value :ttl ttl :prev-val prev-val))))
       (catch Exception e
         (parse-string (get-in (.getData e) [:object :body])))))

(defn create
  [key value & {:keys [ttl prev-val]}]
  (try (parse-string (:body (client/post (build-url :key key :value value :ttl ttl :prev-val prev-val))))
       (catch Exception e
         (parse-string (get-in (.getData e) [:object :body])))))

(defn get
  [key]
  (let [json (parse-string (:body (client/get (build-url :key key))))
        value (clojure.core/get json "value")]
    (if value
      value
      (map #(last (clojure.string/split (clojure.core/get % "key" json) #"/")) json))))

(defn delete
  [key]
  (parse-string (:body (client/delete (build-url :key key)))))

(defn watch
  [key callback]
  (let [f (future (parse-string (:body (client/get (str (base-url) "/watch/" key)))))]
    (when-done f #(callback %)) f))

(defn machines
  []
  (parse-string (:body (client/get (build-url :key "_etcd/machines")))))

(defn leader
  []
  (:body (client/get (str (base-url) "/leader"))))
