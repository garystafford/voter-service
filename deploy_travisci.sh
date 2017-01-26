#!/bin/sh

# Builds and deploys JAR build artifact to GitHub (acts as binary repository)
# Builds immutable Docker Image, deploying the JAR, above.

# Uses encrypted environment variables in .travis.yml file used here
# travis encrypt GH_TOKEN=<your_token_hash> --add
# travis encrypt COMMIT_AUTHOR_EMAIL=<your_email_here> --add
# travis encrypt GH_ARTIFACT_REPO=github.com/<your_repo_path>.git --add
# travis encrypt DOCKER_USERNAME=<your_username> --add
# travis encrypt DOCKER_PASSWORD=<your_password> --add

#set -x

pushd build/libs
git init
git config user.name "travis-ci"
git config user.email "${COMMIT_AUTHOR_EMAIL}"

git add *.jar
git commit -m "Deploy Travis CI Build #${TRAVIS_BUILD_NUMBER} artifacts to GitHub"
git push --force --quiet "https://${GH_TOKEN}@${GH_ARTIFACT_REPO}" master:build-artifacts

popd
docker login -u="${DOCKER_USERNAME}" -p="${DOCKER_PASSWORD}"

docker build -t garystafford/voter-service:latest .
docker push garystafford/voter-service:latest

IMAGE_TAG="v0.2.${TRAVIS_BUILD_NUMBER}"
docker tag garystafford/voter-service:latest voter-service:${IMAGE_TAG}
docker push voter-service::${IMAGE_TAG}
