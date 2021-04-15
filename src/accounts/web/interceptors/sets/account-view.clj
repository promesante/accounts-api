(ns accounts.web.interceptors.sets.account-view
  (:require [accounts.web.interceptors.validate :as validate]))

(def validate
  [validate/account-id-available])

