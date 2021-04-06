(ns user
  (:require [mount.core :as mount]
            [accounts.db.conn :as c]
            [accounts.db.queries :as q]))

(defn start []
  (mount/start))

(defn stop []
  (mount/stop))
