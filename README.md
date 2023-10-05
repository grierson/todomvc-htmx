# TODOMVC (HTMX + Clojure)

- `make run` - Runs application on `localhost:3000` by default
  - `clj -X service.system/start :port 4000` to override from cli
  - update `resources/config.edn` to provide different port via config
- `make repl` - Start repl

## Tools

- Clojure
- HTMX + Hyperscript
- Ring + Reitit
- Donut.system
