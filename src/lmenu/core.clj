(ns lmenu.core
  (:require [lmenu.dmenu :as dmenu]
            [clojure.string :as str])
  (:use [lmenu.debug]
        [clojure.java.shell :only [ sh ] ])
  (:gen-class))

(def root-menu "Root items" [
                             [ "Add 1 2 3" '(+ 1 2 3) ]
                             [ "Exec firefox" `(sh "firefox") ]
                             ])

;; (binding [*ns* lmenu.core] (eval (read-string str)) )
(defn eval-item
  "Eval item"
  [str]
  (if (string? str)
    (eval (read-string) str)
    (eval str )))

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

(defn interpole-string
  "Interpole string, replacing every __TOKEN__ with user input via dmenu"
  [str]
  (reduce #(str/replace %1 %2 (:out (lmenu.dmenu/pick %2 [] ) ) ) str (re-seq #"_.+?_" str))
  )

(defn -main
  "Entry point for lmenu app"
  [& args]
  (loop [ wake_code (str/trim (slurp "/home/minasmazar/.rmenu_waker"))]
    (println "Received wake code:" wake_code)
    (if (or (= wake_code "default") true)
      (let [
            items (map first root-menu)
            res (dmenu/pick "prompt" items :lines 11)
            picked (str/trim-newline (:out res) )
            item (first (filter #(= picked (first %)) root-menu) )
            value (second item)
            ]
        (println "Picked element with label <" picked ">")
        (println "The item selected is" item )
        (if value
          (if (seq? value)
            (secure-exec
             (let [ ret (eval value)]
               (println value)
               (println "Evaluation of <" value "> returned: " ret)
               ) false)
            (if (string? value)
              (println "value is a string")
              (println "values is not a string")
              ;; (sh value)
              ))))
      (println "Unable to resolve wake code " wake_code))
    (recur (slurp "/home/minasmazar/.rmenu_waker")))
  (System/exit 0))
