(ns accounts.web.interceptors.sets.account-view
  (:require [accounts.web.interceptors.validate :as validate]
            [accounts.web.interceptors.prepare.retrieve :as prepare-retrieve]))

(def validate
  [validate/account-id-available
   prepare-retrieve/account-for-report])

