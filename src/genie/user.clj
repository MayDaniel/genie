(ns genie.user
  (:require [genie.db :as db])
  (:use [clojure.string :only [split]]
        [genie.mail :only [send-validation]]
        [genie.constants :only [responses]]))

(defn validate-all [{:strs [username password email update]}]
  (every? (fn [[re coll]] (every? (fn [s] (if s (re-find re s) true)) coll))
          {#"^[a-zA-Z0-9_]{3,12}$" [username password]
           #"^[^@]{1,64}@[^@]{1,255}$" [email]
           #"^.{3,90}$" [update]}))

(defn register [{:strs [username password email] :as user}]
  (cond (db/user-exists? username) :user-exists
        (not (validate-all user)) :invalid-characters
        :else (do (future (send-validation email))                  
                  (db/add-user! user)
                  (db/add-validation! username)
                  :registration-success)))

(defn login [{:strs [username password]}]
  (cond (not (db/user-exists? username)) :user-not-found
        (not (db/validated? username)) :unvalidated
        (not= password (:password (db/fetch-user username))) :incorrect-password
        :else :login-success))

(defn validate [uri]
  (let [[username id] (nnext (split uri #"/"))]
    (cond (not (db/user-exists? username))
          :user-not-found
          (db/validated? username)
          :already-validated
          (not= (:validation-id (db/fetch-user username)) id)
          :incorrect-validation-id
          :else :validation-successful)))
