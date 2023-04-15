# daily

[daily](https://trydaily.click) is an app for organizing everyday life.
<br>
Technically speaking, this is a "monorepo" with a Spring Boot backend and a Quasar (Vue.js)
frontend.
<br>
The backend exposes both the frontend assets, routed by serving the index file for each request, and
a set
of APIs under a specific context.

#### Build & run with Docker

In order to build and run the app, the following requirements are needed:

- Java 17+
- Maven
- Docker

Follow these instructions:

- From the root of the project, run ```docker-compose -f daily-docker-compose.yml up --build```
  to download and build the required environment
- When Postgres and Redis are ready to accept connections and Keycloak has started, run the
  following Maven
  command to start the
  app: ```mvn clean spring-boot:run "-Dspring-boot.run.jvmArguments=-DGOOGLE_OAUTH2_CLIENT_SECRET=nz754EdKFuBI8kJFF9fYqucW91q6mJV1 -DGOOGLE_OAUTH2_CLIENT_ID=daily -Dspring.security.oauth2.client.registration.google.scope=openid,profile,email -Dspring.security.oauth2.client.provider.google.issuer-uri=http://localhost:8081/realms/daily -Dspring.datasource.url=jdbc:postgresql://localhost:5433/daily_develop" -Duser.timezone=Etc/UTC```
- Visit http://localhost:8080 and use ```user1@gmail.com``` or ```user2@gmail.com``` for
  the username and ```strongpass``` for the password. Just remember the changes will not be
  persisted
  to the
  Postgres instance unless you modify the ```daily-docker-compose.yml``` file to mount an auxiliary
  volume
- Hit ```CTRL+C``` to stop the app from running; you can optionally
  run ```docker-compose -f daily-docker-compose.yml down -v --rmi all``` to remove the leftovers

## Authors

- [@LBLucaBonetti](https://www.github.com/LBLucaBonetti)

## Badges

#### Whole project

[![GitHub license](https://img.shields.io/github/license/LBLucaBonetti/daily)](https://github.com/LBLucaBonetti/daily/blob/main/LICENSE)
[![CodeFactor](https://www.codefactor.io/repository/github/lblucabonetti/daily/badge)](https://www.codefactor.io/repository/github/lblucabonetti/daily)
[![Daily production deploy](https://github.com/LBLucaBonetti/daily/actions/workflows/daily-production-deploy.yml/badge.svg?branch=main)](https://github.com/LBLucaBonetti/daily/actions/workflows/daily-production-deploy.yml)
![Security Headers](https://img.shields.io/security-headers?url=https%3A%2F%2Fwww.trydaily.click)
Badge for Security Headers is broken? Scan
daily [here](https://securityheaders.com/?q=https%3A%2F%2Ftrydaily.click&followRedirects=on)

#### Frontend

[![Quality gate status (frontend)](https://sonarcloud.io/api/project_badges/measure?project=LBLucaBonetti_daily_fe&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=LBLucaBonetti_daily_fe)

#### Backend

[![Quality gate status (backend)](https://sonarcloud.io/api/project_badges/measure?project=LBLucaBonetti_daily_be&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=LBLucaBonetti_daily_be)
