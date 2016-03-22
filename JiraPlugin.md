# Usage #

This plugin reacts on known Jira prefixes in the messages.

```
(05:36:32 PM) zizka: AS7-1234
(05:36:37 PM) JawaBot2-beta: [#AS7-1234] Hibernate does not read/use metadata for orm.xml defined entites [Resolved, Major, Stuart Douglas] https://issues.jboss.org/browse/AS7-1234

(11:54:50 AM) ozizka-ntb: JBPAPP-9594 JBPAPP-9518 MJAVACC-35
(11:54:55 AM) JawaBot2-beta: Issue JBPAPP-9594 is secured. I can't login. https://issues.jboss.org/browse/JBPAPP-9594
(11:54:55 AM) JawaBot2-beta: [#JBPAPP-9518] Upgrade jbosssx to 2.0.5 [Open, Critical, Martha Benitez] https://issues.jboss.org/browse/JBPAPP-9518
(11:54:58 AM) JawaBot2-beta: [#MJAVACC-35] If input and output directory are equal then input files are emptied [Closed, Major, Paul Gier] http://jira.codehaus.org/browse/MJAVACC-35
```

Configuration: see http://code.google.com/p/jawabot/source/browse/assem-appassemble/conf/JawaBotConfig-plugin-jira.xml