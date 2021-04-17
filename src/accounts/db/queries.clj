(ns accounts.db.queries
  (:require [datomic.api :as d]
            [accounts.db.conn :as c]))

(defn pull-account-by-id [id]
  (d/pull (d/db c/conn)
          [:account/id
           :account/balance]
          [:account/id id]))

(defn pull-transactions-by-account-id [account-id]
  (->>
   (d/q '[:find (pull ?e [:db/id
                          :transaction/id
                          :transaction/amount
                          :transaction/description
                          :transaction/transfer-account-id
                          :transaction/balance])
              :in $ ?id-value
              :where [?e :transaction/account-id ?id-value]]
            (d/db c/conn)
            [:account/id account-id])
       (map first)
       (sort-by :db/id >)))
