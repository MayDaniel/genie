(ns genie.user
  (:require [genie.db :as db])
  (:use [clojure.string :only [split]]
        [genie.mail :only [send-validation]]
        [genie.constants :only [responses]]
        [genie.util :only [<-]]))

(defn validate-form [{:strs [username password email update]}]
  (every? (fn [[re coll]] (every? #(re-find re %) (<- coll)))
          {#"^[a-zA-Z0-9_]{3,12}$" [username password]
           #"^[^@]{1,64}@[^@]{1,255}$" [email]
           #"^.{3,90}$" [update]}))

(defn register [{:strs [username password email] :as user}]
  (cond (db/user-exists? username) :user-exists
        (not (validate-form user)) :invalid-characters
        :else (do (future (send-validation email))                  
                  (db/add-user! user)
                  (db/add-validation! username)
                  :registration-success)))

(defn login [{:strs [username password]}]
  (cond (not (db/user-exists? username)) :user-not-found
        (not (db/validated? username)) :unvalidated
        (not= password (:password (db/fetch-user username))) :incorrect-password
        :else :login-success))

(defn validate [username id]  
  (cond (not (db/user-exists? username))
        :user-not-found
        (db/validated? username)
        :already-validated
        (not= (:validation-id (db/fetch-user username)) id)
        :incorrect-validation-id
        :else :validation-successful))

(defn user-information [username]
  (let [{:keys [username email joined]} (db/fetch-user username)]
    (map (fn [[title value]] [:tr [:td title] [:td value]])
         [["Username:" username]
          ["Joined:"   joined]
          ["Email:"    email]])))
