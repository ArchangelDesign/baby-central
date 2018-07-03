FROM java:openjdk-8

# make source folder
RUN mkdir -p /app
WORKDIR /app

# copy other source files (keep code snapshot in image)

COPY build/libs/babycentral-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 9001

CMD java -jar /app/app.jar \
--spring.datasource.username=$BABY_CENTRAL_DB_USER \
--spring.datasource.password=$BABY_CENTRAL_DB_PASSWORD \
--one-signal.appId=$BABY_CENTRAL_ONE_SIGNAL_APP_ID \
--one-signal.authorizationId=$BABY_CENTRAL_ONE_SIGNAL_AUTH_ID
