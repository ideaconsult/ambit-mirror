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

package ambit.data.qmrf;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ambit.data.AmbitObject;
import ambit.data.IAmbitEditor;
import ambit.data.IAmbitSearchable;
import ambit.exceptions.AmbitException;
import ambit.io.XMLException;

public class CatalogEntry extends AmbitObject implements InterfaceQMRF , IAmbitSearchable{
	protected QMRFAttributes attributes;    
	public CatalogEntry(String elementID) {
		super(elementID);
		attributes = new QMRFAttributes();
	}
    public CatalogEntry(String elementID,String[] names) {
        super(elementID);
        attributes = new QMRFAttributes(names);
    }    
	public CatalogEntry(String elementID,ArrayList<String> names) {
		super(elementID);
		attributes = new QMRFAttributes(names);
	}	
	public void setproperty(String key,String value) {
		attributes.put(key, value);
		setModified(true);
	}
	public String getProperty(String key) {
		return attributes.get(key);
	}
	@Override
	public void clear() {
		attributes.clear();
		setModified(true);
	}
	public void fromXML(Element xml) throws XMLException {
    	clear();
    	setName(xml.getNodeName());		
		attributes.fromXML(xml);
	}

	public Element toXML(Document document) throws XMLException {
		Element e = document.createElement(getName());
		attributes.toXML(e);
		return e;
	}
	@Override
	public IAmbitEditor editor(boolean editable) {
		ArrayList<String> n = new ArrayList<String>();
		for (int i=0; i < attributes.getNames().size();i++)
			if (
                    "id".equals(attributes.getNames().get(i)) || 
                    "catalog".equals(attributes.getNames().get(i)) ||
                    "publication_ref".equals(attributes.getNames().get(i)) || //TODO create editor for references
                    "number".equals(attributes.getNames().get(i))
                    ) continue;
			else n.add(attributes.getNames().get(i));
		CatalogEntryPanel p = new CatalogEntryPanel(this,n,n);
		p.setEditable(editable);
		return p;
	}
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		Enumeration e = attributes.keys();
		boolean first = true;
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			if ("id".equals(key) || "catalog".equals(key)) continue;
            
			else {
				if (!first) {
					b.append(" ");
				} else first = false;
				b.append(attributes.get(key));
				
			}
		}
		return b.toString();
	}
	public int search(Object query, boolean all) throws AmbitException {
		
		Iterator<String> values = attributes.values().iterator();
		int found = 0;
		String q = query.toString().toLowerCase();
		while (values.hasNext()) {
			String s = values.next().toLowerCase();
			if (s.startsWith(q)) {
				found++;
				if (!all) return found;
			}
		}	
		return found;
	}
}


