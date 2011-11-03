
# mvn -Dexec.args='-classpath %classpath org.jboss.jirabot.Main' -Dexec.executable=/usr/lib/jvm/java-6-sun/bin/java process-classes org.codehaus.mojo:exec-maven-plugin:1.1:exec

mvn dependency:copy-dependencies

# Run:
# java -cp `ls *.jar`:`ls lib/*.jar | awk '{ORS=":";print}'` org.jboss.jawabot.Main