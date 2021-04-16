(ns accounts.web.interceptors.validate-test
  (:require [clojure.test :refer :all]
            [accounts.web.interceptors.validate :refer :all]))

(deftest can-validate-account-id-available
  (testing "validate account id available"
    (let [
          context-1 {:request {:path-params {:account-id "account-1"}}}
          context-2 {:request {:path-params {}}}
          ]
      (is (= context-1 ((:enter account-id-available) context-1)))
      (is (= {:status 400 :body "No account id supplied as path param in URL"}
             (:response ((:enter account-id-available) context-2)))))))

(deftest can-validate-account-available
  (testing "validate account available"
    (let [
          context-1 {:request {:accounts {:report #:account{:id "account-1", :balance 10000.0}}}
                     :query-data {:report {:id "account-1"}}}
          context-2 {:request {:accounts {:report #:account{:id "account-2", :balance 10000.0}}}
                     :query-data {:report {:id "account-1"}}}
          ]
      (is (= context-1 ((:enter account-available-report) context-1)))
      (is (= {:status 404 :body "No account available for id account-1 :report"}
             (:response ((:enter account-available-report) context-2)))))))
