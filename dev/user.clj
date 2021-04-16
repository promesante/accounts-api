(ns user
  (:require [mount.core :as mount]
            [accounts.db.conn :as c]
            [accounts.logging :as l]
            [accounts.db.queries :as q]))

(defn start []
  (do
    (l/config)
    (mount/start)))

(defn stop []
  (mount/stop))

(defn refresh []
  (stop)
  (tn/refresh))

(defn refresh-all []
  (stop)
  (tn/refresh-all))

(defn go
  "starts all states defined by defstate"
  []
  (start)
  :ready)

(defn reset
  "stops all states defined by defstate, reloads modified source files, and restarts the states"
  []
  (stop)
  (tn/refresh :after 'user/go)
  )

(mount/in-clj-mode)
