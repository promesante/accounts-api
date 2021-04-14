(ns accounts.logging
  (:require [clojure.java.io :as io]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.core :as appenders]))

(defn config []
  (let [log-file-name "log.txt"]
    (do
      (io/delete-file log-file-name :quiet)
      (timbre/refer-timbre) ; set up timbre aliases
      (timbre/merge-config! {:appenders {:println {:enabled? false}}})
      (timbre/merge-config! {:appenders {:spit (appenders/spit-appender {:fname "logs/my-file.log"})}})
      (timbre/set-level! :debug))))
