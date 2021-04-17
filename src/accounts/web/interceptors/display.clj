(ns accounts.web.interceptors.display
  (:require [accounts.web.interceptors.base :as b]))

(def entity-render
  {:name :entity-render
   :leave
   (fn [context]
     (if-let [item (:result context)]
       (assoc context :response (b/ok item))
       context))})

(def account-view
  {:name :display-account-view
   :leave
   (fn [context]
     (if-let [the-account (get-in context [:retrieved :accounts :report])]
       (assoc context :result the-account)
       context))})
