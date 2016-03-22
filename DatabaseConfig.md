# Database Configuration #

Currently, the database is created on-the-fly.
That will change in the future, but currently, you only need to create the database like this:

```
CREATE SCHEMA `jawabot` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
GRANT ALL ON jawabot.* TO jawabot IDENTIFIED BY 'jawabot';
FLUSH PRIVILEGES;
```