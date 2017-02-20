(ns postafer.tools)

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
