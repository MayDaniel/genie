(ns genie.core
  (:use [genie [constants :only [responses]] [mail :only [send-validation]]]
        [hiccup [core :only [html]] [page-helpers :only [link-to]]])
  (:require [genie [db :as db]]))

(defn validate-all [{:keys [username password email update]}]
  (let [regexp {:user #"^[a-zA-Z0-9_]{3,12}$"
                :email #"^[^@]{1,64}@[^@]{1,255}$"
                :update #"^.{3,90}$"}]
    (letfn [(re-check [[key coll]] (map #(if % (re-find (regexp key) % true)) coll))]
      (->> {:user [username password] :email [email] :update [update]}
           (map re-check)
           (flatten)
           (every? identity)))))

(defn register [{:keys [username password email] :as user}]
  (responses
   (cond (db/user-exists? username) :user-exists
         (validate-all user) :invalid-characters
         :else (do (future (send-validation email))
                   (db/add-user! user)
                   :registration-success))))

(defn login [{:keys [username password]}]
  (responses
   (cond (not (db/validated? username)) :unvalidated
         (not (db/user-exists? username)) :user-not-found
         (not= password (:password (db/fetch-user username))) :incorrect-password
         :else :login-success)))

(defn render-links [{:keys [in-as]}]
  (map (fn [[link name]] (link-to link name))
       (merge {"/" "Home"
               "/users" "Users"
               "/tags" "Tags"
               "/search" "Search"}
              (if-let [message-count (count (:messages (db/fetch-user in-as)))]
                {"/messages" (str "Messages" "(" message-count ")")
                 (str "/users/" in-as "/edit") "Settings"
                 "/logout" "Log out"}
                {"/login" "Log in"
                 "/register" "Register"}))))
