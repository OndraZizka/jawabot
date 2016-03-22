# How to run JawaBot #

JawaBot is distributed as a `JawaBot-<version>-dist.zip` which contains:

  * `JawaBot-<version>-dist/bin` - scripts to run
  * `JawaBot-<version>-dist/conf` - config files
  * `JawaBot-<version>-dist/lib` - jar files.

To run, you need to specify the config file to be used.

```
bin/run --config=conf/freenode-dist/JawaBotConfig.xml
```

For windows:

```
bin\run.bat --config=conf\freenode-dist\JawaBotConfig.xml
```

All the `--param` params may also be specified as java sys prop. So in case you run `java` on your own (omit the scripts), this way is possible:

```
java -cp $(echo `ls -1 *.jar` | sed 's/\W/:/g') -Dconfig=conf/freenode-dist/JawaBotConfig.xml org.jboss.jawabot.JawaBotApp
```