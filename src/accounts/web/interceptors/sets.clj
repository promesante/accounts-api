(ns accounts.web.interceptors.sets
  (:require [accounts.web.interceptors.base :as base]
            [accounts.web.interceptors.sets.account-view :as account-view]))

(def account-view
  (into [] (concat base/common-interceptors
                   account-view/set)))
