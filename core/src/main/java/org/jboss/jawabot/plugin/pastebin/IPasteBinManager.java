/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.jawabot.plugin.pastebin;

import java.util.List;

/**
 *
 * @author ondra
 */
public interface IPasteBinManager {

   boolean addEntry(PasteBinEntry e);

   PasteBinEntry getById(long id);

   PasteBinEntry getPaste(long id);

   List<PasteBinEntry> getPastes_OrderByWhenDesc(int i);
   
}
