(ns genie.pages
  (:use [hiccup.core :only [html]]
        [hiccup.page-helpers :only [doctype link-to include-css include-js]]))

(def links {:base {"/" "Home", "/users" "Users", "/tags" "Tags", "/search" "Search"}
            :out {"/register" "Register", "/login" "Log in"}
            :in {"/settings" "Settings", "/logout" "Log out"}})

(defn render-links [{:keys [in-as]}]
  (map (fn [[link name]] (link-to link name))
       (apply merge (map links [(if in-as :in :out) :base]))))

(defn redirect [url]
  (html [:meta {:http-equiv "Refresh" :content (str "0;url=" url)}]))

(defmacro make-page [title & body]
  `(html (:html4 doctype)
         [:head [:title ~title]
          (include-css "/css/genie.css")
          (include-js "/javascript/genie.js")]
         (render-links ~'session) ~@body))

(defmacro defpage [name & args]
  {:arglists '([name title? argseq? & body])}
  (let [title (if (string? (first args)) (first args) (str name))
        body (if (string? (first args)) (rest args) args)
        argseq (if (and (vector? (first body)) (not (keyword? (ffirst body))))
                 (first body) [])
        body (if (seq argseq) (rest body) body)]
    `(defn ~name [~'session ~@argseq]
       (make-page ~title ~@body))))
