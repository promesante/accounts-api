(ns accounts.web.interceptors.routes.transaction-create
  (:require [accounts.web.interceptors.validate :as validate]
            [accounts.web.interceptors.prepare.retrieve :as prepare-retrieve]
            [accounts.web.interceptors.retrieve :as retrieve]))

(def validate
  [validate/json-params-available
   validate/json-params-structure
   validate/account-id-available
   validate/transfer-amount
   prepare-retrieve/account-by-transaction
   prepare-retrieve/transfer-account
   retrieve/account-detail-debit
   retrieve/account-detail-credit])

(def interceptors
  (into [] (concat validate)))
