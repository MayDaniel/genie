(ns run (:use [ring.adapter.jetty :only [run-jetty]]
              [genie.core :only [routes]]))

(defonce server (run-jetty #'routes {:port 8080}))
