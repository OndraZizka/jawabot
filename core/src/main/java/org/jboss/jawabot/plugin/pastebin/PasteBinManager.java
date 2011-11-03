
package org.jboss.jawabot.plugin.pastebin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *  Manages PasteBin entries.
 * 
 *  @author Ondrej Zizka
 */
@ApplicationScoped
public class PasteBinManager implements IPasteBinManager {
   
   private Queue<PasteBinEntry> entriesQueue = new ConcurrentLinkedQueue<PasteBinEntry>();
   private Map<Long, PasteBinEntry> idToEntryMap = new HashMap();
   private AtomicLong nextId = new AtomicLong(1);

   public List<PasteBinEntry> getAll() {
      return Arrays.asList( this.entriesQueue.toArray( new PasteBinEntry[0] ) );
   }

   @Override
   public synchronized boolean addEntry( PasteBinEntry e ) {
      Long id = this.nextId.getAndIncrement();
      e.setId(id);
      this.idToEntryMap.put(id, e);
      return this.entriesQueue.add(e);
   }

   @Override
   public PasteBinEntry getById( long id ){
      return this.idToEntryMap.get(id);
   }

   @Override
   public PasteBinEntry getPaste( long id ) {
      return this.getById(id);
   }

   @Override
   public List<PasteBinEntry> getPastes_OrderByWhenDesc(int i) {
      return new ArrayList( new TreeSet( this.entriesQueue ) );
   }


}// class
