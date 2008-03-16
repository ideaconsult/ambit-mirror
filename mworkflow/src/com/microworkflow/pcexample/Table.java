/*
 * 	 
 *  Copyright (c) 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */
package com.microworkflow.pcexample;

import java.util.HashMap;

/**
 * This object represents a rate table with one or two keys.
 * A real application would use an object-relational
 * persistence layer and read/write the data from/to a database.
 * 
 * @author dam
 */
public class Table extends Object {
	private static final String SINGLE_COLUMN_KEY = "singleColumnKey";
	HashMap tables;
	
	public Table() {
		tables=new HashMap();
	}

	public void put(Object columnKey,Object rowKey,Object value) {
		HashMap column=null;
		if (!tables.containsKey(columnKey)) {
			column=new HashMap();
			tables.put(columnKey,column);
		} else {
			column=(HashMap)tables.get(columnKey);
		}
		column.put(rowKey,value);
	}
	
	public void put(Object rowKey,Object value) {
		put(SINGLE_COLUMN_KEY,rowKey,value);
	}
	
	public Object get(Object columnKey,Object rowKey) {
		HashMap column=(HashMap)tables.get(columnKey);
		return column!=null ? column.get(rowKey) : null;
	}

	public Object get(Object rowKey) {
		return get(SINGLE_COLUMN_KEY,rowKey);
	}
}
