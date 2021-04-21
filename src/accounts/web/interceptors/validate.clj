(ns accounts.web.interceptors.validate
  (:require [io.pedestal.interceptor.chain :as chain]
            [malli.core :as m]))

(def account-id-available
  {:name :validate-account-id-available
   :enter
   (fn [context]
     (if-let [id (get-in context [:request :path-params :account-id])]
       context
       (chain/terminate
        (assoc context :response {:status 400
                                  :body "No account id supplied as path param in URL"}))))})

(defn account-available [type]
  (fn [context]
    (if-let [id (get-in context [:query-data type :id])]
      (let [account (get-in context [:retrieved :accounts type])
            account-id (:account/id account)]
        (if (= id account-id)
          context
          (chain/terminate
            (assoc context :response {:status 404
                                      :body (str "No account available for id " id " " type)}))))
      context)))

(def account-available-report
  {:name :validate-account-available-report
   :enter (account-available :report)})

(def json-params-available
  {:name :validate-json-params
   :enter
   (fn [context]
     (if-let [params (get-in context [:request :json-params])]
       context
       (chain/terminate
        (assoc context :response {:status 400
                                  :body "JSON body expected but not available"}))))})

(def Transaction
  [:map
   [:amount double?]
   [:description string?]
   [:account {:optional true} string?]])

(def json-params-structure
  {:name :validate-json-params
   :enter
   (fn [context]
     (if-let [params (get-in context [:request :json-params])]
       (if (m/validate Transaction params)
         context
        (chain/terminate
          (assoc context :response {:status 400
                                    :body (str "Wrong transaction structure: " params)})))
       context))})

(def transfer-amount
  {:name :validate-transfer-amount
   :enter
   (fn [context]
     (if-let [transfer-account-id (get-in context [:request :json-params :account])]
       (let [amount (get-in context [:request :json-params :amount])]
         (if (< amount 0)
           context
           (chain/terminate
            (assoc context :response {:status 400
                                      :body "In a transfer, amount must be negative"}))))
       context))})
