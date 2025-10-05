(ns backend.server
  (:require
   [camel-snake-kebab.core :as csk]
   [next.jdbc.prepare :as prepare]
   [next.jdbc.result-set :as rs]
   [redelay.core :refer [state status stop]]
   [clojure.tools.logging :as log]
   [next.jdbc.date-time]
   [ring.adapter.jetty :as jetty]
   [hikari-cp.core :as hikari-cp]
   [backend.routes :refer [routes]]
   [backend.config :refer [config]]))

(defn init-server [routes {:keys [port] :as jetty-opts}]
  (log/infof "Starting Jetty server on http://localhost:%s" port)
  (jetty/run-jetty routes (-> jetty-opts (dissoc :handler) (assoc :join? false))))

(def server
  (state
    :start
    (init-server
     @routes
     (:adapter/jetty @config))
    :stop
    (log/infof "Stopping Jetty")
    (.stop this)))
