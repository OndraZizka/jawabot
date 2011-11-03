package org.jboss.jawabot.plugin.whereis.irc;

import java.util.Comparator;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *   Data class - when the user was last seen.
 *  
 *   @author Ondrej Zizka
 */
public class SeenInfo {

    public final String userOrChannel;
    public final Date when;


    public SeenInfo( String userOrChannel, Date when ) {
        this.userOrChannel = userOrChannel;
        this.when = when;
    }
    
    
    public static final Comparator<SeenInfo> WHEN_COMPARATOR = new Comparator<SeenInfo>() {
        public int compare( SeenInfo o1, SeenInfo o2 ) {
            return o1.when.compareTo( o2.when );
        }
    };
            


    @Override
    public boolean equals( Object obj ) {
        if( obj == null ) {
            return false;
        }
        if( getClass() != obj.getClass() ) {
            return false;
        }
        final SeenInfo other = (SeenInfo) obj;
        if( (this.userOrChannel == null) ? (other.userOrChannel != null) : !this.userOrChannel.equals( other.userOrChannel ) ) {
            return false;
        }
        return true;
    }


    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }
    
    
}

// class

