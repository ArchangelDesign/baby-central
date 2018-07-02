docker stop babycentral-container
docker rm babycentral-container
docker rmi babycentral

./gradlew assemble
docker build --tag=babycentral:latest .

docker run \
-p 9001:9001 \
--net="host" \
--name babycentral-container \
babycentral