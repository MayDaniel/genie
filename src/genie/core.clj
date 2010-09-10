(ns genie.core)

(defn all? [coll] (every? identity coll))

(defn validate-all [{:keys [username password email update]}]
  (let [regexp {:user #"^[a-zA-Z0-9_]{3,12}$"
                :email #"^[^@]{1,64}@[^@]{1,255}$"
                :update #"^.{3,90}$"}]
    (letfn [(re-check [s key] (boolean (if s (re-find (regexp key) s) true)))]
      (all? (map (fn [[s key]] (re-check s key))
                 {username :user
                  password :user
                  email :email
                  update :update})))))

(defn register [{:keys [username password email :as user]}]
  (responses
   (cond (user-exists? username) :user-exists
         (validate-all user) :invalid-characters
         :else (do (.start (Thread. (send-validation email)))
                   (add-user! user)
                   :registration-success))))

(defn login [{:keys [username password]}]
  (responses
   (cond (not (validated? username)) :not-validated
         (not (user-exists? username)) :user-not-found
         (not= password (:password (fetch-user username))) :incorrect-password
         :else :login-success)))

(defn render-links [{:keys [in-as]}]
  (letfn [(links-to [coll] (html (map (fn [[link name]] (link-to link name)))))]
    (links-to
     (merge {"/" "Home"
             "/users" "Users"
             "/tags" "Tags"
             "/search" "Search"}      
            (if-let [message-count (count (:messages (fetch-user in-as)))]
              {"/messages" (str "Messages" "(" message-count ")")
               (str "/users/" in-as "/edit") "Settings"
               "/logout" "Log out"}                       
              {"/login" "Log in"
               "/register" "Register"})))))
