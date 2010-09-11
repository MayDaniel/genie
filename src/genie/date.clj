(ns genie.date
  (:use [clojure.string :only [join split]]
        [clj-time.core :only [now day month year hour minute sec date-time]]
        [clojure.set :only [map-invert]]
        [clojure.contrib.def]))

(def months (zipmap (iterate inc 1)
                    ["January" "February" "March"
                     "April" "May" "June"
                     "July" "August" "September"
                     "October" "November" "December"]))

(defalias date now)

(defn date-to-string
  ([] (date-to-string (now)))
  ([date] (let [date- (join " " ((juxt day #(months (month %)) year) date))
                time- (join ":" ((juxt hour minute sec) date))]
            (str date- " " time-))))

(defn string-to-date [date]
  (let [[day month year hour minute second] (split date #":| ")
        month (str ((map-invert months) month))]
    (apply date-time (map read-string [year month day hour minute second]))))
