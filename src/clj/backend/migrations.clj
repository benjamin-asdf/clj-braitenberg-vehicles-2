(ns backend.migrations
  (:require
   [backend.main :as main]
   [backend.db]
   [redelay.core :refer [state status stop]]
   [migratus.core :as migratus]))

(defn migratus-config
  []
  {:store                :database
   :migration-dir        "resources/db/migrations/"
   :init-in-transaction? true
   :migration-table-name "migrations"
   :db                   {:datasource @backend.db/db}})

(defn run-migration-system
  [migration-function]
  (try (migration-function (migratus-config))
       (finally (stop))))

(defn create-migration [name type]
  (let [type-kw (or (keyword type) :sql)]
    (migratus/create (migratus-config) (or name "unknown") type-kw)))

(defn -main [& args]
  (let [command (first args)]
    (case command
      "init"    (run-migration-system migratus/init)
      "migrate" (run-migration-system migratus/migrate)
      "create"  (let [[name type] (rest args)]
                  (create-migration name type))
      (throw (ex-info (str "Unknown command \"" command "\" Available commands are: init, migrate") {})))))

(comment
  (run-migration-system migratus/migrate))
