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
  ([jdate] (let [time (juxt hour minute sec)
                 date (juxt day (comp months month) year)]
             (str (join \space (date jdate)) \space
                  (join \: (time jdate))))))

(defn string->date [date]
  (let [[day month year hour minute sec] (split date #":| ")
        month (str ((map-invert months) month))]
    (->> [year month day hour minute sec]
         (map read-string)
         (apply date-time))))
