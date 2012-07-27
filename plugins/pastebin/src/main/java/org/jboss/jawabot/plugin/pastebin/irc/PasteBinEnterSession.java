package org.jboss.jawabot.plugin.pastebin.irc;

/**
 *  Session of IRC pastebin interface.
 *  Started by a command in channel, like:
 *    ozizka: rhusar, paste it
 *    jawabot>>rhusar: Start pasting your text to me. Finish with "." message. I'll take  care of the rest.
 *    rhusar>>jawabot:  at org.apache.wicket.Page.onRender(Page.java:1594)
 *    rhusar>>jawabot:  at org.apache.wicket.Component.render(Component.java:2521)
 *    rhusar>>jawabot: .
 *    jawabot: ozizka, rhusar pasted it here: http://jawabot.qa.jboss.com/pastebin/469
 * 
 *  @author Ondrej Zizka
 */
public class PasteBinEnterSession {
   
   private String paster;
   private String challenger;
   private String channel;
   
   private StringBuilder lines = new StringBuilder();
   

   public PasteBinEnterSession( String paster, String challenger, String channel ) {
      this.paster = paster;
      this.challenger = challenger;
      this.channel = channel;
   }

   public void appendLine(String text) {
      this.lines.append(text + "\n");
   }
   
   public String getWholeText(){
      return this.lines.toString();
   }

   
   
   public String getChallenger() { return challenger; }
   public String getChannel() { return channel; }
   public String getPaster() { return paster; }
      
   
}// class

