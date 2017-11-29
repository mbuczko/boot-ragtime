# boot-ragtime

[![Clojars Project](http://clojars.org/mbuczko/boot-ragtime/latest-version.svg)](http://clojars.org/mbuczko/boot-ragtime)

Ragtime migrations with Clojure Boot build tool.

This is a simple task for [boot](https://github.com/boot-clj/boot) to generate, apply and rollback migrations with weavejester's [ragtime](https://github.com/weavejester/ragtime).

    $ boot ragtime -h
    Apply/rollback ragtime migrations

    Options:
      -h, --help                     Print this help info.
      -d, --database DATABASE        DATABASE sets database jdbc url.
      -g, --generate MIGRATION       MIGRATION sets name of generated migration.
      -m, --migrate                  Run all the migrations not applied so far.
      -r, --rollback                 Increase number of migrations to be immediately rolled back.
      -l, --list-migrations          List all migrations to be applied.
      -c, --driver-class DRIVER      DRIVER sets the JDBC driver class name to initialize.
          --directory DIRECTORY      DIRECTORY sets directory to store migrations in.
          --ragtime-version VERSION  VERSION sets the version of ragtime to use. default: 0.7.2.

To use the ragtime task, require it in `build.boot`:

    (require '[mbuczko.boot-ragtime :refer [ragtime]])

## Examples

To generate brand new migration (up- and down-files located in migrations/ folder):

    boot ragtime -g "add-user-table"
    
To list all the migration not applied so far:

    boot ragtime -l -d "jdbc:postgresql://localhost:5432/template1?user=postgres"
    
To apply those migrations:

    boot ragtime -m -d "jdbc:postgresql://localhost:5432/template1?user=postgres"

Finally to rollback latest migration:

    boot ragtime -r -d "jdbc:postgresql://localhost:5432/template1?user=postgres"
    
To rollback 2 latest migrations:

    boot ragtime -rr -d ...
    
To simplify those commands you may set task option in build.boot:

    (task-options!
     ragtime {:database "jdbc:postgresql://localhost:5432/template1?user=postgres"})
   
and now, you may omit -d ... option from command line.

## JDBC Driver

If you see an exception like `java.sql.SQLException: No suitable driver found`,
you must explicitly pass the class name of the JDBC driver you're using.  For
postgres, this is `org.postgresql.Driver`.

To apply migrations using a specified driver name, you might do:

    boot ragtime -m -c org.postgresql.Driver -d "jdbc:postgresql://localhost:5432/template1?user=postgres"

## Ragtime Version

Currently the default version of ragtime that is used is `0.7.2`. If you need to use another version, this
can be specified using the `ragtime-version` option.
