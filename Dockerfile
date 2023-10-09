FROM clojure:tools-deps

COPY . /usr/src/app
WORKDIR /usr/src/app

RUN clj -T:build uber

EXPOSE 3000

CMD ["java", "-jar", "target/app-standalone.jar"]
