(ns genie.rss
  (:use [hiccup.core :only [html]]))

(defn feed [title link description & body]
  (html [:rss {:version "2.0"}
         [:channel
          [:title title]
          [:link link]
          [:description description]] body]))

(defn update-feed [username]
  (let [{:keys [updates]} (fetch-user username)
        title (str username "'s Genie updates.")]
    (feed title (str "http://localhost:8080/users/" username) title
          (for [{:keys [time status]} updates]
            [:item [:title time] [:description status]]))))
