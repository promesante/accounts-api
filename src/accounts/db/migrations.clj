(ns accounts.db.migrations
  (:require [datomic.api :as d]
            [accounts.db.schema :as s]))

(def sample-accounts
  [{:account/id "account-1"
    :account/balance 0.00}
   {:account/id "account-2"
    :account/balance 0.00}
   {:account/id "account-3"
    :account/balance 0.00}])

(def sample-account-balance-updates
  [[:db/add [:account/id "account-1"] :account/balance 10000.00]
   [:db/add [:account/id "account-2"] :account/balance 20000.00]
   [:db/add [:account/id "account-3"] :account/balance 30000.00]])

(defn run-migrations [conn]
  @(d/transact conn sample-accounts)
  @(d/transact conn sample-account-balance-updates))
