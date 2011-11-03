package org.jboss.jawabot.mod.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;


/**
 *   Interface to bind plugins' tabs.
 * 
 *   @author Ondrej Zizka
 */
public interface ITabBarContrib {
   
   public Label getLabel( String id );
   public Class<? extends WebPage> getLinkedPage();

   
}// class

