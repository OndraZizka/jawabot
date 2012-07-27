package org.jboss.jawabot.plugin.jira.repo;

import java.util.*;


public class StaticRepositoryCacheLoader implements IRepositoryCacheLoader {

    private static Map<String, RepositoryCache.Site> staticMappings;
    private static Set<String> ignoredPrefixes;

    public void initialize() {
        Map<String, RepositoryCache.Site> results = new HashMap<String, RepositoryCache.Site>();

        /*
         * http://jira.jboss.org/jira/browse/  is the default repository for all prefixes
         * not matching those specified bellow, or ignored prefixes.
         */

        results.put("RHQ", new RepositoryCache.Site("http://jira.rhq-project.org/browse/RHQ"));
        results.put("JOPR", new RepositoryCache.Site("http://jira.jboss.org/jira/browse/JOPR"));
        results.put("EMBJOPR", new RepositoryCache.Site("http://jira.jboss.org/jira/browse/EMBJOPR"));
        results.put("JBMANCON", new RepositoryCache.Site("http://jira.jboss.org/jira/browse/JBMANCON"));
        results.put("JBNADM", new RepositoryCache.Site("https://jira.jboss.org/jira/browse/JBNADM"));

        results.put("HHH", new RepositoryCache.Site("http://opensource.atlassian.com/projects/hibernate/browse/HHH"));
        results.put("BVAL", new RepositoryCache.Site("http://opensource.atlassian.com/projects/hibernate/browse/BVAL"));
        results.put("ANN", new RepositoryCache.Site("http://opensource.atlassian.com/projects/hibernate/browse/ANN"));
        results.put("HCANN", new RepositoryCache.Site("http://opensource.atlassian.com/projects/hibernate/browse/HCANN"));
        results.put("EJB", new RepositoryCache.Site("http://opensource.atlassian.com/projects/hibernate/browse/EJB"));
        results.put("HSEARCH", new RepositoryCache.Site("http://opensource.atlassian.com/projects/hibernate/browse/HSEARCH"));
        results.put("HSHARDS", new RepositoryCache.Site("http://opensource.atlassian.com/projects/hibernate/browse/HSHARDS"));
        results.put("HBX", new RepositoryCache.Site("http://opensource.atlassian.com/projects/hibernate/browse/HBX"));
        results.put("HV", new RepositoryCache.Site("http://opensource.atlassian.com/projects/hibernate/browse/HV"));
        results.put("HBI", new RepositoryCache.Site("http://opensource.atlassian.com/projects/hibernate/browse/HBI"));


        results.put("BR", new RepositoryCache.Site("http://jira.openqa.org/browse/BR"));
        results.put("CT", new RepositoryCache.Site("http://jira.openqa.org/browse/CT"));
        results.put("FLOYD", new RepositoryCache.Site("http://jira.openqa.org/browse/FLOYD"));
        results.put("FKS", new RepositoryCache.Site("http://jira.openqa.org/browse/FKS"));
        results.put("JTC", new RepositoryCache.Site("http://jira.openqa.org/browse/JTC"));
        results.put("QQA", new RepositoryCache.Site("http://jira.openqa.org/browse/QQA"));
        results.put("PWA", new RepositoryCache.Site("http://jira.openqa.org/browse/PWA"));
        results.put("SEL", new RepositoryCache.Site("http://jira.openqa.org/browse/SEL"));
        results.put("SEDOC", new RepositoryCache.Site("http://jira.openqa.org/browse/SEDOC"));
        results.put("GRID", new RepositoryCache.Site("http://jira.openqa.org/browse/GRID"));
        results.put("SIDE", new RepositoryCache.Site("http://jira.openqa.org/browse/SIDE"));
        results.put("SOR", new RepositoryCache.Site("http://jira.openqa.org/browse/SOR"));
        results.put("SRC", new RepositoryCache.Site("http://jira.openqa.org/browse/SRC"));
        results.put("ST", new RepositoryCache.Site("http://jira.openqa.org/browse/ST"));
        results.put("WTR", new RepositoryCache.Site("http://jira.openqa.org/browse/WTR"));
        results.put("WTRREC", new RepositoryCache.Site("http://jira.openqa.org/browse/WTRREC"));
        results.put("WET", new RepositoryCache.Site("http://jira.openqa.org/browse/WET"));


        staticMappings = Collections.unmodifiableMap(results);


        // Ignored repositories.
        ignoredPrefixes = new HashSet();

        // Repositories which need login.
        ignoredPrefixes.add("JBQA");

        // Not Jira repositories, but used in ABC-123 format, too:
        ignoredPrefixes.add("RHEL");
        ignoredPrefixes.add("AS");
        ignoredPrefixes.add("DOC");
        ignoredPrefixes.add("CVE");
        ignoredPrefixes.add("JDK");
        ignoredPrefixes.add("SLES");
        ignoredPrefixes.add("JON");
        ignoredPrefixes.add("EP");
        ignoredPrefixes.add("EAP");
        ignoredPrefixes.add("JPP");
        ignoredPrefixes.add("BZ");
    }

    public Map<String, RepositoryCache.Site> loadMappings() {
        return staticMappings;
    }

    public void storeMappings(Map<String, RepositoryCache.Site> mappings) {
        // No-op for static loader.
    }

    public Set<String> loadIgnoredPrefixes() {
        return ignoredPrefixes;
    }

    public void storeIgnoredPrefixes(Set<String> ignoredPrefixes) {
        // No-op for static loader.
    }

    public String getDefaultUrlPrefix() {
        return "https://jira.jboss.org/jira/browse/";
    }

}
