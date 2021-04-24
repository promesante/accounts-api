(ns accounts.web.interceptors.display
  (:require [accounts.web.interceptors.base :as b]))

(def entity-render
  {:name :entity-render
   :leave
   (fn [context]
     (if-let [item (:result context)]
       (assoc context :response (b/ok item))
       context))})

(def account-view
  {:name :display-account-view
   :leave
   (fn [context]
     (if-let [the-account (get-in context [:retrieved :accounts :report])]
       (assoc context :result the-account)
       context))})

(def transactions-list
  {:name :display-transactions-list
   :leave
   (fn [context]
     (if-let [txs (get-in context [:retrieved :txs])]
       (assoc context :result txs)
       context))})

(defn transaction-created [type]
  (fn [context]
    (let [account-id (get-in context [:request :path-params :account-id])
          tx-account-id (get-in context [:tx-data type :id])
          tx (get-in context [:tx-data type :tx])]
      (if (= account-id tx-account-id)
        (assoc context :result tx)
        context))))

(def transaction-created-debit
  {:name :display-transaction-created-debit
   :leave (transaction-created :debit)})

(def transaction-created-credit
  {:name :display-transaction-created-credit
   :leave (transaction-created :credit)})
