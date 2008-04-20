/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.data.qmrf;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import ambit2.io.XMLException;


public class QMRFAttributes extends Hashtable<String,String>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8463974516821891755L;
	protected ArrayList<String> names;
	public QMRFAttributes() {
		super();
		this.names = null;
	}	

    public QMRFAttributes(String[] names) {
        super();
        this.names = new ArrayList<String>();
        for (int i=0; i < names.length;i++)
            this.names.add(names[i]);
    } 

	public QMRFAttributes(ArrayList<String> names) {
		super();
		this.names = names;
		for (int i=0; i < names.size(); i++)
			put(names.get(i),"");
	}
	public QMRFAttributes(ArrayList<String> names,ArrayList<String> values) {
		super();
		this.names = names;
        for (int i=0; i < names.size(); i++)
            put(names.get(i),values.get(i));
	}	
	public void fromXML(Element element) throws XMLException {
		if (names == null) {
			NamedNodeMap map = element.getAttributes();
			names = new ArrayList<String>();
			for (int i=0; i < map.getLength();i++) {
				names.add(map.item(i).getNodeName());
				put(names.get(i),map.item(i).getNodeValue());
			}	
		} else	
			for (int i=0; i < names.size(); i++) {
				String value = element.getAttribute(names.get(i));
				if (value == null) value = "";
				put(names.get(i),value);
			}	
		
	}

	public void toXML(Element element) throws XMLException {
		Enumeration e = keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			element.setAttribute(key.toString(), get(key).toString());
		}
	}
	public ArrayList<String> getNames() {
		return names;
	}
	public void setNames(String[] names) {
		if (this.names == null) this.names= new ArrayList<String>();
		else this.names.clear();
        for (int i=0; i < names.length;i++)
            this.names.add(names[i]);
	}	
	public void setNames(ArrayList<String> names) {
		if (this.names == null) this.names= new ArrayList<String>();
		else this.names.clear();

        for (int i=0; i < names.size();i++)
            this.names.add(names.get(i));
	}
	public void addName(String name) {
	    this.names.add(name);
    }

}


