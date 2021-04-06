(ns accounts.db.queries
  (:require [datomic.api :as d]
            [accounts.db.conn :as c]))

(defn pull-account-by-id [id]
  (d/pull (d/db c/conn)
          [:account/id
           :account/balance]
          [:account/id id]))
