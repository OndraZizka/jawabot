
##  Classpath is created by concating all files in lib/ .
java -cp `ls *.jar`:`ls lib/*.jar | awk '{ORS=":";print}'` org.jboss.jawabot.Main