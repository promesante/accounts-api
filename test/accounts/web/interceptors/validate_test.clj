(ns accounts.web.interceptors.validate-test
  (:require [clojure.test :refer :all]
            [accounts.web.validate :refer :all]))

(deftest can-validate-account-id-available
  (testing "validate account id available"
    (let [
          context-1 {:request {:path-params {:account-id "account-1"}}}
          context-2 {:request {:path-params {}}}
          ]
      (is (= context-1 ((:enter account-id-available) context-1)))
      (is (= {:status 400 :body "No account id supplied as path param in URL"}
             (:response ((:enter account-id-available) context-2)))))))
