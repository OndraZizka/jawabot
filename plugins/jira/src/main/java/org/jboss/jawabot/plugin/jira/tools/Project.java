package org.jboss.jawabot.plugin.jira.tools;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
class Project {

    String prefix;
    String name;
    String url;


    public Project( String prefix, String name, String url ) {
        this.prefix = prefix;
        this.name = name;
        this.url = url;
    }

    public String getPrefix() { return prefix; }
    public String getName() { return name; }
    public String getUrl() { return url; }

}// class
