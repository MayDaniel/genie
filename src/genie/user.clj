(ns genie.user
  (:require [genie.db :as db])
  (:use [genie.mail :only [send-validation]]
        [genie.constants :only [responses]]))

(defn validate-all [{:keys [username password email update]}]
  (let [regexp {:user #"^[a-zA-Z0-9_]{3,12}$"
                :email #"^[^@]{1,64}@[^@]{1,255}$"
                :update #"^.{3,90}$"}]
    (letfn [(re-check [[key coll]] (map #(if % (re-find (regexp key) %) true) coll))]
      (->> {:user [username password] :email [email] :update [update]}
           (map re-check)
           (flatten)
           (every? identity)))))

(defn register [{:strs [username password email] :as user}]
  (responses
   (cond (db/user-exists? username) :user-exists
         (not (validate-all user)) :invalid-characters
         :else (do (future (send-validation email))
                   (db/add-user! user)
                   :registration-success))))

(defn login [{:strs [username password]}]
  (cond (not (db/user-exists? username)) :user-not-found
        (not (db/validated? username)) :unvalidated
        (not= password (:password (db/fetch-user username))) :incorrect-password
        :else :login-success))
