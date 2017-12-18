FROM openjdk:8u151-jdk-alpine
LABEL maintainer="Gary A. Stafford <garystafford@rochester.rr.com>"
ENV REFRESHED_AT 2017-12-17
EXPOSE 8080
RUN set -ex \
  && apk update \
  && apk upgrade \
  && apk add git
RUN mkdir /voter \
  && git clone --depth 1 --branch build-artifacts-gke \
      "https://github.com/garystafford/voter-service.git" /voter \
  && cd /voter \
  && mv voter-service-*.jar voter-service.jar
CMD [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "voter/voter-service.jar" ]
