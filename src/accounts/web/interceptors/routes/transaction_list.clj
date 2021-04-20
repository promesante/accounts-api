(ns accounts.web.interceptors.routes.transaction-list
  (:require [accounts.web.interceptors.validate :as validate]
            [accounts.web.interceptors.prepare.retrieve :as prepare-retrieve]
            [accounts.web.interceptors.retrieve :as retrieve]))

(def validate
  [validate/account-id-available
   prepare-retrieve/account-for-report
   retrieve/transactions-list])

(def interceptors
  (into [] (concat validate)))
