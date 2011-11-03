package org.jboss.jawabot.web._pg.test;

import java.util.Random;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 *  
 *  @author Ondrej Zizka
 */
@ApplicationScoped
public class RandomNumberGenerator {

   private Random random = new Random(System.currentTimeMillis());

   @Produces @Named int getRandomNumber() {
      return random.nextInt(100);

   }

}// class



