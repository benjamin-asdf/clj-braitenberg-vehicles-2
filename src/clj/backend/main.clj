(ns backend.main
  (:require
   [clojure.tools.logging :as log]
   [cognitect.transit]
   [redelay.core :refer [state status stop]]
   [reitit.ring.middleware.exception]
   [backend.config]
   [backend.server]
   [backend.db]))

(defn run-system []
  (try
    (let [initialized-system
          {:config @backend.config/config
           :db @backend.db/db
           :server @backend.server/server}]
      (.addShutdownHook (Runtime/getRuntime)
                        (Thread. (fn []
                                   (log/infof "Shutting down")
                                   (stop))))
      (log/infof "System started")
      initialized-system)
    (catch Throwable t
      (log/error t "Failed to start system"))))

(defn -main [] (run-system))

(comment
  (-main)




  )
