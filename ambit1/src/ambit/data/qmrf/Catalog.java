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

import java.awt.Dimension;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import ambit.data.AmbitList;
import ambit.data.AmbitObject;
import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitEditor;
import ambit.data.IAmbitObjectListener;
import ambit.data.IAmbitSearchable;
import ambit.exceptions.AmbitException;
import ambit.io.XMLException;

public class Catalog extends AmbitList implements InterfaceQMRF, IAmbitSearchable, IAmbitObjectListener {
	protected static String _id= "id";
    protected Catalog external_catalog;
	protected int itemIndex = 0;
	protected boolean saveSelectedOnly = false;
	protected List<String> ids;
	public static final String[][] catalog_names = {
		{"software_catalog","Software","software","software_ref"},
		{"algorithms_catalog","Algorithms","algorithm","algorithm_ref"},
		{"descriptors_catalog","Descriptors","descriptor","descriptor_ref"},
		{"endpoints_catalog","Endpoints","endpoint","endpoint_ref"},
		{"publications_catalog","Publications","publication","publication_ref"},
		{"authors_catalog","Authors","author","author_ref"}
	};    
	public static final String[][] attribute_names = {
		{"name","url","description","contact","number","id"},
		{"definition","description","publication_ref","id"},
		{"name","units","description","publication_ref","id"},
		{"group","subgroup","name","id"},
		{"title","url","id"},
		{"name","affiliation","contact","url","email","number","id"}
	};    	
	public Catalog(String elementID) {
		super();
		setName(elementID);
		ids = new ArrayList<String>();
	}
	public String getReferenceName() {
		return catalog_names[itemIndex][3];
	}
	@Override
	public void setName(String name) {
		super.setName(name);
		for (int i=0; i < catalog_names.length; i++) 
			if (catalog_names[i][0].equals(name)) {
				itemIndex = i;
				break;
			}
		
	}
	public static String[] getAttributeNames(String catalogName) {
		for (int i=0; i < catalog_names.length;i++)
			if (catalogName.equals(catalog_names[i][0])) 
					return attribute_names[i];
		return null;
	}
	
	public void from(String xml) throws Exception {
		  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          factory.setNamespaceAware(true);      
          factory.setValidating(false);
  		  DocumentBuilder builder = factory.newDocumentBuilder();
  		
          Document doc = builder.parse(new InputSource(new StringReader(xml)));
          fromXML(doc.getDocumentElement());
	}
	public void fromXML(Element xml) throws XMLException {
    	clear();
    	setName(xml.getNodeName());	
    	NodeList children = xml.getChildNodes();
        for (int i=0; i < children.getLength();i++) 
            if (Node.ELEMENT_NODE==children.item(i).getNodeType()) {
                //System.out.println(nodes.item(i).getLocalName());
            	String name = children.item(i).getLocalName();
            	if (catalog_names[itemIndex][2].equals(name)) {
	            	CatalogEntry entry = new CatalogEntry(name,attribute_names[itemIndex]) ;
	            	entry.fromXML((Element)children.item(i));
	                addItem(entry);
	                ids.add(entry.getProperty(_id));
            	} 
            }            
      
        setModified(true);
		
	}

	public Element toXML(Document document) throws XMLException {
		Element e = document.createElement(getName());
		for (int i=0; i < size(); i++) {
			AmbitObject o = getItem(i);
			
			if ((o instanceof InterfaceQMRF) && (!saveSelectedOnly || (saveSelectedOnly && o.isSelected())))
				e.appendChild(((InterfaceQMRF) getItem(i)).toXML(document));
		}
		return e;
	}
	@Override
	public String toString() {
		return catalog_names[itemIndex][1];
	}
	@Override
	public AmbitObject createNewItem() {
		CatalogEntry c =  new CatalogEntry(catalog_names[itemIndex][2],attribute_names[itemIndex]);
		c.setproperty("catalog", getName());
		return c;
	}
	@Override
	public int addItem(AmbitObject entry) {
		int i= super.addItem(entry);
        CatalogEntry e = ((CatalogEntry) entry);
        Object id = e.getProperty(_id);
		if ((i > -1) && ((id ==null) || ("".equals(e.getProperty(_id))))) {
			String newId = getNewId();	
			ids.add(newId);
			e.setproperty(_id, newId);
		}
		e.addAmbitObjectListener(this);
		return i;
	}
	@Override
	protected Object remove(int i, boolean notify) {
		AmbitObject o = (AmbitObject) super.remove(i, notify);
		o.removeAmbitObjectListener(this);
		return o;
	}
	@Override
	public boolean remove(Object o) {
		((AmbitObject)o).removeAmbitObjectListener(this);
		return super.remove(o);
	}
	protected synchronized String getNewId() {
		String newid;
		int s = size();
		do {
			newid = getName()+"_"+Integer.toString(s);
			s++;
		} while (ids.indexOf(newid)!=-1);
		return newid;
	}
	public int search(Object query, boolean all) throws AmbitException {
		int found = -1;
		for (int i = 0; i < size(); i++)
			getItem(i).setSelected(false);
		for (int i = 0; i < size(); i++) 
			if (getItem(i) instanceof IAmbitSearchable) {
				found = ((IAmbitSearchable) getItem(i)).search(query,all);
				
				if (found>0) {
					getItem(i).setSelected(true);
					if (!all) return i;
					found = i;
				}	

			}	
		return found;
	}
	public CatalogEntry id(String idref) {
        if (idref == null) return null;
        CatalogEntry e = null;
        for (int i = 0; i < size(); i++) {
            e = ((CatalogEntry)getItem(i));
            if (idref.equals(e.getProperty(_id)))  
                break; 
                
        }
        return e;
    }
    @Override
    public IAmbitEditor editor(boolean editable) {
    	CatalogEditor e = new CatalogEditor(this,true,new Dimension(200,80));
        e.setNoDataText("Click on <+> to add a new item");
        e.setEditable(editable);
        return e;
    }
	public boolean isSaveSelectedOnly() {
		return saveSelectedOnly;
	}
	public void setSaveSelectedOnly(boolean saveSelectedOnly) {
		this.saveSelectedOnly = saveSelectedOnly;
	}
	
	public int addCatalog(Catalog external) throws Exception {
        if (external != null) { //add`
        	//System.out.println("Adding catalog "+ external.getName());
    		if (!external.getName().equals(getName())) throw new Exception("Uncompatible catalog types "+getName() + " and " + external.getName());

    		int count = 0;
        	for (int j=0; j < external.size(); j++) {
        		((CatalogEntry)external.getItem(j)).setproperty(_id,"");
        		addItem(external.getItem(j));
        		count++;
        	}	
        	return count;
        } else return 0;
	}
	public Catalog getExternal_catalog() {
		return external_catalog;
	}
	public void setExternal_catalog(Catalog external_catalog) {
		this.external_catalog = external_catalog;
	}
	public void ambitObjectChanged(AmbitObjectChanged event) {
		
		fireAmbitListEvent();
		
	}
}


