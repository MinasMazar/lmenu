# lmenu

This is my personal journey through [ Clojure ](https://clojure.org/).

 Lmenu is a wrapper around [dmenu](https://wiki.archlinux.org/index.php/dmenu) a pop-up tool to do almost everything..
 
## The end of the journey

If you run lmenu.core/-main you'll get 

~~~
$ lein run
Received wake code: default
Picked element with label < Exec firefox >
The item selected is [Exec firefox (fn* [] (sh firefox))]
Exception in thread "main" java.lang.RuntimeException: Unable to resolve symbol: sh in this context, compiling:(/tmp/form-init8966476250419733582.clj:10:49)
        at clojure.lang.Compiler.analyze(Compiler.java:6688)
        at clojure.lang.Compiler.analyze(Compiler.java:6625)
        at clojure.lang.Compiler$InvokeExpr.parse(Compiler.java:3766)
        at clojure.lang.Compiler.analyzeSeq(Compiler.java:6870)
        at clojure.lang.Compiler.analyze(Compiler.java:6669)
        at clojure.lang.Compiler.analyze(Compiler.java:6625)
        at clojure.lang.Compiler$BodyExpr$Parser.parse(Compiler.java:6001)

~~~

Cannot move on from this issue.. how can *eval* evaluate the code within the *lmenu.core* namespace?

~~~clojure
(ns lmenu.core
  (:require [lmenu.dmenu :as dmenu]
            [clojure.string :as str])
  (:use [lmenu.debug]
        [clojure.java.shell :only [ sh ] ])
  (:gen-class))

(def root-menu "Root items" [
                             [ "Add 1 2 3" '(+ 1 2 3) ]
                             [ "Exec firefox" '#(sh "firefox") ] ; <-- the anonymous function does not access to sh function from clojure.java.shell
                             ])

(defn eval-item
  "Eval item"
  [str]
  (if (string? str)
    (eval (read-string) str)
    (eval str )))
~~~
