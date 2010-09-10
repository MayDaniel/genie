(ns genie.db)

(mongo! :db "Genie")

(defn fetch-user [username]
  (dissoc :id (fetch-one :users :where {:username username})))

(defn user-exists? [username]
  (boolean (fetch-user username)))

(defn add-user! [{:keys [username password email]}]
    (insert! :users {:username username
                     :password password
                     :email email
                     :validated? false
                     ;; :joined :updates :messages
                     }))

(defn update-user! [username f & args]
  (let [user (fetch-user username)]
    (update! :users user (apply f user args))))

(defn destroy-user! [username]
  (destroy! :users {:username username}))
