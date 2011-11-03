package org.jboss.seam.wicket;

import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WebApplication;
import org.jboss.seam.solder.beanManager.BeanManagerProvider;

/**
* A {@link BeanManagerProvider} that looks it up from servletContext. ServletContext is previously resolved from Wicket application.
*
* @author Marek Smigielski
*/
public class ServletContextBeanManagerProvider implements BeanManagerProvider {

    @Override
    public int getPrecedence() {
        return 12;
    }

    @Override
    public BeanManager getBeanManager() {
        ServletContext servletContext = ((WebApplication) Application.get()).getServletContext();
        if (servletContext != null) {
            return (BeanManager) servletContext.getAttribute("org.jboss.weld.environment.servlet" + "."
                    + BeanManager.class.getName());
        }
        return null;
    }

}
