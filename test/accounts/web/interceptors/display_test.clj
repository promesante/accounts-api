(ns accounts.web.interceptors.display-test
  (:require [clojure.test :refer :all]
            [accounts.web.interceptors.display :refer :all]))

(deftest can-render-entity
  (testing "render entity"
    (let [context-in {:result {:field "value"}}
          context-out ((:leave entity-render) context-in)]
      (is (= {:field "value"}
             (get-in context-out [:response :body])))
      (is (= 200 (get-in context-out [:response :status]))))))

(deftest can-display-account-view
  (testing "display account view"
    (let [context-1 {:retrieved {:accounts {:report "account-1"}}}
          context-2 {:retrieved {:accounts {:debit "account-1"}}}]
      (is (= "account-1" (:result ((:leave account-view) context-1))))
      (is (= context-2 ((:leave account-view) context-2))))))

(deftest can-display-transactions-list
  (testing "display transactions list"
    (let [context-1 {:retrieved {:txs "txs"}}
          context-2 {:retrieved {:accounts "accounts"}}]
      (is (= "txs" (:result ((:leave transactions-list) context-1))))
      (is (= context-2 ((:leave transactions-list) context-2))))))
