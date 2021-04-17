(ns accounts.accounts
  (:require [mount.core :as mount])
  (:gen-class)
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "\nCreating your server...")
  (mount/start))
