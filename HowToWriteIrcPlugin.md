# How to Write an IRC Plugin #

Implement `IrcPluginHook`.

Check existing plugins for the examples.

# Hello world #

```
/**
 *  Social - reacts on various social protocol tokens like "Hi!" etc.
 * 
 *  @author Ondrej Zizka
 */
public class SocialIrcPluginHook extends IrcPluginHookBase implements IIrcPluginHook<Object> {
    private static final Logger log = LoggerFactory.getLogger( SocialIrcPluginHook.class );



    @Override
    public void onMessage( IrcEvMessage message, IrcBotProxy bot ) throws IrcPluginException {

        //  Is it for us?
        if(  ! bot.getNick().equals( message.getRecipient() ) 
          && ! message.getText().contains( bot.getNick() ) ) 
            return;
        
        String msgNorm = message.getPayload().toLowerCase();
        String reply = null;
                
        // Hi, hello and such.
        if(        msgNorm.startsWith("hi")
                || msgNorm.startsWith("hello")
                || msgNorm.startsWith("ping")
        ){
            reply = new String[]{"hi", "hello", "hey", "hm?", "Awaiting orders.", "Asus ordenes."}[new Random().nextInt(6)];
        }
                       
        // Send it.
        if( null != reply )
            bot.sendReplyTo( message, reply );
        
    }// onMessage()
    
}// class
```

# AutoOp #

Automatically gives operator privileges.

```
/**
 *  Automatically ops everyone in the channel.
 *  Also asks for operator if not having it.
 * 
 *  @author Ondrej Zizka
 */
public class AutoOpIrcPluginHook extends IrcPluginHookBase implements IIrcPluginHook<Object> {
   private static final Logger log = LoggerFactory.getLogger( AutoOpIrcPluginHook.class );

    @Override
    public void onJoin( IrcEvJoin event, IrcBotProxy bot  ) {
        if( ! this.isCanHaveOperator( event.getChannel(), event.getUser() ) )
            return;
        bot.op( event.getChannel(), event.getUser() );
    }


    @Override
    public void onBotJoinChannel( String channel, IrcBotProxy bot ) {
        
        for( User user : bot.getUsers( channel ) ) {
            if( user.isOp() )
                continue;
            if( ! this.isCanHaveOperator( channel, user.getNick() ) )
                continue;
            bot.op( channel, user.getNick() );
        }
    }


    /**
     *  TODO: Perhaps get this info from database?
     */
    private boolean isCanHaveOperator( String channel, String user ) {
        return true;
    }
   
}// class
```