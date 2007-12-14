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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import ambit.io.XMLException;

public class Catalogs extends Hashtable<String, Catalog> implements
		InterfaceQMRF {
	protected String name; 
	protected boolean editable = true;
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Catalogs() {
		super();
		setName("Catalogs");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

	}

	public void fromXML(Element xml) throws XMLException {
        for (int c=0; c < Catalog.catalog_names.length; c++) {
        	String tag = Catalog.catalog_names[c][0].toString();
            NodeList nodes = xml.getElementsByTagName(tag);
            for (int i=0; i < nodes.getLength(); i++) 
                if (Node.ELEMENT_NODE==nodes.item(i).getNodeType()) {

                    Catalog catalog = new Catalog(tag);
                    catalog.fromXML((Element)nodes.item(i));
                    put(catalog.getName(),catalog);
                    catalog.setEditable(isEditable());
                }

        }   
	}

	public Element toXML(Document document) throws XMLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void read(InputSource source) throws Exception {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setNamespaceAware(true);      
	        factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(source);
			fromXML(doc.getDocumentElement());

	}
}


