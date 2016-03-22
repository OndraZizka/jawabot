# Source files #

Currently, they are scattered amongst various modules.
I want to move it to a single place under root - `configs` or so.
Then the modules would copy the configs from there.

# Parsing #

Currently parsed by JAXB (standard from JDK).

There are two places searched for config files:

  1. Bundled within a jar - i.e. on classpath.
  1. Somewhere in a directory, by default `conf/${profile}/JawaBot-config.xml`

# Structure #

Separating main config (servers etc.) and plugin configs is in process.

```
<jawabotConfig>

   <settings
      unsecuredShutdown = "false"
      adminUser = "ozizka"

      smtpHost = "smtp.corp.redhat.com"
      announceEmailTo = "jboss-qa-brno@redhat.com"
      announceEmailFrom = "jawabot-no-reply@redhat.com"
      
      verbose = "false"
      leaveOnAsk = "true"
      messageDelay = "200"
      acceptInvitation = "true"
      announceDefaultChannel = "#jbosssoaqa"
      debugChannel = "#some"
   />
   <!-- TODO: Move to <irc ... > -->

   <irc>
      <defaultNick>JawaBot-beta</defaultNick>
      <defaultNick2>JawaBot-beta_</defaultNick2>
      <server host="porky.stuttgart.redhat.com">
         <autoJoinChannels>
            #some
            <!--
            #jboss-qa-brno
            #jbosssoaqa
            -->
         </autoJoinChannels>
      </server>
   </irc>


    <!-- Configuration files of plugins, plus simple key-value settings. -->
    <plugins>
        <plugin id="reservation" config="conf/JawaBotConfig-plugin-reservation.xml"/> <!-- TODO: Make it relative to this file, not CWD. -->
        <plugin id="jira"        config="conf/JawaBotConfig-plugin-jira.xml"/>
        <plugin id="logger">
            <settings
                logEnabledByDefault="true"
            />
        </plugin>
        <plugin id="whereis">
            <settings
                scanPeriodMin="30"
            />
        </plugin>
        <plugin id="autoop">
            <settings
                begForOpPeriod="0"
             />
            <nicks>ozizka</nicks>
        </plugin>
        <plugin id="messenger"></plugin>
        <plugin id="social"></plugin>
    </plugins>


   <!-- User groups. -->
   <userGroups>
      <group name="soa">lpetrovi jpechane mvecera</group>
      <group name="seam">oskutka jharting lfryc</group>
      <group name="eap">ozizka rhusar pslavice istudens</group>
      <group name="openjdk">ptisnovs</group>
   </userGroups>

</jawabotConfig>
```