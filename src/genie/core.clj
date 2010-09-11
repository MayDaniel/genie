(ns genie.core
  (:use [genie [constants :only [responses]] [mail :only [send-validation]]]
        [hiccup [core :only [html]] [page-helpers :only [link-to doctype]]])
  (:require [genie [db :as db]]))

(defn validate-all [{:keys [username password email update]}]
  (let [regexp {:user #"^[a-zA-Z0-9_]{3,12}$"
                :email #"^[^@]{1,64}@[^@]{1,255}$"
                :update #"^.{3,90}$"}]
    (letfn [(re-check [[key coll]] (map #(if % (re-find (regexp key) %) true) coll))]
      (->> {:user [username password] :email [email] :update [update]}
           (map re-check)
           (flatten)
           (every? identity)))))

(defn register [{:keys [username password email] :as user}]
  (responses
   (cond (db/user-exists? username) :user-exists
         (not (validate-all user)) :invalid-characters
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
       (concat ["/" "Home"
                "/users" "Users"
                "/tags" "Tags"
                "/search" "Search"]
               (if in-as
                 ["/settings" "Settings"
                  "/logout" "Log out"]
                 ["/register" "Register"
                  "/login" "Log in"]))))

(defmacro make-page [title & body]
  `(html (:html4 doctype) [:head [:title ~title]]
         (render-links ~'session) ~@body))

(defmacro defpage [name & args]
  {:arglists '([name title? argseq? & body])}
  (let [title (if (string? (first args)) (first args) (str name))
        body (if (string? (first args)) (rest args) args)
        argseq (if (vector? (first body)) (first body) [])
        body (if (seq argseq) (rest body) body)]
    `(defn ~name [~'session ~@argseq]
       (make-page ~title ~@body))))

(comment
  (defpage invalid-url "Invalid URL"
    [:img {:src "/images/page-not-found.png"}]))
