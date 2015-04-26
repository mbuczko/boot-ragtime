(set-env!
 :source-paths   #{"src"}
 :dependencies '[[org.clojure/clojure "1.6.0" :scope "provided"]
                 [adzerk/bootlaces "0.1.11" :scope "test"]
                 [boot/core "2.0.0-rc8"]])

(require '[adzerk.bootlaces :refer :all]
         '[mbuczko.boot-ragtime :refer [ragtime]])

(def +version+ "0.1.0-SNAPSHOT")

(bootlaces! +version+)

(task-options!
 pom {:project 'mbuczko/boot-ragtime
      :version +version+
      :description "Run ragtime migrations in boot."
      :url "https://github.com/mbuczko/boot-ragtime"
      :scm {:url "https://github.com/mbuczko/boot-ragtime"}
      :license {"name" "GNU Lesser General Public License"
                "url" "http://www.gnu.org/licenses/lgpl.txt"}}
 ragtime {:database "jdbc:postgresql://localhost:5432/template1?user=postgres"})
