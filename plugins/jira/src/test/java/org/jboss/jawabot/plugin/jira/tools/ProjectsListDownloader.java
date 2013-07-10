package org.jboss.jawabot.plugin.jira.tools;


//import com.atlassian.jira.rest.client.api.domain.BasicProject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  A tool class for updating the projects in Jira plugin config.
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
//@javax.enterprise.inject.Vetoed
public class ProjectsListDownloader {
    private static final Logger log = LoggerFactory.getLogger( ProjectsListDownloader.class );

    List<Project> downloadProjectList( String baseUrl ) throws IOException, JSONException {
        
        String url = baseUrl + "rest/api/2/project";
        String projJson = downloadJson( url );
        JSONArray array = new JSONArray( projJson );
        
        List<Project> ret = new LinkedList();
        for( int i = 0; i < array.length(); i++ ) {
            JSONObject obj = array.getJSONObject( i );
            Project project = new Project( 
                    obj.getString("key"), 
                    obj.getString("name"),
                    "https://issues.jboss.org/browse/" + obj.getString("key")
            );
            ret.add( project );
        }
        
        /*Iterable<BasicProject> projects = new BasicProjectsJsonParser().parse( array );
        for( Iterator<BasicProject> it = projects.iterator(); it.hasNext(); ) {
            BasicProject prj = it.next();
            ret.add( prj );
        }*/
        
        return ret;
    }


    private String downloadJson( String url ) throws IOException {
        try {
            final InputStream is = new URL(url).openStream();
            java.util.Scanner scan = new java.util.Scanner( is).useDelimiter("\\A");
            String source = scan.hasNext() ? scan.next() : "";
            is.close();
            return source;
        } catch( IOException ex ) {
            throw new IOException("Can't retrieve the JSON source from '"+url+"': " + ex.getMessage(), ex );
        }
    }
    
    public static void main( String[] args ) throws IOException, JSONException {
        // https://issues.jboss.org/
        // https://issues.apache.org/jira/
        // http://jira.codehaus.org/

        
        //System.out.println("<repository name=\"%s\" url=\"%s\" type=\"jira50\">");
        List<Project> projects = new ProjectsListDownloader().downloadProjectList("http://jira.codehaus.org/");
        for( Project project : projects ) {
            System.out.println(String.format("<project id=\"%s\" name=\"%s\"/>", project.prefix, project.name));
        }
        //System.out.println("</repository>");
    }

}// class
