## DEPENDENCIES

- OpenJDK 11
- Payara 5.193
- MySQL 5.7.28
- mysql-connector-java-5.1.48

### NOTE:
MySQL-Connector must be placed in 
- payara-5.193.1\payara5\glassfish\domains\domain1\lib
- payara-5.193.1\payara5\glassfish\lib

## JWT
https://www.eclipse.org/community/eclipse_newsletter/2017/september/article2.php

## Docker-Compose
To run project from docker run the following command from inside
`server/`: `docker-compose up --build -d`.

If you want to log what is happening just skip the `-d` or run
`docker log --follow <container-name>`

The database is presisted to a docker volume named `server_my-db`.
If there is a need to reset the db this volume needs to be removed.
This can be done by `docker volume rm server_my-db`.