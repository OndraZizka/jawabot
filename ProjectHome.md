# News #

  * Released 2.1.4. Jira plugin configuration improvements.
  * Added [JBoss AS 7 / WildFly / EAP CLI](https://community.jboss.org/people/ozizka/blog/2013/07/16/collaborative-jboss-as-7-wildfly-eap-management-over-irc) plugin.
  * Finished 2.1.1, which uses [PircBot 1.7](https://github.com/OndraZizka/PircBot).
  * Finished 2.0.0.Final, which has all the IRC functionality working.

Web UI is currently broken due to [SOLDER-339](https://issues.jboss.org/browse/SOLDER-339), will be fixed in 2.5 by replacing with [DeltaSpike](http://deltaspike.apache.org/documentation.html).

Version 3.x will use [PircBotX](https://code.google.com/p/pircbotx/) instead of PircBot.


# About #

Originally it was an IRC bot for reservation of shared resources (lab computers).

Currently it has evolved into a pluggable modularized configurable standalone application with IRC and web frontend.

  * Pluggable - plugins can handle IRC events and also publish their content on web. Also, they can bring in their JPA entities.

  * Modularized - you can disable either module (Web or IRC) just by dropping it from classpath, and only use plugins which don't need it.

  * Standalone - does not need any server of any kind - just JDK 1.6.

  * Configurable - modules config are in an XML file. Plugin's data are usually in JPA (but you may store anything wherever you like).

  * Persistent - plugins may transparently use JPA 1.0 (plan to move to JPA 2.0)

Current plugins:

  * Logger - logs IRC events, shows the history in Web UI
    * Also provides services to other plugins
  * PasteBin - let's users store a long text over PM and then puts the link to the channel.
  * WhereIs - scans all channels for users and to search for them using wildcards
  * AutoOp - automatically makes users operators; also asks for op if it does not have it.
  * Social - reacts on various social protocol tokens like "thanks etc" (For Rado Husar :)
  * Jira   - scans messages for Jira IDs like AS7-1234 and shows basic one-line info.
  * Messenger - stores a message for non-present user to deliver when he/she connects.
  * ReplayChat - replays dialog, only from one user. Makes a lot of fun :)
  * JBoss AS CLI - for collaborative JBoss AS / [EAP](http://www.jboss.org/products/eap) / [WildFly](http://wildfly.org/) management.
  * Reservation - quick exclusive resource reservation over IRC.