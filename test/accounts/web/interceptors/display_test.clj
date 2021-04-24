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

(deftest can-display-transaction-created
  (testing "display transaction created"
    (let [context-1 {:request
                     {:json-params {:amount 1000.0 :description "test"}
                      :path-params {:account-id "account-1"}}
                     :retrieved {:accounts {:credit #:account{:id "account-1",
                                                              :balance 10000.0}}}
                     :tx-data
                     {:credit
                      {:id "account-1"
                       :new-balance 11000.0
                       :tx {:amount 1000.0 :description "test" :balance 11000.0}}}} 
          context-2 {:request
                     {:json-params {:amount -1000.0 :description "test"}
                      :path-params {:account-id "account-1"}}
                     :retrieved {:accounts {:debit #:account{:id "account-1", :balance 10000.0}}}
                     :tx-data
                     {:debit
                      {:id "account-1"
                       :new-balance 9000.0
                       :tx {:amount -1000.0 :description "test" :balance 9000.0}}}}
          context-3 {:request
                     {:json-params {:amount -1000.0 :description "test" :account "account-2"}
                      :path-params {:account-id "account-1"}}
                     :retrieved {:accounts {:debit #:account{:id "account-1", :balance 10000.0}
                                            :credit #:account{:id "account-2",
                                                              :balance 20000.0}}}
                     :tx-data
                     {:debit
                      {:id "account-1"
                       :new-balance 9000.0
                       :tx {:amount -1000.0 :description "test" :balance 9000.0}}
                      :credit
                      {:id "account-2"
                       :new-balance 21000.0
                       :tx {:amount 1000.0 :description "test" :balance 21000.0}}}}]
      (is (= {:amount 1000.0 :description "test" :balance 11000.0}
             (:result ((:leave transaction-created-credit) context-1))))
      (is (= {:amount -1000.0 :description "test" :balance 9000.0}
             (:result ((:leave transaction-created-debit) context-2))))
      (is (= {:amount -1000.0 :description "test" :balance 9000.0}
             (:result ((:leave transaction-created-debit) context-3))))
      (is (= context-3 ((:leave transaction-created-credit) context-3))))))
