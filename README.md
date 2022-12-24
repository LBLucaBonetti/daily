# daily

[daily](https://daily.up.railway.app/) is an app for organizing everyday life.
<br>
Technically speaking, this is a "monorepo" with a Spring Boot backend and a Quasar (Vue.js)
frontend.
<br>
The backend exposes both the frontend assets, by serving the index file for each request, and a set
of APIs under a specific context.

#### Build & run with Docker

In order to build and run the app, you will need:

- Java 17+
- Maven
- Docker
- A Google account

Follow these instructions:

- Use your Google account to follow the tutorial at https://support.google.com/cloud/answer/6158849;
  the important field you need to fill is the ```Authorized redirect URI```:
  put http://localhost:8080/login/oauth2/code/google in and save
- Open the ```daily-docker-compose.yml``` file and replace the ```<google-client-id>```
  and ```<google-client-secret>``` placeholders with the client id and secret Google created for
  your client; save the file
- From the root of the project, run ```mvn clean package -DskipTests``` and wait for the build
  process to complete
- From the root of the project, run ```docker-compose -f daily-docker-compose.yml up``` to
  build/download the Docker images and
  run the app
- When you see the Spring Boot application has correctly started (take a look at the logs),
  open http://localhost:8080 and you should be redirected to your Google client login page; log in
  and you should be redirected back to the app
- Hit ```CTRL+C``` to stop the app from running; you can optionally
  run ```docker-compose -f daily-docker-compose.yml down``` to remove the leftovers

## Authors

- [@LBLucaBonetti](https://www.github.com/LBLucaBonetti)

## Badges

#### Backend & frontend

[![GitHub license](https://img.shields.io/github/license/LBLucaBonetti/daily)](https://github.com/LBLucaBonetti/daily/blob/main/LICENSE)
[![CodeFactor](https://www.codefactor.io/repository/github/lblucabonetti/daily/badge)](https://www.codefactor.io/repository/github/lblucabonetti/daily)
[![Daily production deploy](https://github.com/LBLucaBonetti/daily/actions/workflows/daily-production-deploy.yml/badge.svg?branch=main)](https://github.com/LBLucaBonetti/daily/actions/workflows/daily-production-deploy.yml)

#### Frontend only

[![Quality gate status (frontend)](https://sonarcloud.io/api/project_badges/measure?project=LBLucaBonetti_daily_fe&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=LBLucaBonetti_daily_fe)

#### Backend only

[![Quality gate status (backend)](https://sonarcloud.io/api/project_badges/measure?project=LBLucaBonetti_daily_be&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=LBLucaBonetti_daily_be)
