/* SelectionBean.java
 * Author: nina
 * Date: Mar 1, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.base.data;

import java.util.ArrayList;
import java.util.List;


public class SelectionBean<T> extends AmbitBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4069289362667504269L;
	protected List<T> options;
	protected int selected;
	protected String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public T getSelected() {
		return options.get(selected);
	}
	public void setSelected(int index) {

		this.selected = index;
	}	
	public void setSelected(T selected) {
		setSelected(options.indexOf(selected));

	}
	public List<T> getOptions() {
		return options;
	}
	public void setOptions(List<T> options) {
		this.options = options;
	}
	public SelectionBean(List<T> options) {
		this.options = options;
	}	
	public SelectionBean() {
		this(new ArrayList<T>());
	}
	public SelectionBean(T[] options) {
		this(options,"");
	}
	public SelectionBean(T[] options,String title) {
		this(new ArrayList<T>());
		for (T option: options)
			this.options.add(option);
		setTitle(title);
	}	
	public void clear() {
		options.clear();
	}
	@Override
	public String toString() {
		return options.toString();
	}
	
	
}
