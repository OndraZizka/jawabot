package org.jboss.jawabot.plugin.logger.irc;

import java.util.Date;

/**
 *
 * @author Ondrej Zizka
 */
public class MessagesCriteria {
   
   private String channel;
   private String user;
   private Date fromDate;
   private Date toDate;
   private int  num;

   
   public MessagesCriteria(String channel) {
      this.channel = channel;
   }

   
   //<editor-fold defaultstate="collapsed" desc="get/set">
   public String getChannel() {
      return channel;
   }
   
   public void setChannel(String channel) {
      this.channel = channel;
   }
   
   public Date getFromDate() {
      return fromDate;
   }
   
   public void setFromDate(Date fromDate) {
      this.fromDate = fromDate;
   }
   
   public int getNum() {
      return num;
   }
   
   public void setNum(int num) {
      this.num = num;
   }
   
   public Date getToDate() {
      return toDate;
   }
   
   public void setToDate(Date toDate) {
      this.toDate = toDate;
   }
   
   public String getUser() {
      return user;
   }
   
   public void setUser(String user) {
      this.user = user;
   }
   //</editor-fold>

   @Override
   public String toString() {
      return "MessagesCriteria{" + "#" + channel + ", user=" + user + ", from=" + fromDate + ", to=" + toDate + ", num=" + num + '}';
   }
   
}// class

