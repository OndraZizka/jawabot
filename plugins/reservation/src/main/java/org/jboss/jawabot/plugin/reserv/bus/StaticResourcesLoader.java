package org.jboss.jawabot.plugin.reserv.bus;



import java.util.*;

/**
 *
 * @author OndrejZizka
 * @deprecated  Use .config package classes.
 */
public class StaticResourcesLoader implements ResourcesLoader {

	public List<Resource> getResources() {

		List<Resource> resources = new ArrayList();

		resources.add( new Resource("jawa01", "EAP") );
		resources.add( new Resource("jawa02", "EAP") );
		resources.add( new Resource("jawa03", "EAP") );
		resources.add( new Resource("jawa04", "EAP") );
		resources.add( new Resource("jawa05", "Productization") );
		resources.add( new Resource("jawa06", "SOA") );
		resources.add( new Resource("jawa07", "SOA") );
		resources.add( new Resource("jawa08", "SOA") );
		resources.add( new Resource("jawa09", "SOA") );
		resources.add( new Resource("jawa10", "") );
		resources.add( new Resource("jawa11", "") );
		resources.add( new Resource("jawa12", "") );
		resources.add( new Resource("jawa13", "") );
		resources.add( new Resource("jawa14", "") );
		resources.add( new Resource("jawa15", "EWS") );
		resources.add( new Resource("jawa16", "OpenJDK") );
		resources.add( new Resource("jawa17", "") );
		resources.add( new Resource("jawa18", "") );
/*		resources.add( new Resource("jawa19", "") );
		resources.add( new Resource("jawa20", "") );
		resources.add( new Resource("jawa21", "") );
		resources.add( new Resource("jawa22", "") );
		resources.add( new Resource("jawa23", "") );
		resources.add( new Resource("jawa24", "") );
		resources.add( new Resource("jawa25", "") );
 */
		resources.add( new Resource("xen19", "") );
		resources.add( new Resource("xen64", "") );

		return resources;

	}

}
