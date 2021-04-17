(ns accounts.web.interceptors.routes.account-view
  (:require [accounts.web.interceptors.validate :as validate]
            [accounts.web.interceptors.prepare.retrieve :as prepare-retrieve]
            [accounts.web.interceptors.retrieve :as retrieve]
            [accounts.web.interceptors.display :as display]))

(def validate
  [validate/account-id-available
   prepare-retrieve/account-for-report
   retrieve/account-detail-report
   validate/account-available-report])

(def display
  [display/entity-render
   display/account-view])

(def interceptors
  (into [] (concat display
                   validate)))
