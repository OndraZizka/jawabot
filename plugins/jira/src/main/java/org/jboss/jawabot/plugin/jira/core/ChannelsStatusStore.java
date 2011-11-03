
package org.jboss.jawabot.plugin.jira.core;

import java.util.HashMap;
import java.util.Map;

/**
 *  Keeps info about our activity in channels -
 *  when we informed about a jira, etc -
 *  and decides whether it's too early to repeat the information and such.
 * 
 *  @author Ondrej Zizka
 */
public class ChannelsStatusStore {

   private final Map<String, ChannelInfo> channelInfos = new HashMap();

   //public addChannel( String name );
   
   public void onMessage( String channel ){
      ChannelInfo ch = this.channelInfos.get(channel);
      if( ch == null ) this.channelInfos.put( channel, ch = new ChannelInfo() );
      ch.incMessageCounter();
   }

   public void onJoin( String channel ){
      ChannelInfo ch = this.channelInfos.get(channel);
      if( ch == null ) this.channelInfos.put( channel, ch = new ChannelInfo() );
      ch.touchLastJoined();
   }

   public void onIssueAnnounced( String channel, String issueID ){
      ChannelInfo ch = this.channelInfos.get(channel);
      if( ch == null ) this.channelInfos.put( channel, ch = new ChannelInfo() );
      ch.touchIssueAnnouncement(issueID);
   }


   /**
    * @returns  The delta object for the given message in the given channel, or null if not stored.
    */
   public static IssueShownInfo createIssueShownDelta(IssueShownInfo issueInfo, int messagesCount, long timeMS) {
      return new IssueShownInfo(
                    messagesCount - issueInfo.shownAtCount,
                    timeMS - issueInfo.shownTime
                  );
   }

   /**
    * @returns  The delta object for the given message in the given channel, or null if not stored.
    */
   public IssueShownInfo getIssueShownDelta( String channel, String issueID ){
      ChannelInfo ch = this.channelInfos.get(channel);
      if( ch == null ) return null;
      else return ch.getIssueShownDelta( issueID );
   }

   public int getChannelMessagesCount( String channel ){
      ChannelInfo ch = this.channelInfos.get(channel);
      if( ch == null ) return 0;
      else return ch.getMessagesCount();
   }


   public void removeChannel( String name ){
      this.channelInfos.remove( name );
   }


   /**
    * Returns true if it's too early to repeat a message:
    *  - Too few messages since the last appearance - users would see the previous one in their log.
    *  - Too little time since the last time - users would remember it.
    *
    * @param curTimeMS  Current time, to spare a call to System.currentTimeMillis().
    * @param repeatThreshold  Limits - how many messages and time must pass before repeating.
    *                         Both conditions must be matched.
    */
   public boolean isTooEarlyToRepeat(String channel, String issueID, long curTimeMS, IssueShownInfo repeatThreshold)
   {
      // Get the channel.
      ChannelInfo ch = this.channelInfos.get( channel );
      if( null == ch )  return false;

      // Get the issue shown info.
      IssueShownInfo shown = ch.getIssueShownInfo(issueID);
      if( shown == null )  return false;

      // If someone joined since then, show in all cases.
      if( ch.getLastJoinedCount() > shown.shownAtCount )
         return false;

      // Get the time and msg count since the issue was last shown.
      IssueShownInfo delta = createIssueShownDelta( shown, ch.getMessagesCount(), curTimeMS );
      if( null == delta ) return false;
      
      //if( delta.shownAtCount + repeatedShowThreshold.shownAtCount > this.getChannelMessagesCount( from ) ){
      if( delta.shownAtCount <= repeatThreshold.shownAtCount
               &&  delta.shownTime <= repeatThreshold.shownTime  ){
         return true;
      }

      return false;
   }

}// class ChannelsStatusStore





/**
 * Cnannel info - when was which issue shown.
 * @author ondra
 */
class ChannelInfo {

   private int messagesCount = 0;
   
   private int lastJoinedCount = 0;

   private Map<String, IssueShownInfo> issueToShownInfoMap = new HashMap();


   public int getLastJoinedCount() {      return lastJoinedCount;   }
   public int getMessagesCount() {      return messagesCount;   }
   

   public void incMessageCounter(){ messagesCount++; }

   public void touchLastJoined(){ lastJoinedCount = messagesCount; }

   public void touchIssueAnnouncement( String issueID ){
      touchIssueAnnouncement( issueID, System.currentTimeMillis() );
   }
   
   public void touchIssueAnnouncement( String issueID, long timeMS ){
      issueToShownInfoMap.put( issueID, new IssueShownInfo( messagesCount, timeMS ) );
   }

   public IssueShownInfo getIssueShownDelta( String issueID ){
      return getIssueShownDelta(issueID, System.currentTimeMillis() );
   }

   /**
    *
    * @param issueID  Issue ID to query for last appearance.
    * @param timeMS   Current time in milliseconds.
    * @returns  A misued instance of IssueShownInfo:
    *      <ul><li>shownAtCount shows number of messages since the last appearance.
    *          <li>shownTime  shows number of milliseconds passed since the last appearance.
    *      </ul>
    */
   public IssueShownInfo getIssueShownDelta( String issueID, long timeMS ){
      IssueShownInfo issueInfo = issueToShownInfoMap.get( issueID );
      if( null == issueInfo )
         return null;

      return ChannelsStatusStore.createIssueShownDelta( issueInfo, messagesCount, timeMS );
   }

   public IssueShownInfo getIssueShownInfo( String issueID ) {
      return issueToShownInfoMap.get( issueID );
   }



   @Override
   public String toString() {
      return "ChannelInfo{ msgs:" + messagesCount + ", lastJoinedCount:" + lastJoinedCount + ", issueToShownInfoMap=" + issueToShownInfoMap + " }";
   }

}//class ChannelInfo
