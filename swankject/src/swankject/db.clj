;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Testing persisting clj datastructures
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(ns  swankject.db
  [:use
   [clojure
    [repl                :only [doc find-doc            ]]
    [pprint              :only [pp pprint print-table   ]]]
   [clojure.tools.trace  :only [trace deftrace trace-ns ]]
   [clojure.java.javadoc :only [javadoc                 ]]
   [table.core           :only [table                   ]]]
  [:require
   [clojure
    [data                :as d   ]
    [inspector           :as ins ]
    [set                 :as set ]
    [string              :as s   ]
    [test                :as t   ]
    [walk                :as w   ]
    [xml                 :as xml ]
    [zip                 :as z   ]]
   [clojure.data.xml     :as x   ]
   [clojure.java.shell   :as sh  ]
   [clojure.java.io      :as io  ]])

(def db (ref []))

(defn- rand-seq [s] (repeatedly #(rand-nth s)))

(def voyel [\a \a \a \e \o ])

(def rand-voyel (rand-seq voyel))

(def conson [\f \f  \f  \h   \l  \l \l \n    \s  ])

(def rand-conson (rand-seq conson))

(defn rand-word
  [] (reduce str (take (+ 2 (rand-int 6))
                       (interleave (rand-seq conson)
                                   (rand-seq voyel)))))

(defn append-stuff
  [d] (reduce (fn [r x] (conj r {(keyword (rand-word)) x}))
              d
              (range 10)))

(defn fill-db)
