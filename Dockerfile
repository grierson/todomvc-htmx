FROM clojure:tools-deps

COPY . /usr/src/app
WORKDIR /usr/src/app

RUN clj -T:build uber

CMD ["java", "-jar", "target/app-standalone.jar"]
