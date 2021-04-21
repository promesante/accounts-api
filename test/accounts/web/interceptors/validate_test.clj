(ns accounts.web.interceptors.validate-test
  (:require [clojure.test :refer :all]
            [accounts.web.interceptors.validate :refer :all]))

(deftest can-validate-account-id-available
  (testing "validate account id available"
    (let [context-1 {:request {:path-params {:account-id "account-1"}}}
          context-2 {:request {:path-params {}}}]
      (is (= context-1 ((:enter account-id-available) context-1)))
      (is (= {:status 400 :body "No account id supplied as path param in URL"}
             (:response ((:enter account-id-available) context-2)))))))

(deftest can-validate-account-available
  (testing "validate account available"
    (let [context-1 {:retrieved {:accounts {:report #:account{:id "account-1", :balance 10000.0}}}
                     :query-data {:report {:id "account-1"}}}
          context-2 {:retrieved {:accounts {:report #:account{:id "account-2", :balance 10000.0}}}
                     :query-data {:report {:id "account-1"}}}]
      (is (= context-1 ((:enter account-available-report) context-1)))
      (is (= {:status 404 :body "No account available for id account-1 :report"}
             (:response ((:enter account-available-report) context-2))))
      )))

(def
          context-2 {:request {}}
  )

(deftest can-validate-json-params-available
  (testing "validate json params available"
    (let [context-1 {:request {:json-params {:amount 1000.0
                                             :description "test"}}}
          context-2 {:request {}}
          ]
      (is (= context-1 ((:enter json-params-available) context-1)))
      (is (= {:status 400 :body "JSON body expected but not available"}
             (:response ((:enter json-params-available) context-2))))
    )))

(deftest can-validate-json-params-structure
  (testing "validate json params structure"
    (let [context-1 {:request {:json-params {:amount 1000.0
                                             :description "test"}}}
          context-2 {:request {:json-params {:amount 1000.0
                                             :description "test"
                                             :account "account-1"}}}
          context-3 {:request {:json-params {:amount 1000.0}}}
          context-4 {:request {:json-params {:description "test"}}}
          context-5 {:request {}}
          ]
      (is (= context-1 ((:enter json-params-structure) context-1)))
      (is (= context-2 ((:enter json-params-structure) context-2)))
      (is (= {:status 400 :body (str "Wrong transaction structure: " {:amount 1000.0})}
             (:response ((:enter json-params-structure) context-3))))
      (is (= {:status 400 :body (str "Wrong transaction structure: " {:description "test"})}
             (:response ((:enter json-params-structure) context-4))))
      (is (= context-5 ((:enter json-params-structure) context-5)))
      )))
