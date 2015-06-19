(ns com.firstlinq.om-ssr.util
  (:require [cljs.nodejs :as node]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn make-om-renderer [root-component router]
  (fn [state]
    (->> (om/build root-component state {:opts {:router router}})
         (dom/render-to-str))))

(def ->ring-req
  (let [url (node/require "url")]
    (fn [req]
      (let [parsed       (.parse url (.-url req))
            parsed       (.parse js/JSON (.stringify js/JSON parsed))
            {uri "pathname" query "search"} (js->clj parsed)
            headers      (js->clj (.-headers req))
            conn         (.-connection req)
            address      (js->clj (.address conn))
            peer-cert-fn (.-getPeerCertificate conn)
            ring-req     {:server-port        (address "port")
                          :server-name        (address "address")
                          :remote-addr        (.-remoteAddress conn)
                          :uri                uri
                          :query-string       query
                          :scheme             "http"
                          :request-method     (keyword (.toLowerCase (.-method req)))
                          :content-type       (headers "content-type")
                          :content-length     (headers "content-length")
                          :character-encoding nil
                          :ssl-client-cert    (when peer-cert-fn (peer-cert-fn))
                          :headers            headers
                          :body               req}]
        ring-req))))

