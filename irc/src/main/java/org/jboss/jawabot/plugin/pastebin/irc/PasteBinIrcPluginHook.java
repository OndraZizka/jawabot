package org.jboss.jawabot.plugin.pastebin.irc;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jboss.jawabot.ex.JawaBotException;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.model.IrcMessage;

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
public class PasteBinIrcPluginHook implements IIrcPluginHook<Object> {
   
   private final Map<String, PasteBinEnterSession> userToSession = new HashMap();
   
   
   @Override
   public void onMessage( IrcMessage message, IrcBotProxy bot ) throws IrcPluginException
   {
      Pattern pat = Pattern.compile("([-_a-zA-Z0-9]+)[:,] ?paste it.*");
      Matcher mat = pat.matcher( message.getText() );
      
      if( mat.matches() ){
         String paster = mat.group(1);
         PasteBinEnterSession sess = startPasteBinEnterSession( paster, message.getUser(), message.getChannel() );
         
         bot.sendMessage( paster, "Start pasting your text to me. Finish with \".\" message. I'll take  care of the rest.");
         
      }
   }
   

   @Override
   public void onPrivateMessage( IrcMessage message, IrcBotProxy bot ) throws IrcPluginException {
      PasteBinEnterSession sess = this.userToSession.get( message.getUser() );
      if( null == sess )  return;
      sess.appendLine( message.getText() );
   }
   

   @Override
   public void initModule(Object initObject) throws JawaBotException {
   }

   @Override
   public void startModule() throws JawaBotException {
   }

   @Override
   public void stopModule() throws JawaBotException {
   }

   @Override
   public void destroyModule() throws JawaBotException {
   }

   private PasteBinEnterSession startPasteBinEnterSession( String paster, String challenger, String channel ) {
      PasteBinEnterSession sess = new PasteBinEnterSession(paster, challenger, channel);
      this.userToSession.put( paster, sess );
      return sess;
   }

   
}// class

