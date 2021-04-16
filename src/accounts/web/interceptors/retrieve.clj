(ns accounts.web.interceptors.retrieve
  (:require [accounts.db.queries :as q]))

(defn account-detail [type]
  (fn [context]
    (if-let [id (get-in context [:query-data type :id])]
      (let [account (q/pull-account-by-id id)]
        (assoc-in context [:retrieved :accounts type] account))
      context)))

(def account-detail-report
  {:name :retrieve-account-detail-report
   :enter (account-detail :report)})
