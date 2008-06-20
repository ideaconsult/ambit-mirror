/*
Copyright (C) 2005-2008  

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

package ambit.data.molecule;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ambit.data.AmbitObject;
import ambit.data.qmrf.AbstractQMRFChapter;
import ambit.data.qmrf.QMRFAttributes;
import ambit.io.XMLException;

public class FunctionalGroup extends AmbitObject {
	protected QMRFAttributes properties;
	protected static final String t_name = "name";
	protected static final String t_smarts = "smarts";
	protected static final String t_hint = "hint";
	protected static final String t_family = "family";	

	public FunctionalGroup() {
		this("","","");
		
	}
	public FunctionalGroup(String name,String smarts,String hint,String family) {
		super();
		properties = new QMRFAttributes( new String[] {t_family,t_name,t_smarts,t_hint});
		setFamily(family);
		setSmarts(smarts);
		setName(name);
		setHint(hint);
	}		
	public FunctionalGroup(String name,String smarts,String hint) {
		this(name,smarts,hint,"");
	}	
	@Override
	public void clear() {
		properties.clear();
		super.clear();
	}
	public String getFamily() {
		return properties.get(t_family);
	}	
	public String getHint() {
		return properties.get(t_hint);
	}
	public String getName() {
		return properties.get(t_name);
	}
	public String getSmarts() {
		return properties.get(t_smarts);
	}
	public void setName(String name) {
		properties.put(t_name,name);
	}
	public void setSmarts(String smarts) {
		properties.put(t_smarts,smarts);
	}
	public void setHint(String hint) {
		properties.put(t_hint,hint);
	}	
	public void setFamily(String family) {
		properties.put(t_family,family);
	}		
	@Override
	public String toString() {
		return "[" +getFamily() + "] " + getName()  ;
	}
    public Element toXML(Document document) throws XMLException {
        Element element = document.createElement("group");
        properties.toXML(element);
        return element;
    }	
}
