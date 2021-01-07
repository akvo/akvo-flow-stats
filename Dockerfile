FROM clojure:tools-deps-alpine
COPY ./deps.edn /app/deps.edn
WORKDIR /app
RUN clojure -e "(System/exit 0)"

ENTRYPOINT ["clojure","-A:nREPL"]
CMD ["-M:nREPL"]
