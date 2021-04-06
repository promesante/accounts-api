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

(defn create-schema [conn]
  @(d/transact conn account-schema))
