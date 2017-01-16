FROM java:openjdk-8u111-jdk

MAINTAINER Gary A. Stafford <garystafford@rochester.rr.com>

ENV REFRESHED_AT 2017-01-16

RUN set -ex \
  && sleep 240 \
  && apt-get -y update \
  && apt-get -y upgrade \
  && apt-get -y install git \
  && mkdir /voter \
  && git clone --depth 1 \
    "https://github.com/garystafford/voter-service-artifacts.git" \
    /voter

RUN set -ex \
  && cd /voter \
  && mv voter-service-*.jar voter-service.jar

CMD ["java", "-Dspring.profiles.active=docker-development", "-Djava.security.egd=file:/dev/./urandom", "-jar", "voter/voter-service.jar"]
