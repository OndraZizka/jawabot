package org.jboss.jawabot.plugin.jira.repo;

import java.util.Map;

import java.util.Set;
import org.jboss.jawabot.plugin.jira.core.JiraBotException;


/**
 * You may want to override this strategy.  The default implementation is hard-coded via the
 * {@link StaticRepositoryCacheLoader}, however you may want to externalize the loading / storing
 * to a flat text file or even a database in the future.
 */
public interface IRepositoryCacheLoader {

    /**
     * a method that is guaranteed to be called before the initial loading of the repository because you may
     * want to establish certain state once, authorization for example, as opposed to each time the load/store 
     * methods are called 
     */
    void initialize() throws JiraBotException;

    /**
     * pull the mappings from whatever storage mechanism this loader sits in front of; the RepositoryCache will be
     * merged with the results from this method; if there is a duplicate that already exists in the cache, the one
     * from this method will override the one in the cache
     */
    Map<String, RepositoryCache.Site> loadMappings();

    /**
     * since the {@link JiraBot} allows for the administrator to change the mappings on the fly, each time a live
     * change is made the mappings will be re-persisted to the storage mechanism this loaded sits in front of; as a
     * result, this method should first delete all existing mappings in storage, and then insert these fresh
     */
    void storeMappings(Map<String, RepositoryCache.Site> mappings);


    Set<String> loadIgnoredPrefixes();

    void storeIgnoredPrefixes(Set<String> ignoredPrefixes);



    /**
     * Returns the default URL prefix for projects not matched by any of the
     * stored repositories.
     * @returns  Something like "https://jira.jboss.org/jira/browse/"
     *           used to prepend to JIRA ID to get the URL.
     */
    public String getDefaultUrlPrefix();
}
