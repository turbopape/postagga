;; Copyright(c) 2017 - [Rafik Naccache](rafik@fekr.tech)
(ns postagga.tools
  (require [clojure.edn :as edn]))

(defn get-column
  "Given a matrix represented by a map {[i j] x}, produces the column such as j = column  "
  [matrix column]
  (->> matrix
       (filter #(= (get (key %) 1) column))
       (into {})))

(defn get-row
  "Given a matrix represented by a map {[i j] x}, produces the column such as j = column  "
  [matrix column]
  (->> matrix
       (filter #(= (get (key %) 0) column))
       (into {})))


(def get-column-m (memoize get-column))

(defn load-resource-as-str
  [resource]
  (with-open [rdr (clojure.java.io/reader (clojure.java.io/resource resource))]
    (loop [lines (line-seq rdr)
           txt ""]
      (if (seq lines)
        (recur (rest lines)
               (str txt (first lines)))
        txt))))

(defn load-edn-from-resource
  [resource]
  (let [resource-str (load-resource-as-str resource)]
    (edn/read-string resource-str)))
  
