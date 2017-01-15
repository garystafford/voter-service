FROM java:openjdk-8u111-jdk

MAINTAINER Gary A. Stafford <garystafford@rochester.rr.com>

ENV REFRESHED_AT Sat Jan 14 22:44:15 EST 2017

RUN set -ex \
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

CMD ["java", "-Dspring.profiles.active=docker-local", "-Djava.security.egd=file:/dev/./urandom", "-jar", "voter/voter-service.jar"]
