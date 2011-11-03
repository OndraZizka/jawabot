
package org.jboss.jawabot.testbase;

import junit.framework.TestCase;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 

/**
 *
 * @author Ondrej Zizka
 */
public class JawaBotTestBase extends TestCase
{
   private static final Logger log = LoggerFactory.getLogger(JawaBotTestBase.class);


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
