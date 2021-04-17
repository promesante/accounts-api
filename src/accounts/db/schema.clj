(ns accounts.db.schema
  (:require [datomic.api :as d]))

(def account-schema
  [{:db/doc "Account ID"
    :db/ident :account/id
    :db/valueType :db.type/string
    :db/unique :db.unique/identity
    :db/cardinality :db.cardinality/one}
   {:db/doc "Account Balance"
    :db/ident :account/balance
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}
   ])

(def transaction-schema
  [{:db/doc "Transaction ID"
    :db/ident :transaction/id
    :db/valueType :db.type/string
    :db/unique :db.unique/identity
    :db/cardinality :db.cardinality/one}
   {:db/doc "Transaction Account ID"
    :db/ident :transaction/account-id
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}
   {:db/doc "Transaction Amount"
    :db/ident :transaction/amount
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}
   {:db/doc "Transaction Description"
    :db/ident :transaction/description
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/doc "Transfer Account ID"
    :db/ident :transaction/transfer-account-id
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}
   {:db/doc "Resulting Account Balance"
    :db/ident :transaction/balance
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}])

(def schema
  (into [] (concat account-schema
                   transaction-schema)))

(defn create-schema [conn]
  @(d/transact conn schema))
