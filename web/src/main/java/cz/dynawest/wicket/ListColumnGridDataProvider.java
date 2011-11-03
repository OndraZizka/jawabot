/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.dynawest.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;





/**
 * Allows the use of lists with {@link DataView}. The only requirement is that either list items
 * must be serializable or model(Object) needs to be overridden to provide the proper model
 * implementation.
 * 
 * @author Ondrej Zizka
 * @param <T>
 * 
 * TODO: Would be better to recalculate right in custom iterator.
 * TODO: Perhaps even better would be to implement whole List which re-orders another list.
 *       Custom iterator can't be implemented without private access to the ArrayList
 *       because it uses modCount. So we can't check for concurrent modification.
 */
public class ListColumnGridDataProvider<T extends Serializable> extends  ListDataProvider<T>
{
	private static final long serialVersionUID = 1L;

 
   private int columns = 1;
   
   public int getColumns() { return columns; }
   
   public ListColumnGridDataProvider<T> setColumns( int columns ) {
      if( columns < 1 )
         throw new IllegalArgumentException("Column count must be > 0, was "+columns);
      this.columns = columns;
      return this;
   }
   
   
   /**
    *   Shuffles list's items appropriately.
    * 
    *   0 1        0 3
    *   2 3   ->   1 4
    *   4 -        2 -
    * 
    * @param list  List to shuffle. Kept intact.
    * @return      new shuffled list.
    */
   private static <T> List<T> recalculateList( List<T> list, int cols )
   {
      // Column height.
      int colHeight = list.size() / cols;
      // Last items may not fit whole row.
      colHeight += ( (list.size() % cols) == 0 ? 0 : 1 );
      System.out.println( "SIZE: "+list.size()+"  HEI: "+colHeight );///
      
      //List list2 = new ArrayList<T>( colHeight * cols );
      Object[] list2 = new Object[ colHeight * cols ];
      for ( int i = 0; i < list.size(); i++ ) {
         int newIndex = (i % colHeight) * cols + (i / colHeight);
         //list2.set( newIndex, list.get(i) );
         list2[ newIndex ] = list.get(i);
         System.out.println("  list2["+newIndex+"] <- list["+i+"]  = " + list.get(i) );///
      }
      return ( List<T> ) Arrays.asList( list2 );
   }
   

	/**
	 * Constructs an empty provider. Useful for lazy loading together with {@linkplain #getData()}
	 */
	public ListColumnGridDataProvider()	
   {
		super();
	}

	/**
	 * 
	 * @param list
	 *            the list used as dataprovider for the dataview
	 */
	public ListColumnGridDataProvider(List<T> list)
	{
      super( list );
	}

	/**
	 * @see IDataProvider#iterator(int, int)
	 */
   @Override
	public Iterator<? extends T> iterator(final int first, final int count)
	{
		List<T> list = getData();
      list = this.recalculateList( list, this.columns );

		int toIndex = first + count;
		if (toIndex > list.size())
		{
			toIndex = list.size();
		}
		return list.subList(first, toIndex).listIterator();
	}

   @Override
   public int size() {
      int listLen = getData().size();
      // Column height.
      int colHeight = listLen / this.columns;
      // Last items may not fit whole row.
      colHeight += ( (listLen % this.columns) == 0 ? 0 : 1 );
      return colHeight * this.columns;
   }
   
   
   
  
}
