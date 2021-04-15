(ns accounts.web.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [mount.core :as mount :refer [defstate]]
            [taoensso.timbre :as timbre :refer [info]]
            [accounts.conf :refer [config]]
            [accounts.web.interceptors.sets :as s]
            ))

(def routes
  #{["/accounts/:account-id" :get s/account-view :route-name :account-view]})

(defn service-map [conf]
  (let [port (get-in conf [:www :port])]
    (do
      (info "service - port: " port)
      {::http/routes routes
       ::http/type   :jetty
       ::http/port   port})))

(defstate server
  :start (http/start (http/create-server (assoc (service-map config) ::http/join? false)))
  :stop (http/stop server))
