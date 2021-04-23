(ns accounts.web.interceptors.update
  (:require [accounts.db.transactions :as t]))

(defn update-balance [type]
  (fn [context]
    (if-let [tx-data (get-in context [:tx-data type])]
      (let [id (:id tx-data)
            new-balance (:new-balance tx-data)]
        (do
          (t/update-balance id new-balance)
          context))
      context)))

(def update-balance-debit
  {:name :update-balance-debit
   :enter (update-balance :debit)})

(def update-balance-credit
  {:name :update-balance-credit
   :enter (update-balance :credit)})

(defn new-transaction [type]
  (fn [context]
    (if-let [tx-data (get-in context [:tx-data type])]
      (let [{:keys [account-id tx]} tx-data
            ; account-id (:id tx-data)
            ; tx (:tx tx-data)
            {:keys [amount description transfer-account-id balance]} tx
            ; amount (:amount tx)
            ; description (:description tx)
            ; transfer-account-id (:account-id tx)
            ; balance (:balance tx)
            ]
        (do
          (t/new-transaction account-id amount description balance transfer-account-id)
          context))
      context)))

(def new-transaction-debit
  {:name :new-transaction-debit
   :enter (new-transaction :debit)})

(def new-transaction-credit
  {:name :new-transaction-credit
   :enter (new-transaction :credit)})
