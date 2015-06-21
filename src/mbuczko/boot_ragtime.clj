(ns mbuczko.boot-ragtime
  {:boot/export-tasks true}
  (:require
   [boot.pod    :as pod]
   [boot.core   :as core]
   [boot.util   :as util]))

(def ^:private rag-deps '[[ragtime/ragtime.core "0.3.8"]
                          [ragtime/ragtime.sql "0.3.8"]
                          [ragtime/ragtime.sql.files "0.3.8"]])

(core/deftask ragtime
  "Apply/rollback ragtime migrations"
  [d database  DATABASE   str  "database jdbc url"
   g generate  MIGRATION  str  "name of generated migration."
   m migrate              bool "Run all the migrations not applied so far."
   r rollback             int  "number of migrations to be immediately rolled back."
   l list-migrations      bool "List all migrations to be applied."
   c driver-class         str  "The JDBC driver class name to initialize."
   _ directory DIRECTORY  str  "directory to store migrations in."]

  (let [worker  (pod/make-pod (update-in (core/get-env) [:dependencies] into rag-deps))
        command (if rollback :rollback (if migrate :migrate))
        migrations-dir (or directory "migrations")]
    (core/with-pre-wrap [fs]
      (if generate
        (let [curr (.format (java.text.SimpleDateFormat. "yyyyMMddhhmmss") (java.util.Date.))
              name (str migrations-dir "/" curr "-" generate)]
          (spit (str name ".up.sql") "-- migration to be applied\n\n")
          (spit (str name ".down.sql") "-- rolling back receipe\n\n")

          (util/info "Creating %s\n" name)))

      (if (or list-migrations command)
        (if-not database
          (util/info "No database set\n")
          (pod/with-eval-in worker
            ~(if driver-class (Class/forName driver-class))
            (require 'ragtime.main 'ragtime.sql.files)

            (if ~list-migrations
              (doseq [m (ragtime.sql.files/migrations migrations-dir)] (println (:id m)))
              (let [migrate-fn #(ragtime.sql.files/migrations migrations-dir)
                    options {:database ~database :migrations 'migrate-fn}]
                (case ~command
                  :migrate (ragtime.main/migrate options)
                  :rollback (ragtime.main/rollback options (str ~rollback))))))))
      fs)))
