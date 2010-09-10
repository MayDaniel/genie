(ns genie.mail
  (:require [clj-mail.core :as mail]
            [clj-store.core :as store]
            [clojure.set :as set]
            [genie.db :as db]))

(defn mail-credentials []
  (set/rename-keys (store/in "mail.configuration")
                   {:username :user
                    :password :pass
                    :ssl? :ssl}))

(defn send-message [email subject body]
  (apply mail/send-msg
         :to email :subject subject :body body
         (flatten (vec (mail-credentials)))))

(defn send-validation [username email]
  (send-message email
                "Genie - Confirm your account"
                (str "Validate your Genie account at: "
                     "http://localhost:8080/" username "/"
                     (db/add-validation! username))))
