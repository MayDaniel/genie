(ns genie.rss
  (:use [hiccup.core :only [html]]))

(defn feed [title link description & body]
  (html [:rss {:version "2.0"}
         [:channel
          [:title title]
          [:link link]
          [:description description]] body]))
