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

(defn bigrams
  [input]
  (set  (map (partial reduce str)
             (partition 2 1 input))))

(defn similarity
  [str1 str2]
  (let [bigrams1 (bigrams str1)
        bigrams2 (bigrams str2)
        intersection (clojure.set/intersection bigrams1 bigrams2)
        union (clojure.set/union bigrams1 bigrams2)]
    (try  (/ (count  intersection)
             (count  union))
          (catch #?(:clj Exception :cljs js/Error) e 0))))

(defn are-close-within?
  [threshold str1 str2]
  (>= (similarity str1 str2 )
      threshold))

(defn find-first
  [f coll]
  (first (filter f coll)))

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
  
