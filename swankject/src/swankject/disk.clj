;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Handle disk input/output
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(ns  swankject.disk
  [:use
   [clojure
    [repl                :only [doc find-doc            ]]
    [pprint              :only [pp pprint               ]]]
   [clojure.tools.trace  :only [trace deftrace trace-ns ]]
   [clojure.java.javadoc :only [javadoc                 ]]]
  [:require
   [clojure
    [data                :as d   ]
    [inspector           :as ins ]
    [string              :as s   ]
    [test                :as t   ]
    [walk                :as w   ]
    [xml                 :as xml ]
    [zip                 :as z   ]]
   [clojure.data.xml     :as x   ]
   [clojure.java.shell   :as sh  ]
   [clojure.java.io      :as io  ]]
  [:import
   [java.io                  PushbackReader]])

(defn to-disk!
  "Persist the given object to disk"
  [obj filename]
  (with-open [w (io/writer filename)]
    (binding [*out*       w
              *print-dup* true]
      (pprint obj))))

(defn from-disk!
  "Read an object from the disk"
  [filename]
  (with-open [r (PushbackReader. (io/reader filename))]
    (read r)))

(defn- home
  [] (System/getProperty "user.home"))

(defn to-disk!-
  "Persist the given object to disk"
  [obj] (to-disk! (str (home) "/pp.clj")))

(defn from-disk!-
  "Read an object from the disk"
  []
  (from-disk!  (str (home) "/pp.clj")))
