(ns com.firstlinq.om-ssr.log)

(defn ->log-output [e]
  (if (or (coll? e) (keyword? e) (pr-str e)) e))

