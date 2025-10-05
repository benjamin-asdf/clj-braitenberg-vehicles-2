(ns backend.db
  (:require
   [camel-snake-kebab.core :as csk]
   [next.jdbc.prepare :as prepare]
   [next.jdbc.result-set :as rs]
   [redelay.core :refer [state status stop]]
   [clojure.tools.logging :as log]
   [next.jdbc.date-time]
   [hikari-cp.core :as hikari-cp]
   [backend.config :refer [config]])
  (:import [java.sql PreparedStatement]))

;; TODO: kebab-case?
(def opts
  "Default next.jdbc options."
  {:builder-fn rs/as-unqualified-kebab-maps
   :column-fn csk/->snake_case})

(extend-protocol prepare/SettableParameter
  clojure.lang.Keyword
  (set-parameter [k ^PreparedStatement s i]
    (.setObject s i (name k))))

(defn init-db [jdbc-options]
  (log/infof "Starting DB pool, JDBC config is %s" (pr-str (assoc jdbc-options :password "***")))
  ;; receive java.time.Instant from JDBC
  (next.jdbc.date-time/read-as-instant)
  (hikari-cp/make-datasource jdbc-options))

(def db
  (state
    :start
    (init-db (:database/pool @config))
    :name backend/db
    :stop
    (log/infof "Closing datasource...")
    (hikari-cp/close-datasource this)))

;; ----------------
