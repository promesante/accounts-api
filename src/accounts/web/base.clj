(ns accounts.web.base
  (:require [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http :as bootstrap]
            [io.pedestal.http :as http]))

(defn response [status body & {:as headers}]
  {:status status :body body :headers headers})

(def ok       (partial response 200))
(def created  (partial response 201))
(def accepted (partial response 202))

(def common-interceptors [(body-params/body-params) http/html-body bootstrap/json-body])
