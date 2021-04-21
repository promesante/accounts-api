(ns accounts.web.interceptors.routes.transaction-create
  (:require [accounts.web.interceptors.validate :as validate]))

(def validate
  [validate/json-params-available
   validate/json-params-structure])

(def interceptors
  (into [] (concat validate)))
