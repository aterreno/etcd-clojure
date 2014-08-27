(ns etcd-clojure.util
  (:require [cheshire.core :refer :all]))


(defn when-done [future-to-watch function-to-call]
  (future (function-to-call @future-to-watch)))

(defn compose-query-string [m]
  (let [m (into {} (filter second m))]
    (->> (for [[k v] m]
           (str (name k) "=" v))
         (interpose "&")
         (apply str))))

(defn compose-params
  [& {:keys [ttl prev-value prev-index prev-exist dir val wait]}]
  (cond->
   {}
   val (assoc :value val)
   dir (assoc :dir dir)
   ttl (assoc :ttl ttl)
   prev-value (assoc :prevValue prev-value)
   prev-index (assoc :prevIndex prev-index)
   (not (nil? prev-exist)) (assoc :prevExist prev-exist)
   (not (nil? wait)) (assoc :wait wait)))

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
