(ns accounts.web.interceptors
  (:require [accounts.web.interceptors.base :as base]
            [accounts.web.interceptors.routes.account-view :as account-view]))

(def account-view
  (into [] (concat base/common-interceptors
                   account-view/interceptors)))
