
package org.jboss.jawabot.plugin.jira.repo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.jboss.jawabot.plugin.jira.core.JiraBotException;
import org.jboss.jawabot.plugin.jira.config.beans.ConfigBean;
import org.jboss.jawabot.plugin.jira.config.beans.ProjectBean;
import org.jboss.jawabot.plugin.jira.config.beans.RepositoryBean;
import org.jboss.jawabot.plugin.jira.repo.RepositoryCache.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Creates a RepositoryCache from the provided ConfigBean.
 *  @author Ondrej Zizka
 */
public class ConfigRepositoryCacheLoader implements IRepositoryCacheLoader {
	private static final Logger log = LoggerFactory.getLogger( ConfigRepositoryCacheLoader.class );

   private ConfigBean config;

   private Map<String, Site> mappings = new HashMap();
   private Set<String> ignoredPrefixes = new TreeSet<String>();
   private Site defaultRepo;


   public ConfigRepositoryCacheLoader(ConfigBean config) {
      if( null == config )
         throw new IllegalArgumentException("Config can't be null.");
      this.config = config;
   }



   @Override public void initialize() throws JiraBotException {
      // Repositories.
      for( RepositoryBean repo : this.config.jira.repositories ){
         for( ProjectBean project : repo.projects ){
            //results.put("BR", new RepositoryCache.Site("http://jira.openqa.org/browse/BR"));
            mappings.put( project.id, new RepositoryCache.Site( repo.getUrl() + project.id ));
         }
      }
      // Ignored prefixes.
      for( String prefix: this.config.jira.ignoredPrefixes )
         ignoredPrefixes.add(prefix);

      String defaultRepoName = this.config.jira.defaultRepo;
      defaultRepo = this.mappings.get(defaultRepoName);
      if( null == defaultRepo )
         throw new JiraBotException("Default repo '"+defaultRepoName+"' not found in defined repos.");
   }

   @Override  public Map<String, Site> loadMappings() {
      return this.mappings;
   }

   @Override  public void storeMappings(Map<String, Site> mappings) {
      /** Currently no-op. */
   }

   @Override  public Set<String> loadIgnoredPrefixes() {
      for( String prefix: this.config.jira.ignoredPrefixes )
         ignoredPrefixes.add(prefix);
      return ignoredPrefixes;
   }

   @Override  public void storeIgnoredPrefixes(Set<String> ignoredPrefixes) { }

   @Override
   public String getDefaultUrlPrefix() {
      return this.defaultRepo.getUrl();
   }

}// class
