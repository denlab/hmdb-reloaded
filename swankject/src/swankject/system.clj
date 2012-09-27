;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Handle system stuff:
;;     - disk input/output
;;     - jvm threads
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(ns  swankject.system
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
   [clojure.java.io      :as io  ]]
  [:import
   [java.io                  PushbackReader]])

;;-----------------------------------------------------------------------------
;; Disk utils
;;-----------------------------------------------------------------------------

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

;;-----------------------------------------------------------------------------
;; Thread utils
;;-----------------------------------------------------------------------------

(defn prn-threads
  "Print the current threads"
  []
  (table (swank.commands.basic/list-threads)))

(defn future-repeat
  "Yield a future that exec the given fn, sleep, exec the fn, etc"
  ([f            ] (future-repeat f 1000))
  ([f sleep-milis]
     {:pre [(fn? f)]}
     (future
       (doseq [f (repeat f)]
         (f)
         (Thread/sleep sleep-milis)))))

(defn watch-threads
  "Yield a future that do in a loop: print the current threads"
  []
  (future-repeat prn-threads))

(defn contains-ignore-case?
  [searching searched]
  (.contains (s/lower-case searched)
             (s/lower-case searching)))

(defn find-thread
  "Takes a list of thread (as per `swank.commands.basic/list-threads` format) and return the threads whose names matches the given string (case insensitive) "
  [pat threads]
  (filter #(contains-ignore-case? pat (second %))
          (next threads)))

(defn watch-shutdown-thread
  "Yield a future that list all the threads whose names contains 'shutdown'"
  []
  (future-repeat (fn []
                   (println (java.util.Date.))
                   (pprint (find-thread "shutdown")))))

(defn thread-info!
  "Impure! Like `swank.commands.basic/list-threads`, but add a reference to the thread itself"
  (let [t-info (swank.commands.basic/list-threads)
        t-list (swank.commands.basic/get-thread-list)]))

(defn thread-action
  "Takes a list of thread (as per `swank.commands.basic/list-threads`),
and return the action to take:
    - {:action :not-found-so-noop} OR
    - {:action :kill :thread-id <thread-id>}"
  [threads] (if-let [[[thread-id & _] & _] (seq (find-thread "shutdown" threads))]
              {:action :kill              :thread-id thread-id}
              {:action :not-found-so-noop                     }))

(defn find-thread-by-id
  "Take a list of Thread, and a thread id and return the thread that have that id"
  [threads id] (first (filter #(= id (.getId %)) threads)))

(defn maybe-kill-shutdown-thread!
  "Side-effects! if a shutdown thread is found, then call (.sleep shutdown-thread Long.MAX_VALUE) on it"
  [] (let [t-info (swank.commands.basic/list-threads)
           t-list (swank.commands.basic/get-thread-list)
           {:keys [action thread-id]} (maybe-kill-shutdown-thread t-info)
           t (find-thread-by-id t-list thread-id)]
       (case action
         :kill              (do (println "Found shutdown thread (id=" thread-id "), now suspending it!")
                                (.suspend t))
         :not-found-so-noop (println "No shutdown thread found"))))


