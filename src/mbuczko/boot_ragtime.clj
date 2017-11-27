(ns mbuczko.boot-ragtime
  {:boot/export-tasks true}
  (:require
   [boot.pod    :as pod]
   [boot.core   :as core]
   [boot.util   :as util]))


(core/deftask ragtime
  "Apply/rollback ragtime migrations"
  [d database  DATABASE      str  "database jdbc url"
   g generate  MIGRATION     str  "name of generated migration."
   m migrate                 bool "Run all the migrations not applied so far."
   r rollback                int  "number of migrations to be immediately rolled back."
   l list-migrations         bool "List all migrations to be applied."
   c driver-class  DRIVER    str  "The JDBC driver class name to initialize."
   _ directory DIRECTORY     str  "directory to store migrations in."
   _ ragtime-version VERSION str  "The version of ragtime to use. default: 0.7.2"]

  (let [worker  (pod/make-pod (update-in (core/get-env) [:dependencies] into [['ragtime/ragtime ragtime-version]]))
        command (if rollback :rollback (if migrate :migrate))
        migrations-dir (or directory "migrations")
        ragtime-version (or ragtime-version "0.7.2")]

    (core/with-pre-wrap [fs]
      (if generate
        (let [curr (.format (java.text.SimpleDateFormat. "yyyyMMddHHmmss") (java.util.Date.))
              name (str migrations-dir "/" curr "-" generate)]
          (spit (str name ".up.sql") "-- migration to be applied\n\n")
          (spit (str name ".down.sql") "-- rolling back recipe\n\n")

          (util/info "Creating %s\n" name)))

      (if (or list-migrations command)
        (if-not database
          (util/info "No database set\n")
          (pod/with-eval-in worker
            ~(if driver-class (Class/forName driver-class))
            (require '[ragtime.jdbc :as jdbc]
                     '[ragtime.repl :as repl])

            (let [migrations (jdbc/load-directory ~migrations-dir)]
              (if ~list-migrations
                (doseq [m migrations]
                  (println (:id m)))
                (let [config {:datastore (jdbc/sql-database ~database)
                              :migrations migrations}]
                  (case ~command
                    :migrate (repl/migrate config)
                    :rollback (repl/rollback config ~rollback))))))))
      fs)))
