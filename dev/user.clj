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
