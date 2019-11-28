# KLUBBHUSET
IE303911-Mobile-og-Distribuerte Applikasjoner Prosjektoppgave

Distribuert løsning for medlemsskap i klubber og organisasjoner





## Reqiuired Local Files:
create this file
.\app\src\main\assets\connection.properties

and add the following fields with your own values
host=[address]
port=[portnumber]



## Docker log from server

Log on to the server with ssh

Run following command: 

```bash
docker-compose -f /home/stefhola/klubbhuset/server/docker-compose.yml logs --follow
```

This will show log from all the services. `Ctrl+C` to quit

If you just want to log the one service you can specify it at the end of the command.