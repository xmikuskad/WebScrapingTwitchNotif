# Test of twitch API and web browsing using java
This is test of twitch API and headless java browser using htmlunit.

### Twitch API
In this part we check if streamer with ID in config file has come online in last 30 seconds. Then it saves stream and channel information to POJO classes. This is running on second thread and check periodically.

Initial use is for my discord bot.

### Web scraping
This is used to browse site https://login.uniba.sk, enter login info and login. Afterwards we save cookie and login information and call HTML GET in plain javascript code. This way we simulate searching in students of university and can their found name, surname and email. This is ugly workaround and is not recommended.

Initial use is for my authorization of students in discord.

### Config file example
ID=21210212 //twitch ID - first numbers in streamer key
CLIENT_ID=XXXXX //your app id
AUTH=XXXXX //your app auth code
NAME=XXXXX //login info for website
PASSWORD=XXXX //login info for website
