(ns accounts.web.interceptors
  (:require [accounts.web.interceptors.base :as base]
            [accounts.web.interceptors.routes.account-view :as account-view]
            [accounts.web.interceptors.routes.transaction-list :as transaction-list]
            [accounts.web.interceptors.routes.transaction-create :as transaction-create]))

(def account-view
  (into [] (concat base/common-interceptors
                   account-view/interceptors)))

(def transaction-list
  (into [] (concat base/common-interceptors
                   transaction-list/interceptors)))

(def transaction-create
  (into [] (concat base/common-interceptors
                   transaction-create/interceptors)))
