(ns accounts.web.interceptors.display
  (:require [taoensso.timbre :as timbre :refer [info]]
            [accounts.web.base :as b]
            [accounts.db.queries :as q]))

(def entity-render
  {:name :entity-render
   :leave
   (fn [context]
     (if-let [item (:result context)]
       (assoc context :response (b/ok item))
       context))})

(def account-view
  {:name :account-view
   :leave
   (fn [context]
     (info "account-view")
     (if-let [the-account (get-in context [:request :accounts :report])]
       (assoc context :result the-account)
       context))})
