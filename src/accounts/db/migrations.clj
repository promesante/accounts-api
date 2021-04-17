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

(def sample-deposits
  [{:transaction/account-id [:account/id "account-1"]
    :transaction/id "trx-1"
    :transaction/amount 10000.00
    :transaction/description "first deposit"
    :transaction/balance 10000.00}
   {:transaction/account-id [:account/id "account-2"]
    :transaction/id "trx-2"
    :transaction/amount 20000.00
    :transaction/description "first deposit"
    :transaction/balance 20000.00}
   {:transaction/account-id [:account/id "account-3"]
    :transaction/id "trx-3"
    :transaction/amount 30000.00
    :transaction/description "first deposit"
    :transaction/balance 30000.00}])

(def sample-account-balance-updates-deposits
  [[:db/add [:account/id "account-1"] :account/balance 10000.00]
   [:db/add [:account/id "account-2"] :account/balance 20000.00]
   [:db/add [:account/id "account-3"] :account/balance 30000.00]])

(def sample-withdrawals
  [{:transaction/account-id [:account/id "account-1"]
    :transaction/id "trx-4"
    :transaction/amount -1000.00
    :transaction/description "appartment rent - febr 2021"
    :transaction/balance 9000.00}
   {:transaction/account-id [:account/id "account-2"]
    :transaction/id "trx-5"
    :transaction/amount -1000.00
    :transaction/description "credit card - febr 2021"
    :transaction/balance 19000.00}
   {:transaction/account-id [:account/id "account-3"]
    :transaction/id "trx-6"
    :transaction/amount -2000.00
    :transaction/description "mortgage - febr 2021"
    :transaction/balance 28000.00}])

(def sample-account-balance-updates-withdrawals
  [[:db/add [:account/id "account-1"] :account/balance 9000.00]
   [:db/add [:account/id "account-2"] :account/balance 19000.00]
   [:db/add [:account/id "account-3"] :account/balance 28000.00]])

(def sample-transfers
  [{:transaction/account-id [:account/id "account-3"]
    :transaction/transfer-account-id [:account/id "account-2"]
    :transaction/id "trx-7"
    :transaction/amount -2000.00
    :transaction/description "peter's present"
    :transaction/balance 21000.00}
   {:transaction/account-id [:account/id "account-2"]
    :transaction/transfer-account-id [:account/id "account-3"]
    :transaction/id "trx-8"
    :transaction/amount 2000.00
    :transaction/description "peter's present"
    :transaction/balance 26000.00}
   {:transaction/account-id [:account/id "account-2"]
    :transaction/transfer-account-id [:account/id "account-1"]
    :transaction/id "trx-9"
    :transaction/amount -1000.00
    :transaction/description "thomas' present"
    :transaction/balance 20000.00}
   {:transaction/account-id [:account/id "account-1"]
    :transaction/transfer-account-id [:account/id "account-2"]
    :transaction/id "trx-10"
    :transaction/amount 1000.00
    :transaction/description "thomas' present"
    :transaction/balance 10000.00}])

(def sample-account-balance-updates-transfers
  [[:db/add [:account/id "account-1"] :account/balance 10000.00]
   [:db/add [:account/id "account-2"] :account/balance 20000.00]
   [:db/add [:account/id "account-3"] :account/balance 26000.00]])

(defn run-migrations [conn]
  @(d/transact conn sample-accounts)
  @(d/transact conn sample-deposits)
  @(d/transact conn sample-account-balance-updates-deposits)
  @(d/transact conn sample-withdrawals)
  @(d/transact conn sample-account-balance-updates-withdrawals)
  @(d/transact conn sample-transfers)
  @(d/transact conn sample-account-balance-updates-transfers))
