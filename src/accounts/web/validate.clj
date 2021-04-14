(ns accounts.web.validate
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
