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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.repeater.data.IDataProvider;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;




/**
 * Allows the use of lists with {@link DataView}. The only requirement is that either list items
 * must be serializable or model(Object) needs to be overridden to provide the proper model
 * implementation.
 * 
 * @author Igor Vaynberg ( ivaynberg )
 * @author Ondrej Zizka
 * @param <T>
 * 
 * TODO: Would be better to recalculate right in the iterator.
 * 
 */
public class ListColumnGridDataProvider<T extends Serializable> implements IDataProvider<T>
{
	private static final long serialVersionUID = 1L;

	/** reference to the list used as dataprovider for the dataview */
	private final List<T> list;
	private List<T> list2;
   
   private int columns = 1;
   
   public int getColumns() { return columns; }
   
   public ListColumnGridDataProvider<T> setColumns( int columns ) {
      if( columns < 1 )
         throw new IllegalArgumentException("Column count must be > 0, was "+columns);
      this.columns = columns;
      this.recalculateList2();
      return this;
   }
      
   private void recalculateList2() {
      int cols = this.getColumns();
      // Column height.
      int colHeight = list.size() / cols;
      // Last items may not fit whole row.
      colHeight += ( (list.size() % cols) == 0 ? 0 : 1 );
      System.out.println( "HEI: "+colHeight );///
      
      list2 = new ArrayList<T>( list );
      for ( int i = 0; i < list.size(); i++ ) {
         int newIndex = (i % colHeight) * cols + (i / colHeight);
         list2.set( newIndex, list.get(i) );
         System.out.println("  list2["+newIndex+"] <- list["+i+"] ");///
      }
   }
   

	/**
	 * Constructs an empty provider. Useful for lazy loading together with {@linkplain #getData()}
	 */
	public ListColumnGridDataProvider()
	{
		this(Collections.<T> emptyList());
	}

	/**
	 * 
	 * @param list
	 *            the list used as dataprovider for the dataview
	 */
	public ListColumnGridDataProvider(List<T> list)
	{
		if (list == null)
		{
			throw new IllegalArgumentException("argument [list] cannot be null");
		}

		this.list = list;
		this.list2 = new ArrayList<T>(list.size());
	}

	/**
	 * Subclass to lazy load the list
	 * 
	 * @return The list
	 */
	protected List<T> getData()
	{
		return list2;
	}

	/**
	 * @see IDataProvider#iterator(int, int)
	 */
	public Iterator<? extends T> iterator(final int first, final int count)
	{
		List<T> list = getData();

		int toIndex = first + count;
		if (toIndex > list.size())
		{
			toIndex = list.size();
		}
		return list.subList(first, toIndex).listIterator();
	}

	/**
	 * @see IDataProvider#size()
	 */
	public int size()
	{
		return getData().size();
	}

	/**
	 * @see IDataProvider#model(Object)
	 */
	public IModel<T> model(T object)
	{
		return new Model<T>(object);
	}

	/**
	 * @see org.apache.wicket.model.IDetachable#detach()
	 */
	public void detach()
	{
	}

}
