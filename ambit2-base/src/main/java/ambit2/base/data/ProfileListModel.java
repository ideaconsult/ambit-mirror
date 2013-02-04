/* ProfileListModel.java
 * Author: nina
 * Date: Apr 17, 2009
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


public class ProfileListModel extends AbstractListModel implements	TypedListModel<Property> , ListDataListener{
	static Logger logger = Logger.getLogger(ProfileListModel.class.getName());
	protected TypedListModel<Property> fields;
	protected List<Integer> visibleIndex = new ArrayList<Integer>();
	protected Boolean visibility = null;
	
	public ProfileListModel(Profile<Property> profile) {
		this(profile,null);
	}
	public ProfileListModel(Profile<Property> profile, Boolean visibility) {
		this.visibility = visibility;

		TypedDefaultListModel model = new TypedDefaultListModel();
		Iterator<Property> i = profile.iterator();
		while (i.hasNext()) 
			model.addElement(i.next());
		
		fields = model;
		if (fields != null)	fields.addListDataListener(this);
		setVisible(visibility);
	}
	
	public ProfileListModel(Hashtable<String,Profile<Property>> profiles, Boolean visibility) {
		this.visibility = visibility;
		TypedDefaultListModel model = new TypedDefaultListModel();
		Enumeration<String> e = profiles.keys();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			Profile<Property> profile = profiles.get(key);
			logger.finest(key);
			Iterator<Property> i = profile.iterator();
			while (i.hasNext()) {
				Property p = i.next();
				if (p.isEnabled()) {
					model.addElement(p);
					logger.finest(p.toString());

				}
			}
		}
		model.sort();
		fields = model;
		if (fields != null)	fields.addListDataListener(this);
		setVisible(visibility);
	}	
	public ProfileListModel(TypedListModel<Property> fields) {
		this(fields,null);
	}
	public ProfileListModel(TypedListModel<Property> fields, Boolean visibility) {
		this.visibility = visibility;
		this.fields = fields;
		if (fields != null)	fields.addListDataListener(this);
		setVisible(visibility);
	}
	
	protected void setVisible(Boolean visibility) {
		visibleIndex.clear();
		if (fields == null) return;
		for (int i=0; i < fields.getSize();i++) 
			if ((visibility==null) || !(visibility ^ fields.getElementAt(i).isEnabled())) 
				visibleIndex.add(i);
			
	}
	
	protected Property addElement(Property p,Boolean visibility) {
		if ((visibility==null) || !(visibility ^ p.isEnabled())) {
			visibleIndex.add(fields.getSize()-1);
		}
		return p;
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7758488965678782461L;

	public Property getElementAt(int index) {
		int i = getVisibleIndex(index);
		if (i>=0) return fields.getElementAt(i);
		else return null;
	}

	public int getSize() {
		return visibleIndex.size();
	}
	protected int getVisibleIndex(int index) {
		if (index<visibleIndex.size()) return visibleIndex.get(index);
		else return 0;
	}
	public void contentsChanged(ListDataEvent e) {
		setVisible(visibility);
		fireContentsChanged(this, getVisibleIndex(e.getIndex0()),getVisibleIndex(e.getIndex1()));
	}
	public void intervalAdded(ListDataEvent e) {
		setVisible(visibility);		
		fireIntervalAdded(this,getVisibleIndex(e.getIndex0()),getVisibleIndex(e.getIndex1()));
		
	}
	public void intervalRemoved(ListDataEvent e) {
		setVisible(visibility);		
		fireIntervalRemoved(this,getVisibleIndex(e.getIndex0()),getVisibleIndex(e.getIndex1()));
		
	}
	
}

class TypedDefaultListModel extends AbstractListModel  implements TypedListModel<Property> {
	protected ArrayList<Property> list = new ArrayList<Property>();
	/**
	 * 
	 */
	private static final long serialVersionUID = 9212690589287644925L;

	public Property getElementAt(int index) {
		Object o = list.get(index);
		return (Property)o;
	}


	public int getSize() {
		return list.size();
	}
	
    public void addElement(Property obj) {
    	int index = list.size();
    	list.add(obj);
    	fireIntervalAdded(this, index, index);
    }
    public void sort() {
    	Collections.sort(list, new Comparator<Property>() {
    		public int compare(Property o1, Property o2) {
    			
    			int i = o1.getReference().getTitle().compareTo(o2.getReference().getTitle());
    			if (i==0) return o1.getName().compareTo(o2.getName());
    			else return i;
    		}
    	});
    
    }
	
}
