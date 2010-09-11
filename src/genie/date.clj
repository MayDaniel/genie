(ns genie.date
  (:use [clojure.string :only [join split]]
        [clj-time.core :only [now day month year hour minute sec date-time]]
        [clojure.set :only [map-invert]]
        [clojure.contrib.def]))

(defalias date now)

(def months (zipmap (iterate inc 1)
                    ["January" "February" "March"
                     "April" "May" "June"
                     "July" "August" "September"
                     "October" "November" "December"]))

(defn date-to-string
  "Returns a string of a date in the format \"11 September 2010 18:41:32\"
   If no date is supplied, it defaults to now."
  ([] (date-to-string (now)))
  ([date] (let [date- (join " " ((juxt day #(months (month %)) year) date))
                time- (join ":" ((juxt hour minute sec) date))]
            (str date- " " time-))))

(defn string-to-date
  "Returns an org.joda.time.DateTime from a date that's been
   represented as a string by \"date-to-string\"."
  [date]
  (let [[day month year hour minute second] (split date #":| ")
        month (str ((map-invert months) month))]
    (apply date-time (map read-string [year month day hour minute second]))))
