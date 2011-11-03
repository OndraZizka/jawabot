
package org.jboss.jawabot.plugin.jira.core;

/**
 *  Data POJO used in ChannelsStatusStore.
 *  Immutable.
 * 
 *  @author Ondrej Zizka
 */
public class IssueShownInfo {
   public final int shownAtCount;
   public final long shownTime;

   public IssueShownInfo( int shownAtCount, long shownTime ) {
      this.shownAtCount = shownAtCount;
      this.shownTime = shownTime;
   }


   @Override
   public String toString() {
      return "IssueShownInfo{ shownAtCount: " + shownAtCount + ", shownTime: " + shownTime + " }";
   }

}// class
