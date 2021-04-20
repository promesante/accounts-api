(ns accounts.web.interceptors
  (:require [accounts.web.interceptors.base :as base]
            [accounts.web.interceptors.routes.account-view :as account-view]
            [accounts.web.interceptors.routes.transaction-list :as transaction-list]))

(def account-view
  (into [] (concat base/common-interceptors
                   account-view/interceptors)))

(def transactions-list
  (into [] (concat base/common-interceptors
                   transaction-list/interceptors)))
