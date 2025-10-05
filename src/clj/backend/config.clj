(ns backend.config
  (:require
   [aero.core :as aero]
   [redelay.core :refer [state status stop]]
   [clojure.tools.logging :as log]
   [clojure.java.io :as io]))

(def config
  (state
    (log/info "Loading config...")
    (aero/read-config (io/resource "config.edn"))))
