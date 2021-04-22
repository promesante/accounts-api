(ns accounts.web.interceptors.prepare.retrieve)

(defn resolve-account [context type]
  (let [id (get-in context [:request :path-params :account-id])]
    (assoc-in context [:query-data type :id] id)))

(def account-for-report
  {:name :prepare-to-retrieve-account-for-report
   :enter
   (fn [context]
       (resolve-account context :report))})

(defn resolve-type [context]
  (let [id (get-in context [:request :path-params :account-id])
        amount (get-in context [:request :json-params :amount])]
    (if (> amount 0) :credit :debit)))

(def account-by-transaction
  {:name :prepare-to-retrieve-account-by-transaction
   :enter
   (fn [context]
     (let [type (resolve-type context)]
       (resolve-account context type)))})

(def transfer-account
  {:name :prepare-to-retrieve-transfer-account
   :enter
   (fn [context]
     (if-let [id (get-in context [:request :json-params :account])]
       ; the transaction is a transfer, its debit accout has been recorded in
       ; validate-account-available, and this validation keeps record of its credit account
       ; (retrieve-account context :credit id)
       (assoc-in context [:query-data :credit :id] id)
       context))})
