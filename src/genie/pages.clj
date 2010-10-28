(ns genie.pages
  (:use [genie.constants :only [responses]]
        [hiccup.core :only [html]]
        [hiccup.page-helpers :only [doctype link-to include-css include-js]]))

(defn links [logged-in?]
  (merge (if logged-in?
           {"/settings" "Settings" "/logout" "Log out"}
           {"/login" "Log in" "/register" "Register"})
           {"/search" "Search" "/tags" "Tags" "/users" "Users" "/" [:b "Genie"]}))

(defn render-links [{:keys [in-as]}]
  [:tr (for [[link name] (interpose " | " (links in-as))]
         [:td (link-to link name)])])

(defn redirect [url]
  (html [:meta {:http-equiv "Refresh" :content (str "0;url=" url)}]))

(defn check-response [{:keys [response]}]
  (when-let [response (responses response)]
    [:div#dialog {:title "Response"}
     [:p response]]))

(defmacro make-page [title & body]
  `(html (:html4 doctype)
         [:head [:title ~title]
          (include-css "/css/genie.css"
                       "/css/jquery-ui.css")
          (include-js  "/javascript/genie.js"
                       "/javascript/jquery.js"
                       "/javascript/jquery-ui.js")]
         (check-response ~'session)
         [:table (render-links ~'session) ~@body]))

(defmacro defpage [name & args]
  {:arglists '([name title? argseq? & body])}
  (let [title (if (string? (first args)) (first args) (str name))
        body (if (string? (first args)) (rest args) args)
        argseq (if (and (vector? (first body)) (not (keyword? (ffirst body))))
                 (first body) [])
        body (if (seq argseq) (rest body) body)]
    `(defn ~name [~'session ~@argseq]
       (make-page ~title ~@body))))
