package org.jboss.jawabot.plugin.jira.repo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jboss.jawabot.plugin.jira.core.JiraBotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryCache {
		private static final Logger log = LoggerFactory.getLogger(RepositoryCache.class);

		
		public static class Site {

				private final String url;
				private final String username;
				private final String password;

				public Site(String url) {
						this(url, null, null);
				}

				public Site(String url, String username, String password) {
						this.url = url;
						this.username = username;
						this.password = password;
				}

				public boolean requiresAuthentication() {
						return username != null || password != null;
				}

				public String getUrl() {
						return url;
				}

				public String getUsername() {
						return username;
				}

				public String getPassword() {
						return password;
				}
		}
		
		private IRepositoryCacheLoader loader;
		private final Map<String, Site> cache;
		private final Set<String> ignoredPrefixes;
		private String defaultUrlPrefix;
		private static final RepositoryCache singleton = new RepositoryCache();

		public static RepositoryCache getSingleton() {
				return singleton;
		}

		private RepositoryCache() {
				this.cache = Collections.synchronizedMap(new HashMap<String, Site>());
				this.ignoredPrefixes = Collections.synchronizedSet(new HashSet());

				try {
						setRepositoryCacheLoader(new StaticRepositoryCacheLoader());
				} catch (JiraBotException ex) {
						log.error("Uh.", ex);
				}
		}

		public void setRepositoryCacheLoader(IRepositoryCacheLoader loader) throws JiraBotException {
				this.loader = loader;
				loader.initialize();
				reloadMappings();
		}

		public void reloadMappings() {

				Map<String, Site> mappings = this.loader.loadMappings();
				for (Map.Entry<String, Site> siteMapping : mappings.entrySet()) {
						cache.put(siteMapping.getKey().toUpperCase(), siteMapping.getValue());
				}

				this.defaultUrlPrefix = this.loader.getDefaultUrlPrefix();

				Set<String> ignoredPrefixes = this.loader.loadIgnoredPrefixes();
				this.setIgnoredPrefixes(ignoredPrefixes);
		}

		public void setIgnoredPrefixes(Collection<String> prefixes) {
				this.ignoredPrefixes.clear();
				if (null != prefixes) {
						this.ignoredPrefixes.addAll(prefixes);
				}
		}

		public Set<String> getIgnoredPrefixes() {
				return this.ignoredPrefixes;
		}

		/**
		 *
		 * @param keyPrefix  Project part of the Jira issue ID - e.g. JBAS of JBAS-1234
		 * @returns  The URL prefix for the given issue, e.g.
		 *           http://jira.rhq-project.org/browse/JBAS
		 */
		public String getUrl(String keyPrefix) {
				Site siteForKey = cache.get(keyPrefix.toUpperCase());
				if (siteForKey == null) {
						//return null;
						return defaultUrlPrefix + keyPrefix;
				}
				return siteForKey.getUrl();
		}

		/**
		 * @returns  true if the given prefix is ignored 
		 *           (useful for non-Jira abbrevs, e.g. RHEL, AS, etc.)
		 */
		public boolean isIgnoredPrefix(String keyPrefix) {
				return this.ignoredPrefixes.contains(keyPrefix);
		}
}// class

