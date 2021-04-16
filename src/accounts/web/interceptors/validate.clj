(ns accounts.web.interceptors.validate
  (:require [io.pedestal.interceptor.chain :as chain]))

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
