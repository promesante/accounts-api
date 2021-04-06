(ns accounts.db.conn
  (:require [datomic.api :as d]
            [mount.core :refer [defstate]]
            [accounts.db.schema :as sch]
            [accounts.db.migrations :as m]
            [accounts.conf :refer [config]]))

(defn- new-connection [conf]
  (let [uri (get-in conf [:datomic :uri])]
    (d/create-database uri)
    (d/connect uri)))

(defn disconnect [conf conn]
  (let [uri (get-in conf [:datomic :uri])]
    (.release conn) ;; usually it's not released, here just to illustrate the access to connection on (stop)
    (d/delete-database uri)))

(defstate conn :start (new-connection config)
               :stop (disconnect config conn))

(defn load-database []
  (do
    (sch/create-schema conn)
    (m/run-migrations conn)))
