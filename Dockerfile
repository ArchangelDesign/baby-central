FROM java:openjdk-8

# make source folder
RUN mkdir -p /app
WORKDIR /app

# copy other source files (keep code snapshot in image)

COPY build/libs/babycentral-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 9001

CMD java -jar /app/app.jar