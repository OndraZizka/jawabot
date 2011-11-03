package org.jboss.jawabot.plugin.jira.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 


/**
 * Generic cache class, providing an expiry functionality with regular cleanup.
 *
 * This was originally concieved as a singleton - why?
 *
 * @author ondra
 * @param <TCachedItem>  Class of the items to be cached.
 */
public class TimeoutCache<TCachedItem>
{
   private static final Logger log = LoggerFactory.getLogger( TimeoutCache.class );


	// Please keep it low, Jiras change frequently... and loading it is not so expensive.
	public static final int DEFAULT_EXPIRATION_MINUTES = 30;


   /**  Cache items map. */
	private final Map<String, ItemWrapper> cacheMap;

   /** Timeout in miliseconds. Default is ISSUE_CACHE_EXPIRATION_MINUTES. */
   public void setTimeoutMS(long timeoutMS) {      this.timeout = timeoutMS;   }
   public long getTimeoutMS() {      return timeout;   }
   private long timeout;

	
	private ScheduledThreadPoolExecutor expiryThreadPoolExecutor;
   //private ScheduledFuture<?> scheduledFuture;///



	/**  Constructor with default timeout - DEFAULT_EXPIRATION_MINUTES. */
   public TimeoutCache() {
      this( TimeoutCache.DEFAULT_EXPIRATION_MINUTES * 60 * 1000 );
   }

	/**  Constructor. */
	public TimeoutCache( long timeoutMS ) {

      log.info("New cache with timeout "+timeoutMS+" ms.");

		cacheMap = Collections.synchronizedMap( new HashMap<String, ItemWrapper>() );
      this.timeout = timeoutMS;

      long expirationCheckFrequencyMS = Math.max( timeoutMS / 10, 5*1000 );  // At least 5 sec.
      this.scheduleExpiryThread( expirationCheckFrequencyMS );
	}



   /**
    * (Re)schedules expiry thread executions.
    */
   private void scheduleExpiryThread( long delayBetweenChecksMS ){
      // Purge old.
      if( null != expiryThreadPoolExecutor ){
         log.info("Purging old expiry thread schedule.");
         ScheduledThreadPoolExecutor exe = expiryThreadPoolExecutor;
         expiryThreadPoolExecutor = null;
         exe.shutdown();
         //try { exe.awaitTermination( 1, TimeUnit.SECONDS ); } catch (Throwable ex){ ; }
      }
      // Create new.
      log.info("Scheduling expiry thread to run every "+delayBetweenChecksMS+" ms.");
      this.expiryThreadPoolExecutor = new ScheduledThreadPoolExecutor(1); // only one expiry thread at a time
      //this.scheduledFuture =///
      this.expiryThreadPoolExecutor.scheduleWithFixedDelay(
                  new IssueCacheExpiry(), delayBetweenChecksMS, delayBetweenChecksMS, TimeUnit.MILLISECONDS);
      
      this.expiryThreadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
      this.expiryThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
   }


   /**
    *  Stops expiry thread - no more checks will be scheduled or executed.
    */
	public void stopExpiryThread(){
      //this.scheduledFuture.cancel(true);///
      if( null != this.expiryThreadPoolExecutor ){
         this.expiryThreadPoolExecutor.shutdown();
         //this.expiryThreadPoolExecutor.shutdownNow();///
         this.expiryThreadPoolExecutor.purge();///
      }
	}



   
	public TCachedItem getItem( String key ) {
      //if( null == key ) return null;
      ItemWrapper wrap = cacheMap.get(key); // toUpperCase() removed.
      if( null == wrap ) return null;
		return wrap.item;
	}

	public void putItem(String key, TCachedItem cachedItem) {
		cacheMap.put(key, new ItemWrapper( cachedItem, System.currentTimeMillis() ));
	}

	public void removeItem(String key) {
		cacheMap.remove(key);
	}

	public void clear() {
		cacheMap.clear();
	}




   /**  Adds timeCreated to the cached items. */
   private class ItemWrapper {
      public final TCachedItem item;
      public final long  timeCreated;

      public ItemWrapper(TCachedItem item, long timeCreated) {
         this.item = item;
         this.timeCreated = timeCreated;
      }
      public boolean isStale() {
         return isStale( System.currentTimeMillis() );
      }
      public boolean isStale( long currentTimeMillis ) {
         long timeInCacheMS = currentTimeMillis - this.timeCreated;
         return ( timeInCacheMS > TimeoutCache.this.timeout );
      }
   }


   /**  Expiry thread.  */
   private class IssueCacheExpiry implements Runnable {

		public void run() {
         //TimeoutCache.this.expiryThreadPoolExecutor.shutdown();///
         if( TimeoutCache.this.expiryThreadPoolExecutor.isShutdown() )
            return;

			// Make a reference copy of the cache so we can iterate and modify the cache without getting
			// ConcurrentModificationException due to another thread putting new values into the cache.
			Map<String, ItemWrapper> referenceCopy = new HashMap<String, ItemWrapper>(TimeoutCache.this.cacheMap);
			long currentTimeMillis = System.currentTimeMillis();

			for (Map.Entry<String, ItemWrapper> itemEntry : referenceCopy.entrySet()) {
				log.debug("Checking if " + itemEntry.getValue().item + " is stale.");
				if( itemEntry.getValue().isStale(currentTimeMillis)) {
					log.debug("Removing expired entry " + itemEntry.getKey() +": "+ itemEntry.getValue().item);
					// The map is SynchronizedMap, so this is safe.
					TimeoutCache.this.cacheMap.remove(itemEntry.getKey());
				}
			}
		}// run()
	}// class IssueCacheExpiry{}


}// class TimeoutCache
