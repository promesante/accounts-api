(ns accounts.web.interceptors.display-test
  (:require [clojure.test :refer :all]
            [accounts.web.interceptors.display :refer :all]))

(def context-1 {:request {:accounts {:report "account-1"}}}
  )

(def context-2 {:request {:accounts {:debit "account-1"}}}
  )

(deftest can-render-entity
  (testing "entity render"
    (let [context-in {:result {:field "value"}}
          context-out ((:leave entity-render) context-in)]
      (is (= {:field "value"}
             (get-in context-out [:response :body])))
      (is (= 200 (get-in context-out [:response :status]))))))

(deftest can-account-view
  (testing "account view"
    (let [context-1 {:request {:accounts {:report "account-1"}}}
          context-2 {:request {:accounts {:debit "account-1"}}}]
      (is (= "account-1" (:result ((:leave account-view) context-1))))
      (is (= context-2 ((:leave account-view) context-2))))))
