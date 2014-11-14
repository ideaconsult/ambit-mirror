/*
 Copyright (C) 2005-2007  

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

package ambit2.descriptors;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class FuncGroupsDescriptorFactory extends DefaultAmbitProcessor<String,List<FunctionalGroup>>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3836212586696127104L;
	protected static Logger slogger = Logger.getLogger(FuncGroupsDescriptorFactory.class.getName());
	public transient static final String[] extensions = {".xml"};
	public transient static final String[] extensionDescription = 
		{"Functional groups with SMARTS notation (*.xml)"
		};	

	
	public static Document getDocument(String filename)
			throws Exception {

		return getDocument(new File(filename));
	}
	public static synchronized Document getDocument(File file) throws Exception {
		slogger.fine("Trying to load from file "+file.getAbsolutePath());		
		FileReader filereader = new FileReader(file);
		try {
			return getDocument(new InputSource(filereader));
		} catch (Exception x) {
			throw new Exception(x);
		} finally {
			filereader.close();	
		}
		
	}	
	public static synchronized Document getDocument(InputStream stream) throws Exception {
		slogger.fine("Trying to load from as a resource stream ");		
		try {
			return  getDocument(new InputSource(stream));
		} catch (Exception x) {
			throw new Exception(x);
		} finally {
			stream.close();	
		}		
	}	
	public static Document getDocument() throws Exception {
		slogger.fine("Trying to load the default ambit2/data/descriptors/funcgroups.xml");		
		ClassLoader c = FuncGroupsDescriptorFactory.class.getClassLoader();
		java.net.URL url = c.getResource("ambit2/descriptors/funcgroups.xml");
		slogger.fine("PATH: resolved name = " + url);
		InputStream is = url.openStream();
		return  getDocument(is);

	}	
	
	public static Document getDocument(InputSource source) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(source);
	}	
	public List<FunctionalGroup> process(String target) throws AmbitException {
		List<FunctionalGroup> groups = new ArrayList<FunctionalGroup>();
		try {
			Document doc=null;
			if (target == null)
				doc = getDocument();
			else
				try {
					doc = getDocument(target);
				} catch (Exception x) {
					try {
						
						doc = getDocument(FuncGroupsDescriptorFactory.class
							.getClassLoader().getResourceAsStream(target));
					} catch (Exception xx) {
						//if this fails, everything fails
						doc = getDocument();					
					}
			}
			NodeList nodes = doc.getDocumentElement().getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++)
				if (nodes.item(i).getNodeType() == Element.ELEMENT_NODE) {
					Element e = ((Element) nodes.item(i));
					try {
						groups.add(new FunctionalGroup(
								e.getAttribute("name"),
								e.getAttribute("smarts"),
								e.getAttribute("hint"),
								e.getAttribute("family"),
								e.getAttribute("example")
								));
					} catch (Exception x) {
						logger.log(Level.WARNING,x.getMessage(),x);
					}
				}
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		return groups;
	}


	public static void saveFragments(OutputStream writer,Collection<FunctionalGroup> fragments) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

		Element top = doc.createElement("funcgroups");
		doc.appendChild(top);
		for (FunctionalGroup group : fragments)
			try {
				top.appendChild(group.toXML(doc));
			} catch (Exception x) {
				slogger.log(Level.SEVERE,x.getMessage(),x);
			}
		
		
    	DOMSource domSource = new DOMSource(doc);
    	StreamResult streamResult = new StreamResult(writer);
    	TransformerFactory tf = TransformerFactory.newInstance();
    	Transformer serializer = tf.newTransformer();
    	serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
    	serializer.setOutputProperty(OutputKeys.INDENT,"yes");
    	serializer.transform(domSource, streamResult);

	}		
}
