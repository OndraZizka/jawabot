package org.jboss.jawabot.plugin.jbossascli.irc;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.jboss.as.cli.impl.CLIModelControllerClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.jboss.jawabot.JawaBot;
import org.jboss.jawabot.irc.IIrcPluginHook;
import org.jboss.jawabot.irc.IrcBotProxy;
import org.jboss.jawabot.irc.IrcPluginException;
import org.jboss.jawabot.irc.IrcPluginHookBase;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jboss.loom.utils.as7.AS7CliUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  JBoss AS CLI client over IRC. Could be fun.
 * 
 *  @author Ondrej Zizka
 */
@ApplicationScoped
public class JBossAsCliPlugin extends IrcPluginHookBase implements IIrcPluginHook<Object> {
    private static final Logger log = LoggerFactory.getLogger( JBossAsCliPlugin.class );

    private static final String PLUGIN_NAME = "jboss-as-cli";
    
    private static final Pattern PAT_CLI_CMD = Pattern.compile("((?:/\\w+)*):([\\w-]+(\\(.*\\))?)");
    

    // CLI
    private ModelControllerClient cli;
    private Set<String> operators = new HashSet();

    @Inject private JawaBot jawaBot;

    

    // Just one at a time. Could handle more connections in the future.
    @Override
    public void onMessage( IrcEvMessage msg, IrcBotProxy bot ) throws IrcPluginException {
        
        String pl = msg.getPayload().trim();
        
        // Connect
        if( msg.getPayload().startsWith("cli connect ") ){
            String[] parts = StringUtils.split( pl );
            if( parts.length < 4 ){
                bot.sendReplyTo( msg, "Try `cli connect <host> <user> [<port>]`. You'll be asked for password on PM.");
            } else {
                try {
                    int port = parts.length > 4 ? Integer.parseInt(parts[4]) : 9999;
                            
                    //ModelControllerClientConfiguration conf = new 
                    this.cli = CLIModelControllerClient.Factory.create( parts[2], port );
                    this.operators.clear();
                    this.operators.add( msg.getUser() );
                    bot.sendReplyTo( msg, "Connected to " + parts[2]);
                } catch( Exception ex ) {
                    bot.sendReplyTo( msg, "Can't connect: " + ex.getMessage() );
                }
            }
            return;
        }
        
        // Disconnect
        if( msg.getPayload().startsWith("cli disconnect") ){
            if( ! this.operators.contains( msg.getUser() ) ){
                this.onlyOpsInfo( msg, bot ); return;
            }
            try {
                this.cli.close();
                this.cli = null;
                this.operators.clear();
                bot.sendReplyTo( msg, "Closed." );
            } catch( Exception ex ) {
                bot.sendReplyTo( msg, "Sorry, failed: " + ex.getMessage() );
            }
            return;
        }
        
        // Add operator
        if( msg.getPayload().startsWith("cli addop ") ){
            String[] parts = StringUtils.split( pl );
            if( ! this.operators.contains( msg.getUser() ) ){
                this.onlyOpsInfo( msg, bot ); return;
            }
            try {
                this.operators.add( parts[2] );
                bot.sendReplyTo( msg, "Added." );
            } catch( Exception ex ) {
                bot.sendReplyTo( msg, "Sorry, failed: " + ex.getMessage() );
            }
            this.cli = null;
            return;
        }
        
        // CLI Command
        boolean find = PAT_CLI_CMD.matcher(pl).find();
        if( find ){
            if( ! this.operators.contains( msg.getUser() ) ){
                this.onlyOpsInfo( msg, bot ); return;
            }
            if( this.cli == null ){
                bot.sendReplyTo( msg, "Not connected. Try `cli connect ...`" );
                return;
            }
            try {
                ModelNode res = this.cli.execute( AS7CliUtils.parseCommand( pl, true ) );
                final String resStr = res.toString();
                if( ! resStr.contains("\n") )
                    bot.sendReplyTo( msg, "OK: " + resStr );
                else {
                    for( String string : StringUtils.split( resStr, "\n")) {
                        bot.sendReplyTo( msg, string );
                    }
                }
            } catch( Exception ex ) {
                bot.sendReplyTo( msg, "Sorry, failed: " + ex.getMessage() );
            }
            return;
        }
    }// onMessage()
    
    
    private void onlyOpsInfo( IrcEvMessage msg, IrcBotProxy bot ) {
        String ops = StringUtils.join( operators, ", ");
        bot.sendReplyTo( msg, "You can't do that. Ask "+ops+" to add you. `cli addop "+msg.getUser()+"`" );
    }

}// class
