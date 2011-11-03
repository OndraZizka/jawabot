package org.jboss.jawabot.plugin.pastebin.irc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.jboss.jawabot.ex.JawaBotException;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jboss.jawabot.plugin.pastebin.JpaPasteBinManager;
import org.jboss.jawabot.plugin.pastebin.PasteBinEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Session is started by a command in channel, like:
 * 
 *    ozizka: rhusar, paste it
 *    jawabot>>rhusar: Start pasting your text to me. Finish with "." message. I'll take  care of the rest.
 *    rhusar>>jawabot: 	at org.apache.wicket.Page.onRender(Page.java:1594)
 *    rhusar>>jawabot: 	at org.apache.wicket.Component.render(Component.java:2521)
 *    rhusar>>jawabot: .
 *    jawabot: ozizka, rhusar pasted it here: http://jawabot.qa.jboss.com/pastebin/469
 * 
 *  @author Ondrej Zizka
 */
public class PasteBinIrcPluginHook extends IrcPluginHookBase implements IIrcPluginHook<Object> 
{
   private static final Logger log = LoggerFactory.getLogger(PasteBinIrcPluginHook.class);
   
   private final Map<String, PasteBinEnterSession> userToSession = new HashMap();
   
   @Inject private JpaPasteBinManager pbManager;
   
   
   
   /**
    * Reacts on a message in a form "ozizka: paste it"
    */
   @Override
   public void onMessage( IrcEvMessage message, IrcBotProxy bot ) throws IrcPluginException
   {
      Pattern pat = Pattern.compile("([-_a-zA-Z0-9]+)[:,] ?paste it.*");
      Matcher mat = pat.matcher( message.getText() );
      
      if( mat.matches() ){
         String paster = mat.group(1);
         PasteBinEnterSession sess = startPasteBinEnterSession( paster, message.getUser(), message.getChannel() );
         
         // Instructions & disclaimers.
         bot.sendMessage( paster, "Start pasting your text to me. Finish with \".\" message. I'll take  care of the rest.");
         bot.sendMessage( paster, "The pasted text will be publicly accessible.");
         bot.sendMessage( paster, "The URL will be sent to " + 
                 // Channel or user?
                 StringUtils.defaultIfEmpty( message.getChannel(), message.getUser() ) );
      }
   }
 
   
   @Override
   public void onPrivateMessage( IrcEvMessage message, IrcBotProxy bot ) throws IrcPluginException {
      String paster = message.getUser();
      PasteBinEnterSession sess = this.userToSession.get( paster );
      if( null == sess )  return;
      if( ".".equals( message.getText() ) ){
         PasteBinEntry entry = new PasteBinEntry( message.getUser(), sess.getWholeText() );
         pbManager.addEntry( entry );
         String url;
         try {
            url = this.getUrlFor( entry );
            bot.sendMessage( message.getUser(), "Got it. Saved. Sending notice to the recipient." );
            bot.sendMessage( sess.getChallenger(), sess.getChannel(), paster + " has pasted " + url );
         } catch (JawaBotException ex) {
            log.error("Couldn't get URL for PB entry: " + ex, ex);
            String apology = "Sorry, couldn't get the URL of the paste: " + ex.getMessage();
            bot.sendMessage( message.getUser(), apology );
            bot.sendMessage( sess.getChallenger(), sess.getChannel(), apology );
         }
      }
      else
         sess.appendLine( message.getText() );
   }
   

   
   private PasteBinEnterSession startPasteBinEnterSession( String paster, String challenger, String channel ) {
      PasteBinEnterSession sess = new PasteBinEnterSession(paster, challenger, channel);
      this.userToSession.put( paster, sess );
      return sess;
   }

   
   /**
    * @returns URL for given pastebin entry.
    * TODO better.
    */
   private String getUrlFor( PasteBinEntry entry ) throws JawaBotException {
      try {
          InetAddress addr = InetAddress.getLocalHost();
          String hostname = addr.getHostName();
          return "http://" + hostname + "/pastebin/" + entry.getId();
      } catch( UnknownHostException e ) {
         throw new JawaBotException("Sorry, couldn't get hostname of the server.");
      }
   }

   
}// class

