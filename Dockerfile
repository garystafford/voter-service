FROM openjdk:8-jdk-alpine
LABEL maintainer "Gary A. Stafford <garystafford@rochester.rr.com>"
ENV REFRESHED_AT 2018-05-04
EXPOSE 8080
RUN set -ex \
  && apk update \
  && apk upgrade \
  && apk add git
RUN git clone --depth 1 --branch build-artifacts \
      "https://github.com/garystafford/voter-service.git" \
  && mv /voter-service/voter-service-*.jar /tmp/voter-service.jar \
  && rm -rf voter-service
WORKDIR /tmp/
CMD [ "java", "-Dspring.profiles.active=docker-development", "-Djava.security.egd=file:/dev/./urandom", "-jar", "voter-service.jar"]
