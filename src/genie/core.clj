(ns genie.core
  (:use [genie.pages :only [defpage redirect]]
        [net.cgrand.moustache :only [app]]
        [ring.middleware params file session stacktrace]))

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
                 ["logout"] logout-handler
                 [&] not-found-handler))
