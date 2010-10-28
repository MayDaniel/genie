(ns genie.pages
  (:use [genie.constants :only [responses]]
        [hiccup.core :only [html]]
        [hiccup.page-helpers :only [doctype link-to include-css include-js]]))

(defn render-links [{:keys [in-as]}]
  (interpose " | " (map (fn [[link name]] (link-to link name))
    (merge (if in-as {"/settings" "Settings" "/logout" "Log out"}
                     {"/login" "Log in" "/register" "Register"})
           {"/search" "Search", "/tags" "Tags", "/users" "Users", "/" [:b "Genie"]}))))

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
         [:header (render-links ~'session)]
         (check-response ~'session)
         [:body ~@body]))

(defmacro defpage [name & args]
  {:arglists '([name title? argseq? & body])}
  (let [title (if (string? (first args)) (first args) (str name))
        body (if (string? (first args)) (rest args) args)
        argseq (if (and (vector? (first body)) (not (keyword? (ffirst body))))
                 (first body) [])
        body (if (seq argseq) (rest body) body)]
    `(defn ~name [~'session ~@argseq]
       (make-page ~title ~@body))))
