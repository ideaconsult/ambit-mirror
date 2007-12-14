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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit.data.AmbitListChanged;
import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitEditor;
import ambit.data.IAmbitListListener;
import ambit.io.XMLException;

public class QMRFSubChapterReference extends QMRFSubChapterText implements IAmbitListListener {
	protected CatalogReference catalogReference;
	public QMRFSubChapterReference() {
		this("subchapter","0","Subchapter title","Help");
	}
    public QMRFSubChapterReference(String elementID) {
        this(elementID,"0","Title","Help on "+ elementID);
    }       
	public QMRFSubChapterReference(String elementid, String chapter,String title,String help) {
		super(elementid,chapter,title,help);
		catalogReference = new CatalogReference("");
		catalogReference.addListListener(this);
	}
    @Override
    protected void finalize() throws Throwable {
        catalogReference.removeListListener(this);
        super.finalize();
    }
	public Catalog getCatalog() {
		return catalogReference.getCatalog();
	}
	public void setCatalog(Catalog catalog) {
        catalogReference.clear();
        catalogReference.setCatalog(catalog);
        catalogReference.setName(catalog.getName());
	}
	@Override
	public IAmbitEditor editor(boolean editable) {
        return new QMRFSubChapterReferenceEditor(this, editable);
	}


    public void fromXML(Element xml) throws XMLException {
        super.fromXML(xml);
        NodeList children = xml.getChildNodes();
        for (int i=0; i < children.getLength();i++) 
            if (Node.ELEMENT_NODE==children.item(i).getNodeType()) {
            	/*
                System.out.print(children.item(i).getNodeType());
                System.out.print("\t");
                System.out.println(children.item(i).getLocalName());
                */
                
                NamedNodeMap attributes = children.item(i).getAttributes();
                if (attributes != null)
                for (int j=0; j < attributes.getLength(); j++) 
                    if ("idref".equals(attributes.item(j).getNodeName())) { 
                        //System.out.println(attributes.item(j).getNodeValue());
                        catalogReference.addReference(attributes.item(j).getNodeValue());
                    }    
            }
     
    }
    public Element toXML(Document document) throws XMLException {
        Element element = super.toXML(document);
        //element.appendChild(document.createTextNode(text));
        for (int i=0; i < catalogReference.size();i++) {
            Element ref = document.createElement(catalogReference.getCatalog().getReferenceName());
            ref.setAttribute("idref",((CatalogEntry) catalogReference.getItem(i)).getProperty("id"));
            element.appendChild(ref);
        }
        return element;
    }
    public synchronized CatalogReference getCatalogReference() {
        return catalogReference;
    }
    public synchronized void setCatalogReference(CatalogReference catalogReference) {
        this.catalogReference = catalogReference;
        setModified(true);
    }
    @Override
    public void setNotModified() {
        super.setNotModified();
        catalogReference.setNotModified();
    }
    public void ambitListChanged(AmbitListChanged event) {
        setModified(event.getList().isModified());
        
    }
    public void ambitObjectChanged(AmbitObjectChanged event) {
        setModified(event.getObject().isModified());
        
    }
  
}


