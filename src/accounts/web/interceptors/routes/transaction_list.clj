(ns accounts.web.interceptors.routes.transaction-list
  (:require [accounts.web.interceptors.validate :as validate]
            [accounts.web.interceptors.prepare.retrieve :as prepare-retrieve]
            [accounts.web.interceptors.retrieve :as retrieve]
            [accounts.web.interceptors.display :as display]))

(def validate
  [validate/account-id-available
   prepare-retrieve/account-for-report
   retrieve/transactions-list])

(def display
  [display/entity-render
   display/transactions-list])

(def interceptors
  (into [] (concat validate)))
