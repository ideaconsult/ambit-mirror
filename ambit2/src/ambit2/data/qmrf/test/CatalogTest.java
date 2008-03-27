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

package ambit2.data.qmrf.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import ambit2.data.qmrf.Catalog;
import ambit2.data.qmrf.CatalogEntry;

public class CatalogTest extends TestCase {

		public void testReadURL() {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            factory.setNamespaceAware(true);      
	            factory.setValidating(false);
	    		DocumentBuilder builder = factory.newDocumentBuilder();
	
	    		
	    		URL e_url = new URL("http://nina.acad.bg/qmrf/endpoints_xml.jsp");
				BufferedReader r = new BufferedReader(new InputStreamReader(e_url.openStream()));
				StringBuffer b = new StringBuffer();
				String l;
				while ((l = r.readLine()) != null) {
			         b.append(l);
			       } // end while 
				r.close();
	    		
				//System.out.println(b.toString());
		        Document doc = builder.parse(new InputSource(new StringReader( b.toString().trim())));
		        Catalog c = new Catalog(Catalog.catalog_names[3][0]);
		        c.fromXML(doc.getDocumentElement());
		        for (int i=0; i < c.size(); i++) {
	    			System.out.println(c.getItem(i));
	    		//	assertFalse(((CatalogEntry) c.getItem(i)).getProperty("name").equals("")); 
	    		}
		        
		        
		        Catalog e = new Catalog(Catalog.catalog_names[3][0]);
		        e.fromXML(doc.getDocumentElement());
		        e.addCatalog(c);
		        e.editor(true).view(null,true,"catalogs");
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	  public void testExternalCatalogs() {
	    	Catalog c = new Catalog(Catalog.catalog_names[3][0]);
	    	Catalog e = new Catalog(Catalog.catalog_names[3][0]);
	    	
	    	String src = "<endpoints_catalog>" +
	  "<endpoint id=\"endpoint1\" group=\"1.Physicochemical effects\" name=\"1.1.Melting point\" />"+ 
	  "<endpoint id=\"endpoint2\" group=\"1.Physicochemical effects\" name=\"1.2.Boiling point\" />"+ 
	  "<endpoint id=\"endpoint3\" group=\"1.Physicochemical effects\" name=\"1.3.Water solubility\" />"+ 
	  "<endpoint id=\"endpoint4\" group=\"1.Physicochemical effects\" name=\"1.4.Vapour pressure\" />"+
	  "</endpoints_catalog>";

	    	
	    	String src1 = "<endpoints_catalog>" +
	  	   
	  	 "<endpoint id=\"endpoint5\" group=\"1.Physicochemical effects\" name=\"1.5.Surface tension\" />"+ 
	  	  "<endpoint id=\"endpoint6\" group=\"1.Physicochemical effects\" name=\"1.6.1.Partition coefficients: Octanol-water (Kow)\" />"+ 
	  	  "<endpoint id=\"endpoint7\" group=\"1.Physicochemical effects\" name=\"1.6.2.Partition coefficients: Octanol-water (D)\" />"+ 
	  	"<endpoint id=\"endpoint1\" group=\"1.Physicochemical effects\" name=\"1.1.Melting point\" />"+ 
	  	  "<endpoint id=\"endpoint2\" group=\"1.Physicochemical effects\" name=\"1.2.Boiling point\" />"+
	  	  "</endpoints_catalog>";	    	
	    	try {
	    		c.from(src);
	    		assertEquals(4,c.size());
	    		e.from(src1);
	    		assertEquals(5,e.size());
	    		
	    		c.addCatalog(e);
	    		assertEquals(9,c.size());
	    		for (int i=0; i < c.size(); i++) {
	    			//System.out.println(c.getItem(i));
	    			assertFalse(((CatalogEntry) c.getItem(i)).getProperty("name").equals("")); 
	    		}
	    		
	    	} catch (Exception x) {
	    		fail();
	    		x.printStackTrace();
	    	}
	    }    	

}


