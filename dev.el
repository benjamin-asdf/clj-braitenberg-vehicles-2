(let ((cider-clojure-cli-aliases "backend:dev:repl:test"))
  (cider-jack-in-clj '(:project-type clojure-cli)))

(let ((shell-command-buffer-name-async "*braiten-shadow*"))
  ;; ------------------------------------------------------
  (async-shell-command
   "npx shadow-cljs watch app"))


(let ((shell-command-buffer-name-async "*braiten-db*"))
  ;; ------------------------------------------------------
  (async-shell-command
   "docker compose up"))
