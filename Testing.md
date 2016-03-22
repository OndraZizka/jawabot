# How to test run the app #

This assumes that you're using the NetBeans IDE which takes care of running etc.
However, if not, thanks to Maven it's still easy.

To run a particular module, build everything (build from the root module) and then run the module. NetBeans meta files are configured so that:

  * It runs `org.jboss.jawabot.JawaBotApp#main()`
  * The working directory is `./workdir` which contains the config files
  * The right config file is specified using the `--config` param.

The config files are copied to the `workdir` from the `configs` modules' artifacts. Either one of them is chosen and copied to `workdir/conf`, or all of them are copied under the respective profile dir, `workdir/conf/<profile>`, and then has to be specified using `--config=...conf/<profile>/JawaBotConfig.xml` (or `-Dconfig=...`).