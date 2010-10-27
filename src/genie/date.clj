(ns genie.date
  (:use [clojure.string :only [join split]]
        [clj-time.core :only [now day month year hour minute sec date-time]]
        [clojure.set :only [map-invert]]
        [clojure.contrib.def :only [defalias]]))

(defalias date now)

(def months (zipmap (iterate inc 1)
                    ["January" "February" "March"
                     "April" "May" "June"
                     "July" "August" "September"
                     "October" "November" "December"]))

(defn date->string
  ([] (date->string (date)))
  ([date] (format "%s  %s"
            (join " " ((juxt day (comp months month) year) date))
            (join ":" ((juxt hour minute sec)              date)))))

(defn string->date [date]
  (let [[day month year hour minute sec]
        (map read-string (split date #":| "))
        month ((map-invert months) (str month))]
    (date-time year month day hour minute sec)))
