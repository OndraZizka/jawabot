

JawaBot
*******

Please keep in mind that this is my sand-box for technologies I learn.

Architecture
============


* Core - CDI (Weld SE) as plugin framework, JPA Persistence Unit (Hibernate)
* Modules:
   * web - Wicket on Jetty embedded.
   * IRC - PircBot.

* Plugins:
   * IRC part (IRC plugin hooks),
       * Packages named org.jboss.jawabot.plugin.*.irc
   * web part (Wicket pages and components), 
       * Packages named org.jboss.jawabot.plugin.*.web
   * common part (business logic, services, JPA entities + DAOs)
       * Packages named org.jboss.jawabot.plugin.*[.common]
       * public class *IrcPluginHook implements IIrcPluginHook<TypeOfObjectPassedAsConfiguration>


