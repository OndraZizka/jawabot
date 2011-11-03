
package org.jboss.jawabot.plugin.jira.repo2;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.jboss.jawabot.plugin.jira.config.beans.ProjectBean;
import org.jboss.jawabot.plugin.jira.config.beans.RepositoryBean;





/**
 *
 * @author Ondrej Zizka
 */
public class RepositoriesManager
{
   private static final Logger log = LoggerFactory.getLogger( RepositoriesManager.class );


   // TODO: ignored prefixes, evt. account information.


   /** repo name -> repo bean map. */
   private Map<String, RepositoryBean> repos = new HashMap();

   /** Issue prefix -> repo bean map. */
   private Map<String, RepositoryBean> prefixToRepoMap = new HashMap();

   /** Set of ignored issue prefixes. */
   private final Set<String> ignoredPrefixes = new HashSet();

   private String defaultRepoType = null;



   // -- Repos -- //


   public void clearRepos(){
      this.repos.clear();
      this.prefixToRepoMap.clear();
   }

   public void addRepo( RepositoryBean repo )
   {
      if( repo == null )  throw new IllegalArgumentException("RepositoryBean repo == null.");

      this.repos.put( repo.getName(), repo );

      // No projects.
      if( repo.getProjects() == null || repo.getProjects().size() == 0 ){
         // TODO: Is it a catch-all repo?
         log.info("Empty repo - is it catch-all repo?");
         return;
      }

      //  <project id="BZ" name="Red Hat bugs"/>
      for( ProjectBean proj : repo.getProjects() ){
         // First claim wins.
         if( ! this.prefixToRepoMap.containsKey( proj.id ) )
            this.prefixToRepoMap.put( proj.id, repo );
      }
   }



   /**
   * E.g. JBPAPP => JBoss.org repo bean.
   * @param prefix  Issue prefix - e.g. for JBPAPP-123 or BZ-1234 its JBPAPP or BZ.
   * @returns  null if not found, or the appropriate repository metadata.
   */
   public RepositoryBean getRepoForPrefix( String prefix ){
      return this.prefixToRepoMap.get( prefix );
   }

   public RepositoryBean getRepoForIssue( String issueID ) {
      return this.prefixToRepoMap.get( getPrefix(issueID) );
   }





   // -- Ignored prefixes -- //
   

   /**
    * @param issueID  Something with JIRA issue ID format, e.g. RHEL-5
    * @returns  true if the part before dash is ignored one.
    *
    * TODO: This shouldn't be in RepositoryParser...
    *       Perhaps it would be better to have cache using parser, not the opposite way.
    */
   public boolean hasIgnoredPrefix( String issueID ){
      if( null == issueID ) return true;
      String prefix = getPrefix(issueID);
      //if( prefix.length() < this.minJiraPrefixLength )
      //   return false;
      return this.isIgnoredPrefix( prefix );
   }

   public void setIgnoredPrefixes( Collection<String> prefixes ){
     this.ignoredPrefixes.clear();
     if( null != prefixes )
      this.ignoredPrefixes.addAll( prefixes );
   }
   public Set<String> getIgnoredPrefixes( ){
    return this.ignoredPrefixes;
   }


   /**
    * @returns  true if the given prefix is ignored
    *           (useful for non-Jira abbrevs, e.g. RHEL, AS, etc.)
    */
   public boolean isIgnoredPrefix( String keyPrefix ){
      return this.ignoredPrefixes.contains( keyPrefix );
   }

   public static String getPrefix( String issueID ){
      return StringUtils.substringBefore( issueID, "-" );
   }


   /**  @returns  Repo name -> repo bean map. */
   public Map<String, RepositoryBean> getRepos() {
      return Collections.unmodifiableMap( this.repos );
   }

   /**  @returns  Project prefix ->  containing repo bean. */
   public Map<String, RepositoryBean> getPrefixToRepoMap() {
      return Collections.unmodifiableMap( this.prefixToRepoMap );
   }

   
   public String getDefaultRepoType() {
      return defaultRepoType;
   }

   public void setDefaultRepoType(String defaultRepoType) {
      this.defaultRepoType = defaultRepoType;
   }








}// class
