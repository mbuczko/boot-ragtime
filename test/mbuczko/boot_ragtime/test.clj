(ns mbuczko.boot-ragtime.test
  (:require [clojure.test :refer :all]
            [boot.core :refer :all]
            [mbuczko.boot-ragtime :refer [ragtime]]))

(deftest ragtime-task-composes
  []
  (let [fs (boot.tmpdir.TmpFileSet. nil nil nil)]
    (is (= fs (((ragtime :list-migrations true) identity) fs)))))
