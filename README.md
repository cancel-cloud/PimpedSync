# PimpedSync

---

Welcome to my syncing system for Minecraft servers between them.
In each release 2 jars will be contained, which you will need to include in your plugins' folder.
<br>

---
## How to configure the database connection
Go in the home directory of the server -> `JETData` -> `#InvSyncer` and then you can configure the
``config.json``.
<br>
There you can change the:
- url
- driver
- user
- password

**Important:**
<br>
If you want to use `localhost` you'll have to write `127.0.0.1`.

e.g.:
```
{
  "url": "jdbc:mysql://127.0.0.1:3306/minecraft",
  "driver": "com.mysql.jdbc.Driver",
  "user": "root",
  "password": "password"
}
```
or
```
{
  "url": "jdbc:mysql://127.0.0.1:3306/minecraft",
  "driver": "org.postgresql.Driver",
  "user": "root",
  "password": "password"
}
```

---
## How to use the plugin
In this section ill explain, how you can use the plugin to your advantage.
### Commands
The plugin has a command system, which you can use to control the plugin. The base command is:
<br>
`/invsync`
<br>
After that you have the following attributes:
- `/view`
- `/cache`

#### /view `playername`
with that you'll be able to view the inventory contents of the player. Ether he is online or not.
If he is online, the inventory will be shown directly, or it will be fetched from the database.
After that you can see the items in the inventory. If you're finished, you can close the inventory.
And its content will be saved again.

#### /cache `clear / update`
With that you can clear the cache or update the cache.
<br>
You could need that if you´re database disconnected and the completion isn't correct.

---
## How to get help
Simply click [this](https://github.com/cancel-cloud/PimpedSync/issues/new) and ill try to help you / fix the problem.