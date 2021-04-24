(ns user
  (:require [clojure.tools.namespace.repl :as tn]
            [mount.core :as mount]
            [io.pedestal.http :as http]
            [io.pedestal.test :as test]
            [accounts.web.server :as server]
            [accounts.logging :as l]
            [accounts.db.conn :as c]
            [accounts.db.queries :as q]
            [accounts.db.transactions :as t]))

(defn start []
  (do
    (l/config)
    (mount/start)))

(defn stop []
  (mount/stop))

(defn refresh []
  (stop)
  (tn/refresh))

(defn refresh-all []
  (stop)
  (tn/refresh-all))

(defn go
  "starts all states defined by defstate"
  []
  (start)
  :ready)

(defn reset
  "stops all states defined by defstate, reloads modified source files, and restarts the states"
  []
  (stop)
  (tn/refresh :after 'user/go)
  )

(mount/in-clj-mode)


;###############################################################
;
; Utils
;
;###############################################################

(defn test-request [verb url]
  (test/response-for (::http/service-fn server/server) verb url))

(defn account-view []
  (test-request :get "/accounts/account-1"))

(defn transaction-list []
  (test-request :get "/accounts/account-1/transactions"))

(defn deposit-1 []
  (test/response-for (::http/service-fn server/server) :post "/accounts/account-1/transactions" :headers {"Content-Type" "application/json"} :body "{\"amount\": 2000.0, \"description\": \"second deposit\"}"))

(defn deposit-2 []
  (test/response-for (::http/service-fn server/server) :post "/accounts/account-2/transactions" :headers {"Content-Type" "application/json"} :body "{\"amount\": 2000.0, \"description\": \"second deposit\"}"))

(defn withdrawal []
  (test/response-for (::http/service-fn server/server) :post "/accounts/account-1/transactions" :headers {"Content-Type" "application/json"} :body "{\"amount\": -1000.0, \"description\": \"appartment rent - march 2021\"}"))

(defn withdrawal-excess []
  (test/response-for (::http/service-fn server/server) :post "/accounts/account-1/transactions" :headers {"Content-Type" "application/json"} :body "{\"amount\": -21000.0, \"description\": \"appartment rent - march 2021\"}"))

(defn transfer []
  (test/response-for (::http/service-fn server/server) :post "/accounts/account-1/transactions" :headers {"Content-Type" "application/json"} :body "{\"amount\": -1000.0, \"account\": \"account-2\", \"description\": \"anne's present\"}"))

(defn deposit-int-amount []
  (test/response-for (::http/service-fn server/server) :post "/accounts/account-1/transactions" :headers {"Content-Type" "application/json"} :body "{\"amount\": 2000, \"description\": \"second deposit\"}"))

(defn deposit-no-amount []
  (test/response-for (::http/service-fn server/server) :post "/accounts/account-1/transactions" :headers {"Content-Type" "application/json"} :body "{\"description\": \"second deposit\"}"))
