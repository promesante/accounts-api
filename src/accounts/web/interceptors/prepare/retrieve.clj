(ns accounts.web.interceptors.prepare.retrieve)

(defn resolve-account [context type]
  (let [id (get-in context [:request :path-params :account-id])]
    (assoc-in context [:query-data type :id] id)))

(def account-for-report
  {:name :rprepare-retrieve-account-for-report
   :enter
   (fn [context]
       (resolve-account context :report))})
