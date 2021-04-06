(ns accounts.conf
  (:require [mount.core :as mount :refer [defstate]]
            [clojure.edn :as edn]))

(defn load-config [path]
  (-> path
      slurp
      edn/read-string))

(defstate config
  :start (load-config "resources/config.edn"))
