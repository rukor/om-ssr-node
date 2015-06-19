(ns com.firstlinq.om-ssr.routes
  #?(:cljs (:require-macros [cljs.core.async.macros :refer [go]]))
  #?(:cljs (:require [cljs.core.async :refer [<! chan put!]]
             [com.firstlinq.om-ssr :refer [redirect-key]])))

(defn- ->service-vector [service-id]
  (if (vector? service-id)
    [(first service-id)
     (second service-id)
     (second (rest service-id))]
    [service-id nil nil]))

(defn- request-vecs [service-calls]
  (for [[data-key service-id] (partition 2 service-calls)
        :let [[svc-call-id param txform] (->service-vector service-id)]]
    [data-key svc-call-id param txform]))

(defn- make-req-map [state-sym service-reqs]
  (reduce
    (fn [accum [data-key service-call param txform]]
      (->> `[(com.firstlinq.om-ssr.api/handle-request ~state-sym ~service-call ~param) ~txform]
           (assoc accum data-key)))
    {}
    service-reqs))

(defn- make-redirect-check [state-sym opts]
  `(when-let [r-fn# (:redirect? ~opts)]
     (r-fn# ~state-sym)))

(defn- add-title-to-state [title state]
  (cond-> state
          (fn? title)
          (assoc-in [:route :title] (title state))

          (string? title)
          (assoc-in [:route :title] title)))

#?(:cljs
   (defn- append-return-uri [uri return-uri]
     (when uri
       (let [return-uri (js/encodeURIComponent return-uri)]
         (if (not= -1 (.indexOf uri "?"))
           (str uri "&redirect=" return-uri)
           (str uri "?redirect=" return-uri))))))

#?(:cljs
   (defn create-route-handler [state]
     (fn [id params]
       (let [params (->> (get-in params [:domkm.silk/url :query])
                         (assoc params :query))
             uri    (str (:domkm.silk/url params))]
         (go
           (when-let [ch (com.firstlinq.om-ssr.state/get-state @state id params {})]
             (let [new-state (<! ch)]
               (if-let [location (redirect-key new-state)]
                 (do (set! (.-location js/window) (append-return-uri location uri)))
                 (do (swap! state merge new-state)
                     (when-let [title (get-in new-state [:route :title])]
                       (set! (.-title js/document) title)))))))))))


#?(:cljs
   (defn reify-map
     [init req-map route title opts]
     (let [ch (chan)]
       (go
         (loop [accum (merge init {:route route})
                items req-map]
           (if-let [item (first items)]
             (let [[k [v txform]] item
                   res (<! v)
                   res (if txform (txform (:body res)) (:body res))
                   res (assoc accum k res)]
               (recur res (rest items)))
             (put! ch (add-title-to-state title accum)))))
       ch)))


#?(:clj
   (defmacro defroute
     [route-id title args & service-calls]
     (let [[opts service-calls] (if (map? (first service-calls))
                                  [(first service-calls) (rest service-calls)]
                                  [nil service-calls])
           state-sym    (gensym "state")
           service-reqs (request-vecs service-calls)
           req-map      (make-req-map state-sym service-reqs)]
       `(defmethod com.firstlinq.om-ssr.state/get-state ~route-id
          [~state-sym _# params# opts#]
          (let [{:keys ~args} params#
                redirect-uri# ~(make-redirect-check state-sym opts)
                init-state#   (cond-> ~state-sym redirect-uri# ; init state
                                      (assoc com.firstlinq.om-ssr/redirect-key redirect-uri#))
                req-map#      (if-not redirect-uri# ~req-map {})
                route#        {:id ~route-id :params params#}]
            (com.firstlinq.om-ssr.routes/reify-map init-state# req-map# route# ~title ~opts))))))
