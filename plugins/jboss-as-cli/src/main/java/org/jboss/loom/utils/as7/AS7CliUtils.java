/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 .
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.jboss.loom.utils.as7;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.dmr.ModelNode;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelType;

/**
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class AS7CliUtils {
    
    private final static String OP_KEY_PREFIX = "Operation step-";
    
    
    
    public static void removeResourceIfExists( ModelNode loggerCmd, ModelControllerClient aS7Client ) throws IOException, Exception {
        
        // Check if exists.
        if( ! exists( loggerCmd, aS7Client ))  return;
        
        // Remove.
        ModelNode res = aS7Client.execute( createRemoveCommandForResource( loggerCmd ) );
        throwIfFailure( res );
    }
    
    /**
     *  Queries the AS 7 if given resource exists.
     */
    public static boolean exists( final ModelNode resource, ModelControllerClient client ) throws IOException {
        ModelNode query = new ModelNode();
        // Read operation.
        query.get(ClientConstants.OP).set(ClientConstants.READ_RESOURCE_OPERATION);
        // Copy the address.
        query.get(ClientConstants.OP_ADDR).set( resource.get(ClientConstants.OP_ADDR) );
        ModelNode res = client.execute( query );
        return wasSuccess( res );
    }

    /** Parses the command string into ModalNode and calls the sister method. */
    public static boolean exists( String command, ModelControllerClient client ) throws IOException {
        return exists( parseCommand( command, false ), client);
    }

    
    public static ModelNode createRemoveCommandForResource( ModelNode resource ) {
        // Copy the address.
        ModelNode query = new ModelNode();
        query.get(ClientConstants.OP_ADDR).set( resource.get(ClientConstants.OP_ADDR) );
        // Remove operation.
        query.get(ClientConstants.OP).set(ClientConstants.REMOVE_OPERATION);
        
        return query;
    }
    
    
    
    /**
     *  If the result is an error, throw an exception.
     */
    private static void throwIfFailure(final ModelNode node) throws Exception {
        if( wasSuccess( node ) )
            return;
        
        final String msg;
        if (node.hasDefined(ClientConstants.FAILURE_DESCRIPTION)) {
            if (node.hasDefined(ClientConstants.OP)) {
                msg = String.format("Operation '%s' at address '%s' failed: %s", node.get(ClientConstants.OP), node.get(ClientConstants.OP_ADDR), node.get(ClientConstants.FAILURE_DESCRIPTION));
            } else {
                msg = String.format("Operation failed: %s", node.get(ClientConstants.FAILURE_DESCRIPTION));
            }
        } else {
            msg = String.format("Operation failed: %s", node);
        }
        throw new CliBatchException(msg, node);
    }
    
    private static boolean wasSuccess( ModelNode node ) {
        return ClientConstants.SUCCESS.equals( node.get(ClientConstants.OUTCOME).asString() );
    }
    
    
    

    /**
     * @returns A ModelNode with two properties: "failedOpIndex" and "failureDesc".
     */
    public static BatchFailure extractFailedOperationNode(final ModelNode node) throws Exception {
        
        if( ClientConstants.SUCCESS.equals( node.get(ClientConstants.OUTCOME).asString() ))
            return null;
        
        if( ! node.hasDefined(ClientConstants.FAILURE_DESCRIPTION))
            return null;
        
        ModelNode failDesc = node.get(ClientConstants.FAILURE_DESCRIPTION);
        if( failDesc.getType() != ModelType.OBJECT )
            return null;
        
        String key = failDesc.keys().iterator().next();
        // "JBAS014653: Composite operation failed and was rolled back. Steps that failed:" => ...
        
        ModelNode compositeFailDesc = failDesc.get(key);
        // { "Operation step-1" => "JBAS014803: Duplicate resource ...
        
        Set<String> keys = compositeFailDesc.keys();
        String opKey = keys.iterator().next();
        // "Operation step-XX"

        if( ! opKey.startsWith(OP_KEY_PREFIX) )
            return null;
        
        String opIndex = StringUtils.substring( opKey, OP_KEY_PREFIX.length() );
        
        return new BatchFailure( Integer.parseInt( opIndex ), compositeFailDesc.get(opKey).toString());
    }



    
    
    /**
     *  Joins the given list into a string of quoted values joined with ", ".
     * @param col
     * @return 
     */
    public static String joinQuoted( Collection<String> col ){

        if( col.isEmpty() )
            return "";
        
        StringBuilder sb = new StringBuilder();
        for( String item : col )
            sb.append(",\"").append(item).append('"');

        String str = sb.toString();
        str = str.replaceFirst(",", "");
        return str;
    }


    /**
     *  Parse CLI command into a ModelNode - /foo=a/bar=b/:operation(param=value,...) .
     * 
     *  TODO: Support nested params.
     */
    public static ModelNode parseCommand( String command ) {
       return parseCommand( command, true );
    }
    public static ModelNode parseCommand( String command, boolean needOp ) {
        String[] parts = StringUtils.split( command, ':' );
        if( needOp && parts.length < 2 )  throw new IllegalArgumentException("Missing CLI command operation: " + command);
        String addr = parts[0];
        
        ModelNode query = new ModelNode();
        
        // Addr
        String[] partsAddr = StringUtils.split( addr, '/' );
        for( String segment : partsAddr ) {
            String[] partsSegment = StringUtils.split( segment, "=", 2);
            if( partsSegment.length != 2 )  throw new IllegalArgumentException("Wrong addr segment format - need '=': " + command);
            query.get(ClientConstants.OP_ADDR).add( partsSegment[0], partsSegment[1] );
        }
        
        // No op?
        if( parts.length < 2 )  return query;
        
        // Op
        String[] partsOp = StringUtils.split( parts[1], '(' );
        String opName = partsOp[0];
        query.get(ClientConstants.OP).set(opName);
        
        // Op args
        if( partsOp.length > 1 ){
            String args = StringUtils.removeEnd( partsOp[1], ")" );
            for( String arg : args.split(",") ) {
                String[] partsArg = arg.split("=", 2);
                query.get(partsArg[0]).set( unquote( partsArg[1] ) );
            }
        }
        return query;
    }// parseCommand()
    
    
    /**
     *  Changes "foo\"bar" to foo"bar.
     *  Is tolerant - doesn't check if the quotes are really present.
     */
    public static String unquote( String string ) {
        string = StringUtils.removeStart( string, "\"" );
        string = StringUtils.removeEnd( string, "\"" );
        return StringEscapeUtils.unescapeJava( string );
    }

    
    
    /**
     *   Formats Model node to the form of CLI script command - /foo=a/bar=b/:operation(param=value,...) .
     */
    public static String formatCommand( ModelNode command ) {
        
        if( ! command.has(ClientConstants.OP) )
            throw new IllegalArgumentException("'"+ClientConstants.OP+"' not defined.");
        if( command.get(ClientConstants.OP).getType() != ModelType.STRING )
            throw new IllegalArgumentException("'"+ClientConstants.OP+"' must be a string.");
        if( ! command.has(ClientConstants.OP_ADDR) )
            throw new IllegalArgumentException("'"+ClientConstants.OP_ADDR+"' not defined.");
        if( command.get(ClientConstants.OP_ADDR).getType() != ModelType.LIST )
            throw new IllegalArgumentException("'"+ClientConstants.OP_ADDR+"' must be a list.");
        
        // Operation.
        String op = command.get(ClientConstants.OP).asString();
        
        // Address
        ModelNode addr = command.get(ClientConstants.OP_ADDR);
        StringBuilder sb = new StringBuilder();
        for( int i = 0; ; i++ ) {
            if( ! addr.has(i) )  break;
            ModelNode segment = addr.get( i );
            String key = segment.keys().iterator().next();
            sb.append('/').append(key).append('=').append(segment.get(key).asString());
        }
        sb.append(':').append(op);
        
        // Params.
        boolean hasParams = false;
        Set<String> keys = command.keys();
        for( String key : keys ) {
            switch( key ){
                case ClientConstants.OP:
                case ClientConstants.OP_ADDR: continue;
            }
            sb.append( hasParams ? ',' : '(');
            hasParams = true;
            sb.append(key).append('=').append(command.get(key));
        }
        if( hasParams )  sb.append(')');
        return sb.toString();
    }

   
    /**
     *  Escape CLI address element - the parts between / and = in /foo=bar/baz=moo .
     */
    public static String escapeAddressElement(String element) {
        element = element.replace(":", "\\:");
        element = element.replace("/", "\\/");
        element = element.replace("=", "\\=");
        element = element.replace(" ", "\\ ");
        return element;
    }
    
    
}// class
