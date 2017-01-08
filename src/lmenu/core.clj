(ns lmenu.core
  (:require [lmenu.dmenu :as dmenu]
            [clojure.string :as s])
  (:use [lmenu.debug]
        [clojure.java.shell :only [ sh ] ])
  (:gen-class))

(def root-menu "Root items" [
                             [ "Add 1 2 3" '(+ 1 2 3) ]
                             [ "Exec firefox" "firefox" ]
                             [ "Take screenshot (Dropbox)" "i3-scrot" ]
                             [ "Search on wiki" `( open-url ( normalize-url ( interpolate-string "https://it.wikipedia.org/w/index.php?search=__SEARCH ON WIKI__"))) ]
                             ])

(defn add-item
  "Add item to root-menu"
  [item]
  (cons item root-menu))

(defmacro secure-exec
  "Exec in se~cure mode, catching exceptions"
  ([f]
  ('secure-exec f 'true))
  ([f secure]
  `(if ~secure
    (try
      ~f
      (catch Exception e# (str "exception catched: " (.getMessage e#))))
    ~f)))

(defn open-url
  "Open browser at URL given"
  [url]
  (sh "firefox" url
  ))

(defn normalize-url
  "Transform a vector of words to a query string"
  [url]
  (clojure.string/replace url #"\s" "+")
  )

(defn interpolate-string
  "Interpole string, replacing every __TOKEN__ with user input via dmenu"
  [str]
  (reduce #(s/replace %1 %2 (:out (lmenu.dmenu/pick %2 [] ) ) ) str (re-seq #"__.+?__" str))
  )

(defn execute-seq
  "Evaluate seq"
  [seq-to-eval]
  (secure-exec
   (let [ret (eval seq-to-eval)]
     (println "Evaluation of <" seq-to-eval "> returned: " ret)
     ) false)
  )

(defn execute-string
  "Execute string command via shell, interpolate throught __TOKENS__"
  [str]
  (println "Executing <" str "> via shell subprocess")
  (apply sh (s/split str #" " )))

(defn -main
  "Entry point for lmenu app"
  [& args]
  (loop [ wake_code (s/trim (slurp "/home/minasmazar/.rmenu_waker"))]
    (println "Received wake code:" wake_code)
    (if (or (= wake_code "default") true)
      (let [
            items (map first root-menu)
            res (dmenu/pick "prompt" items :lines 11)
            picked (s/trim (:out res) )
            item (first (filter #(= picked (first %)) root-menu) )
            value (second item)
            ]
        (println "Picked element with label <" picked ">")
        (println "The item selected is" item )
        (if (and item (not (s/blank? picked)))
          (if (seq? value)
            (execute-seq value)
            (when (and (string? value) (not (s/blank? (second item) )))
              (execute-string (interpolate-string value))))
          ;; If input was not in the item list, just execute the command via shell; interpolate first
          (when (and (string? picked) (not (s/blank? picked)))
            (execute-string (interpolate-string picked)))))
      (println "Unable to resolve wake code " wake_code))
    (recur (slurp "/home/minasmazar/.rmenu_waker")))
  (System/exit 0))
