(defproject com.firstlinq/om-ssr-node "0.1.1-SNAPSHOT"
            :description "OM Server Side Rendering for NodeJS"
            :url "http://github.com/rukor/om-ssr-node"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                           [org.clojure/clojurescript "0.0-3269"]
                           [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                           [org.omcljs/om "0.8.8"]
                           [com.domkm/silk "0.0.4" :scope "provided"]
                           [com.cognitect/transit-cljs "0.8.220" :scope "provided"]
                           ]
            :node-dependencies [[source-map-support "0.2.8"]
                                [express "4.12.4"]
                                [plates "0.4.11"]
                                [react "0.12.2"]
                                [st "0.5.4"]]

            :jar-exclusions [#"\.cljx|\.swp|\.swo|\.DS_Store"]
            :global-vars {*warn-on-reflection* true})
