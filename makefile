.PHONY: repl run

repl:
	clj -M:repl:dev:test

run:
	clj -X service.system/start
