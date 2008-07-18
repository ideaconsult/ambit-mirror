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

package ambit.test;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import ambit.data.qmrf.Catalog;
import ambit.data.qmrf.QMRFObject;


public class CatalogTest extends TestCase {

	public void testFromXML() {
    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><authors_catalog><author affiliation=\"\" contact=\"\" email=\"rbenigni@iss.it\" id=\"AUTH1\" name=\"Romualdo Benigni\" number=\"1\" /></authors_catalog>";
    	
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);      
        factory.setValidating(false);
        
        try {
        	
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        
	        Document doc = builder.parse(new InputSource(new StringReader(xml)));
	    	Catalog catalog = new Catalog("authors_catalog");
	    	catalog.fromXML(doc.getDocumentElement());
	    	
	    	assertEquals(1,catalog.size());
	    	
	    	QMRFObject o = new QMRFObject();
	    	o.init();
	    	Catalog c = o.getCatalogs().get("authors_catalog");
	    	assertEquals(2,c.size());
	    	c.addItem(catalog.getItem(0));
	    	assertEquals(3,c.size());
	    	o.cleanCatalogs();
	    	assertEquals(2,c.size());
	    	
	    	c = o.getCatalogs().get("endpoints_catalog");
	    	System.out.println(c.size());
        } catch (Exception x) {
        	x.printStackTrace();
        	fail();
        }
	}

}


