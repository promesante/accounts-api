(ns accounts.db.transactions
  (:require [datomic.api :as d]
            [accounts.db.conn :as c]))

(defn update-balance [id new-balance]
    (d/transact
     c/conn
     [[:db/add [:account/id id] :account/balance new-balance]]))

 (defn new-transaction [account-id amount description balance & [transfer-account-id]]
   (let [base {:transaction/id (str (gensym "trx-9"))
               :transaction/account-id [:account/id account-id]
               :transaction/amount amount
               :transaction/description description
               :transaction/balance balance}
         full (if (nil? transfer-account-id)
                base
                (assoc base :transaction/transfer-account-id [:account/id transfer-account-id]))]
     (d/transact c/conn [full])))
