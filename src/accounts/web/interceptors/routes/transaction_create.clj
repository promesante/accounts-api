(ns accounts.web.interceptors.routes.transaction-create
  (:require [accounts.web.interceptors.validate :as validate]
            [accounts.web.interceptors.prepare.retrieve :as prepare-retrieve]
            [accounts.web.interceptors.prepare.update :as prepare-update]
            [accounts.web.interceptors.retrieve :as retrieve]
            [accounts.web.interceptors.update :as update]))

(def validate
  [validate/json-params-available
   validate/json-params-structure
   validate/account-id-available
   validate/transfer-amount
   prepare-retrieve/account-by-transaction
   prepare-retrieve/transfer-account
   retrieve/account-detail-debit
   retrieve/account-detail-credit
   validate/account-available-debit
   validate/account-available-credit
   validate/sufficient-funds])

(def prepare
  [prepare-update/new-balance-debit
   prepare-update/new-balance-credit
   prepare-update/new-transaction-debit
   prepare-update/new-transaction-credit])

(def update
  [update/update-balance-debit
   update/update-balance-credit
   update/new-transaction-debit
   update/new-transaction-credit])

(def interceptors
  (into [] (concat validate
                   prepare
                   update)))
