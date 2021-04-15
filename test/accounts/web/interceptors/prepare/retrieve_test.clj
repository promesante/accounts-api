(ns accounts.web.interceptors.prepare.retrieve-test
  (:require [clojure.test :refer :all]
            [accounts.web.interceptors.prepare.retrieve :refer :all]))

(deftest can-prepare-retrieve-account-for-report
  (testing "prepare retrieve account for report"
    (let [context {:request {:path-params {:account-id "account-1"}}}]
      (is (= {:report {:id "account-1"}}
             (:query-data ((:enter account-for-report) context)))))))
