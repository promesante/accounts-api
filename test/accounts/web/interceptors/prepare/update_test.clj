(ns accounts.web.interceptors.prepare.update-test
  (:require [clojure.test :refer :all]
            [accounts.web.interceptors.prepare.update :refer :all]))

(deftest can-resolve-new-balance
  (testing "resolve new balance"
    (is (= 9000.0
           (resolve-new-balance {:request {:json-params {:amount -1000.0}}} :debit 10000.0)))
    (is (= 11000.0
           (resolve-new-balance {:request {:json-params {:amount 1000.0}}} :credit 10000.0)))
    (is (= 9000.0
           (resolve-new-balance {:request {:json-params {:amount -1000.0 :account "account-1"}}}
                                :debit
                                10000.0)))
    (is (= 11000.0
           (resolve-new-balance {:request {:json-params {:amount -1000.0 :account "account-1"}}}
                                :credit
                                10000.0)))))

(deftest can-prepare-new-balance
  (testing "prepare new balance"
    (let [context-1 {:request {:json-params {:amount -1000.0}}
                     :retrieved {:accounts {:debit #:account{:id "account-1",
                                                             :balance 10000.0}}}}
          context-2 {:request {:json-params {:amount 1000.0}}
                     :retrieved {:accounts {:credit #:account{:id "account-1",
                                                              :balance 10000.0}}}}
          context-3 {:request {:json-params {:amount -1000.0 :account "account-1"}}
                     :retrieved {:accounts {:debit #:account{:id "account-1",
                                                             :balance 10000.0}}}}
          context-4 {:request {:json-params {:amount -1000.0 :account "account-1"}}
                     :retrieved {:accounts {:credit #:account{:id "account-1",
                                                              :balance 10000.0}}}}]
      (is (= {:id "account-1" :new-balance 9000.0}
             (get-in ((:enter new-balance-debit) context-1)
                     [:tx-data :debit])))
      (is (= {:id "account-1" :new-balance 11000.0}
             (get-in ((:enter new-balance-credit) context-2)
                     [:tx-data :credit])))
      (is (= {:id "account-1" :new-balance 9000.0}
             (get-in ((:enter new-balance-debit) context-3)
                     [:tx-data :debit])))
      (is (= {:id "account-1" :new-balance 11000.0}
             (get-in ((:enter new-balance-credit) context-4)
                     [:tx-data :credit]))))))

(deftest can-prepare-new-transaction
  (testing "prepare new transaction"
    (let [context-1 {:request {:json-params {:amount -1000.0 :description "test"}}
                     :retrieved {:accounts {:debit #:account{:id "account-1", :balance 10000.0}}}
                     :tx-data {:debit {:id "account-1" :new-balance 9000.0}}}
          context-2 {:request {:json-params {:amount 1000.0 :description "test"}}
                     :retrieved {:accounts {:credit #:account{:id "account-1", :balance 10000.0}}}
                     :tx-data {:credit {:id "account-1" :new-balance 11000.0}}}]
      (is (= {:amount -1000.0 :description "test" :balance 9000.0}
             (get-in ((:enter new-transaction-debit) context-1)
                     [:tx-data :debit :tx])))
      (is (= context-2 ((:enter new-transaction-debit) context-2)))
      (is (= {:amount 1000.0 :description "test" :balance 11000.0}
             (get-in ((:enter new-transaction-credit) context-2)
                     [:tx-data :credit :tx])))
      (is (= context-1 ((:enter new-transaction-credit) context-1))))))

(deftest can-prepare-new-transfer
  (testing "prepare new transfer"
    (let [
          context-1 {:request {:json-params {:amount -1000.0
                                             :description "test"
                                             :account "account-2"}}
                     :retrieved {:accounts {:debit #:account{:id "account-1", :balance 10000.0}}}
                     :tx-data {:debit {:id "account-1" :new-balance 9000.0}}}
          context-2 {:request 
                     {:json-params {:amount -1000.0 :description "test" :account "account-2"}
                      :path-params {:account-id "account-1"}}
                     :retrieved {:accounts {:credit #:account{:id "account-2", :balance 10000.0}}}
                     :tx-data {:credit {:id "account-2" :new-balance 11000.0}}}
          context-3 {:request {:json-params {:amount -1000.0 :description "test"}}
                     :retrieved {:accounts {:debit #:account{:id "account-1", :balance 10000.0}}}
                     :tx-data {:debit {:id "account-1" :new-balance 9000.0}}}
          context-4 {:request 
                     {:json-params {:amount -1000.0 :description "test"}
                      :path-params {:account-id "account-1"}}
                     :retrieved {:accounts {:credit #:account{:id "account-2", :balance 10000.0}}}
                     :tx-data {:credit {:id "account-2" :new-balance 11000.0}}}
          ]
      (is (= {:amount -1000.0 :description "test" :account-id "account-2" :balance 9000.0}
             (get-in ((:enter new-transfer-debit) context-1)
                     [:tx-data :debit :tx])))
      (is (= {:amount 1000.0 :description "test" :account-id "account-1" :balance 11000.0}
             (get-in ((:enter new-transfer-credit) context-2)
                     [:tx-data :credit :tx])))
      (is (= context-3 ((:enter new-transfer-debit) context-3)))
      (is (= context-4 ((:enter new-transfer-credit) context-4))))))
