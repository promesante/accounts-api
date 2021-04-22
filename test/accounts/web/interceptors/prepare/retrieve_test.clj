(ns accounts.web.interceptors.prepare.retrieve-test
  (:require [clojure.test :refer :all]
            [accounts.web.interceptors.prepare.retrieve :refer :all]))

(deftest can-prepare-retrieve-account-for-report
  (testing "prepare retrieve account for report"
    (let [context {:request {:path-params {:account-id "account-1"}}}]
      (is (= {:report {:id "account-1"}}
             (:query-data ((:enter account-for-report) context)))))))

(deftest can-prepare-to-retrieve-account-for-report-by-transaction
  (testing "prepare to retrieve account for report by transaction"
    (let [context-1 {:request
                     {:path-params {:account-id "account-1"}
                      :json-params {:amount 1000.0 :description "test"}}}
          context-2 {:request
                     {:path-params {:account-id "account-1"}
                      :json-params {:amount -1000.0 :description "test"}}}]
      (is (= {:credit {:id "account-1"}}
             (:query-data ((:enter account-for-report-by-transaction) context-1))))
      (is (= {:debit {:id "account-1"}}
             (:query-data ((:enter account-for-report-by-transaction) context-2)))))))

(deftest can-prepare-to-retrieve-transfer-account
  (testing "prepare to retrieve transfer account"
    (let [context-1 {:request
                     {:json-params {:amount -1000.0 :account "account-1"}}}
          context-2 {:request
                     {:json-params {:amount -1000.0 :account "account-1"}}}
          context-3 {:request
                     {:json-params {:amount -1000.0}}}
          context-4 {:request
                     {:json-params {:amount 1000.0}}}]
      (is (= {:credit {:id "account-1"}}
             (:query-data ((:enter transfer-account) context-1))))
      (is (= {:credit {:id "account-1"}}
             (:query-data ((:enter transfer-account) context-2))))
      (is (= context-3 ((:enter transfer-account) context-3)))
      (is (= context-4 ((:enter transfer-account) context-4))))))
