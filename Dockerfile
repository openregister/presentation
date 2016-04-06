FROM alpine:latest
RUN apk add --no-cache openjdk8
RUN install -d /srv/presentation
COPY build/libs/presentation.jar /srv/presentation/presentation.jar
CMD sh -c java -jar /srv/presentation/presentation.jar server /srv/presentation/config.yaml
