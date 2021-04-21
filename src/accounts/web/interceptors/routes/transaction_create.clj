(ns accounts.web.interceptors.routes.transaction-create
  (:require [accounts.web.interceptors.validate :as validate]))

(def validate
  [validate/json-params-available
   validate/json-params-structure
   validate/account-id-available
   validate/transfer-amount])

(def interceptors
  (into [] (concat validate)))
