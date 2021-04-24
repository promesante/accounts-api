(ns accounts.web.interceptors.routes.transaction-create
  (:require [accounts.web.interceptors.validate :as validate]
            [accounts.web.interceptors.prepare.retrieve :as prepare-retrieve]
            [accounts.web.interceptors.prepare.update :as prepare-update]
            [accounts.web.interceptors.retrieve :as retrieve]
            [accounts.web.interceptors.update :as updating]
            [accounts.web.interceptors.display :as display]))

(def display
  [display/entity-render
   display/transaction-created-debit
   display/transaction-created-credit])

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
   prepare-update/new-transaction-credit
   prepare-update/new-transfer-debit
   prepare-update/new-transfer-credit])

; naming this funtction as 'updating' instead of 'update',
; as it might seem required by project's naming convention
; because 'update' conflicts with 'clojure.core/update' function
(def updating
  [updating/update-balance-debit
   updating/update-balance-credit
   updating/new-transaction-debit
   updating/new-transaction-credit])

(def interceptors
  (into [] (concat display
                   validate
                   prepare
                   updating)))
