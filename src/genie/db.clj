(ns genie.db
  (:require [somnium.congomongo :as db])
  (:use [genie.date :only [date]]))

(db/mongo! :db "Genie")

(defn fetch-user [username]
  (db/fetch-one :users :where {:username username}))

(defn user-exists? [username]
  (boolean (fetch-user username)))

(defn add-user! [{:keys [username password email]}]
  (db/insert! :users {:username username
                      :password password
                      :email email
                      :validated? false
                      :joined (date)}))

(defn update-user! [username f & args]
  (let [user (fetch-user username)]
    (db/update! :users user (apply f user args))))

(defn destroy-user! [username]
  (db/destroy! :users {:username username}))

(defn add-validation! [username]
  (let [id (rand-int 1e6)]
    (update-user! username :validation-id id) id))

(defn validated? [username]
  (:validated? (fetch-user username)))

(defn validate! [username]
  (let [update! (partial update-user! username)]
    (update! assoc :validated? true)
    (update! dissoc :validation-id)))
