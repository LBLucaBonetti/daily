# daily

> [!WARNING]
> This project is currently archived. No updates (security, bugs, features, dependencies) will be made. I want to focus on something else. I was working on the <a href="https://github.com/LBLucaBonetti/daily/tree/GH-867">GH-867</a> branch of the <a href="https://github.com/LBLucaBonetti/daily/tree/GH-864_Budget_and_expenses_feature">GH-864_Budget_and_expenses_feature</a> feature. You can track the project status as it was archived <a href="https://github.com/users/LBLucaBonetti/projects/1">here</a>.

[daily](https://trydaily.click) is an app for organizing everyday life.
<br>
Technically speaking, this is a "monorepo" with a Spring Boot backend and a Quasar (Vue.js)
frontend.
<br>
The backend exposes both the frontend SPA assets, routed by serving the index file for each request,
and
a set
of APIs under a specific context. The login and signup routes are the only public ones.

#### Build & run with Docker

To build and run the app, the following requirements are needed:

- Java 21+
- Maven
- Docker

Follow these instructions:

- From the root of the project, run ```docker compose -f daily-docker-compose.yml up --build -d```
  to download, build and run the required environment
- Check the status of the containers with ```docker compose -f daily-docker-compose.yml ps``` and
  wait until every listed item is ```healthy``` (this may take a while), then run the following
  Maven command to start the
  app: ```mvn clean spring-boot:run "-Dspring-boot.run.jvmArguments=-DGOOGLE_OAUTH2_CLIENT_SECRET=nz754EdKFuBI8kJFF9fYqucW91q6mJV1 -DGOOGLE_OAUTH2_CLIENT_ID=daily -DSPRING_MAIL_HOST=localhost -DSPRING_MAIL_PORT=1025 -Dspring.security.oauth2.client.registration.google.scope=openid,profile,email -Dspring.security.oauth2.client.provider.google.issuer-uri=http://localhost:8081/realms/daily -Dspring.datasource.url=jdbc:postgresql://localhost:5433/daily_develop -Duser.timezone=Etc/UTC"```
- Visit http://localhost:8080 and use ```user1@gmail.com``` or ```user2@gmail.com``` for the
  username and ```strongpass``` for the password; you can also sign a new app user up, if you want;
  in that case, be aware that every e-mail sent is locally redirected to the MailHog instance, and
  you can check http://localhost:8025 to open the local e-mail client. Remember the changes will not
  be persisted to the Postgres instance unless you modify the ```daily-docker-compose.yml``` file to
  mount an auxiliary volume
- Hit ```CTRL+C``` to stop the app from running; you can optionally
  run ```docker-compose -f daily-docker-compose.yml down -v --rmi all``` to remove the leftovers

## Authors

- [@LBLucaBonetti](https://www.github.com/LBLucaBonetti)

## Badges

#### Whole project

[![GitHub license](https://img.shields.io/github/license/LBLucaBonetti/daily)](https://github.com/LBLucaBonetti/daily/blob/main/LICENSE)
[![CodeFactor](https://www.codefactor.io/repository/github/lblucabonetti/daily/badge)](https://www.codefactor.io/repository/github/lblucabonetti/daily)
[![Daily production deploy](https://github.com/LBLucaBonetti/daily/actions/workflows/daily-production-deploy.yml/badge.svg?branch=main)](https://github.com/LBLucaBonetti/daily/actions/workflows/daily-production-deploy.yml)
<br>
[Security Headers](https://securityheaders.com/?q=https%3A%2F%2Ftrydaily.click&followRedirects=on)

#### Frontend

[![Quality gate status (frontend)](https://sonarcloud.io/api/project_badges/measure?project=LBLucaBonetti_daily_fe&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=LBLucaBonetti_daily_fe)

#### Backend

[![Quality gate status (backend)](https://sonarcloud.io/api/project_badges/measure?project=LBLucaBonetti_daily_be&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=LBLucaBonetti_daily_be)
