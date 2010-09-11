(ns genie.core
  (:require [genie.user :as user])
  (:use [genie.pages :only [defpage redirect]]
        [net.cgrand.moustache :only [app]]
        [hiccup.form-helpers :only [form-to text-field password-field submit-button]]
        [ring.middleware params file session stacktrace]))

(defpage login "Log in"
  (form-to [:post "/login"]
           (text-field "username" "Username")
           (password-field "password" "Password")
           (submit-button "Log in")))

(defn login-handler [{session :session params :params}]
  (let [{:strs [username password]} params
        response (user/login params)
        success? #(= :login-success response)
        session (cond (not (and username password)) session
                      (success?) (assoc session :in-as username :response response)
                      :else (assoc session :response response))]
    {:status 200
     :session session
     :headers {"Content-Type" "text/html"}
     :body (if (:in-as session) (redirect "/") (login session))}))

(defn logout-handler [{session :session}]
  {:status 200
   :session {}
   :headers {"Content-Type" "text/html"}
   :body (redirect "/")})

(defpage not-found "Invalid URL"
  [:img {:src "/images/not-found.png"}])

(defn not-found-handler [{session :session}]
  {:status 200
   :session session
   :headers {"Content-Type" "text/html"}
   :body (not-found session)})

(def routes (app (wrap-session)
                 (wrap-file "resources/public")
                 (wrap-params)
                 (wrap-stacktrace)
                 ["login"] login-handler
                 ["logout"] logout-handler
                 [&] not-found-handler))
