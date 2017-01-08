(ns lmenu.dmenu
  (:use
   [ clojure.string :only [ join trim-newline] ]
   [ clojure.java.shell :only [ sh ] ]
   ))

(defn pick
  "Launch dmenu executable"
  [prompt items & {:keys [lines]}]
  (let [
        cmd (map str [ "dmenu" "-p" prompt "-l" lines ])
        lines (str lines)
        items (join "\n" items)
        sh_ret (sh "dmenu" "-p" prompt "-l" lines :in items)
        ]
    (assoc sh_ret :out (trim-newline (:out sh_ret)))
  ))
