(ns genie.mail
  (:use [clj-mail.core :only [send-msg]]
        [clojure.set :only [rename-keys]]
        [genie.db :only [add-validation!]]))

(defn mail-credentials []
  (rename-keys (load-file "configuration.clj")
               {:username :user, :password :pass, :ssl? :ssl}))

(defn send-message [email subject body]
  (apply send-msg :to email :subject subject :body body
         (flatten (vec (mail-credentials)))))

(defn send-validation [username email]
  (send-message email "Genie - Confirm your account"
    (str "Validate your Genie account at: "
         "http://localhost:8080/confirm/"
         username "/" (add-validation! username))))
