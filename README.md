# PimpedSync

---

Welcome to my syncing system for Minecraft servers between them.
In each release 2 jars will be contained, which you will need to include in your plugins folder.
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
```json
{
  "url": "jdbc:mysql://127.0.0.1:3306/minecraft",
  "driver": "com.mysql.jdbc.Driver",
  "user": "root",
  "password": "password"
}
```
or
```json
{
  "url": "jdbc:mysql://127.0.0.1:3306/minecraft",
  "driver": "org.postgresql.Driver",
  "user": "root",
  "password": "password"
}
```

---
## How to get help
Simply click [this](https://github.com/cancel-cloud/PimpedSync/issues/new) and ill try to help you / fix the problem.