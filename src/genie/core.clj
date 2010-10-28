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

(defn login-handler [{:keys [session params]}]
  (let [{:strs [username]} params
        response (user/login params)
        success? #(= :login-success response)
        session (cond (not username) session ; Check whether they'd actually posted some log in data.
                      (success?) (assoc session :in-as username :response response)
                      :else (assoc session :response response))] ; Log in failed
    {:session session
     :headers {"Content-Type" "text/html"}
     :body (if (or (success?) (:in-as session)) (redirect "/") (login session))}))

(defpage register "Register"
  (form-to [:post "/register"]
           (text-field "username" "Username")
           (password-field "password" "Password")
           (text-field "email" "Email")
           (submit-button "Register")))

(defn register-handler [{:keys [session params]}]
  (let [{:strs [username]} params
        response (user/register params)
        success? #(= :registration-success response)
        session (cond (not username) session
                      (success?) (assoc session :response response)
                      :else (assoc session :response response))]
    {:session session
     :headers {"Content-Type" "text/html"}
     :body (if (success?) (redirect "/") (register session))}))

(defn logout-handler [{:keys []}]
  {:session {}
   :headers {"Content-Type" "text/html"}
   :body (redirect "/")})

(defn confirmation-handler [{:keys [session uri]}]
  {:session (assoc session :response (user/validate uri))
   :headers {"Content-Type" "text/html"}
   :body (redirect "/")})

(defpage not-found "Invalid URL"
  "Page not found.")

(defn not-found-handler [{:keys [session]}]
  {:session (dissoc session :response)
   :headers {"Content-Type" "text/html"}
   :body (not-found session)})

(def routes (app (wrap-session)
                 (wrap-file "resources/public")
                 (wrap-params)
                 (wrap-stacktrace)
                 ["register"] register-handler
                 ["login"] login-handler
                 ["confirm" username id] confirmation-handler
                 ["logout"] logout-handler
                 [&] not-found-handler))
