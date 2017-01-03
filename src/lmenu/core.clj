(ns lmenu.core
  (:require [lmenu.dmenu :as dmenu]
            [clojure.java.shell :only [ sh ] ]
            [clojure.string :as str])
  (:gen-class))

(def root-menu "Root items" [
                             [ "root1" (+ 1 2 3) ]
                             ])
(defmacro eval-str
  "Eval string"
  [str]
  '(eval (read-string ~str)))

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

(defn -main
  "Entry point for lmenu app"
  [& args]
  (loop [ wake_code (str/trim-newline (slurp "/home/minasmazar/.rmenu_waker"))]
    (println wake_code)
    (if (= wake_code "default")
      (let [
            items (map first root-menu)
            res (dmenu/pick "prompt" items :lines 11)
            picked (str/trim-newline (:out res) )
            item (filter #(= picked (first %)) root-menu)
            ]
        (println "Picked element with label <" picked ">")
        (println "The item selected is" item )
        (secure-exec
         (let [ ret (eval-str (second item))]
           (println "Evaluation of <" (second item) "> returned: " ret)
           ))
        (recur (slurp "/home/minasmazar/.rmenu_waker")))))
  (System/exit 0))
