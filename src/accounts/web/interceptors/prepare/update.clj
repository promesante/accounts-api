(ns accounts.web.interceptors.prepare.update)

(defn resolve-new-balance [context type balance]
  (let [amount (get-in context [:request :json-params :amount])]
    (if-let [account-id-json (get-in context [:request :json-params :account])]
      (if (= type :debit)
        (+ balance amount)
        (- balance amount))
      (+ balance amount))))

(defn new-balance [type]
  (fn [context]
    (if-let [account (get-in context [:retrieved :accounts type])]
      (let [id (:account/id account)
            balance (:account/balance account)
            new-balance (resolve-new-balance context type balance)]
        (assoc-in context [:tx-data type] {:id id :new-balance new-balance}))
      context)))

(def new-balance-debit
  {:name :prepare-new-balance-debit
   :enter (new-balance :debit)})

(def new-balance-credit
  {:name :prepare-new-balance-credit
   :enter (new-balance :credit)})

(defn new-transaction [type]
  (fn [context]
    (if-let [account-id-json (get-in context [:request :json-params :account])]
      context
      (if-let [account (get-in context [:retrieved :accounts type])]
        (let [amount (get-in context [:request :json-params :amount])
              description (get-in context [:request :json-params :description])
              tx-data (get-in context [:tx-data type])
              new-balance (:new-balance tx-data)
              tx {:amount amount :description description :balance new-balance}]
          (assoc-in context [:tx-data type :tx] tx))
        context))))

(def new-transaction-debit
  {:name :prepare-new-transaction-debit
   :enter (new-transaction :debit)})

(def new-transaction-credit
  {:name :prepare-new-transaction-credit
   :enter (new-transaction :credit)})

(defn record-transfer [type]
  (fn [context]
    (if-let [account-id-json (get-in context [:request :json-params :account])]
      (if-let [account (get-in context [:request :accounts type])]
        (let [account-id-path (get-in context [:request :path-params :account-id])
              description (get-in context [:request :json-params :description])
              raw-amount (get-in context [:request :json-params :amount])
              amount (if (= type :debit) raw-amount (- 0 raw-amount))
              account-id (if (= type :debit) account-id-json account-id-path)
              tx-data (get-in context [:tx-data type])
              new-balance (:new-balance tx-data)
              tx {:amount amount :description description :account-id account-id
                  :balance new-balance}]
          (assoc-in context [:tx-data type :tx] tx))
        context)
      context)))

(def record-debit-transfer
  {:name :record-debit-transaction
   :enter (record-transfer :debit)})

(def record-credit-transfer
  {:name :record-debit-transaction
   :enter (record-transfer :credit)})
