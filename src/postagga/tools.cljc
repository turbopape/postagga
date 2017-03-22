;; Copyright(c) 2017 - [Rafik Naccache](rafik@fekr.tech)
(ns postagga.tools
  #?(:clj (require [clojure.edn :as edn])))

(defn get-column
  "Given a matrix represented by a map {[i j] x}, produces the column such as j = column  "
  [matrix column]
  (->> matrix
       (filter #(= (get (key %) 1) column))
       (into {})))

(defn get-row
  "Given a matrix represented by a map {[i j] x}, produces the column such as j = column  "
  [matrix row]
  (->> matrix
       (filter #(= (get (key %) 0) row))
       (into {})))

(defn arg-max
  "gives the key that yields the maximum value"
  [coll]
  (apply max-key (into [coll] (keys coll))))



(def get-column-m (memoize get-column))
(def get-row-m (memoize get-row))
(def arg-max-m (memoize arg-max))

#?(:clj (defn load-file-as-str
          [file]
          (with-open [rdr (clojure.java.io/reader file)]
            (loop [lines (line-seq rdr)
              txt ""]
              (if (seq lines)
                (recur (rest lines)
                       (str txt (first lines)))
                txt)))))

#?(:clj (defn load-edn
          [file]
          (let [file-str (load-file-as-str file)]
            (edn/read-string file-str))))
  
