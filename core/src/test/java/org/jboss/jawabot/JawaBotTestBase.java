
package org.jboss.jawabot;

import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 *
 * @author Ondrej Zizka
 */
public class JawaBotTestBase extends TestCase
{
   private static final Logger log = Logger.getLogger(JawaBotTestBase.class);


   @Override
   protected void setUp() throws Exception {
      super.setUp();
      log.info("\n=======================================================" +
                "\n  Setting up: " + this.getName() +
                "\n=======================================================");
   }

   @Override
   protected void tearDown() throws Exception {
      log.info("\n=========  Tearing down: " + this.getName() + "  ====================\n\n");
      super.tearDown();
   }



}// class
