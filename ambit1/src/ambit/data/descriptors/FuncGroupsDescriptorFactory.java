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

package ambit.data.descriptors;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ambit.data.molecule.FunctionalGroup;
import ambit.data.molecule.SmartsFragmentsList;

public class FuncGroupsDescriptorFactory {
	public transient static final String[] extensions = {".xml"};
	public transient static final String[] extensionDescription = 
		{"Functional groups with SMARTS notation (*.xml)"
		};	

	
	private FuncGroupsDescriptorFactory() {
		super();
	}

	
	public static Document getDocument(String filename)
			throws Exception {
		return getDocument(new File(filename));
	}
	public static Document getDocument(File file)
		throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = null;
		if (file.exists())
			try {
				doc = builder.parse(file);
			} catch (Exception x) {
				x.printStackTrace();
				doc = null;
			}
		if (doc == null)	
			return builder.parse(FuncGroupsDescriptorFactory.class
					.getClassLoader().getResourceAsStream(
							"ambit/data/descriptors/funcgroups.xml"));
		else return doc;
	}	
	
	public static void getDescriptors(
			IProcessDescriptor<FunctionalGroupDescriptor> process)
			throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(FuncGroupsDescriptorFactory.class
				.getClassLoader().getResourceAsStream(
						"ambit/data/descriptors/funcgroups.xml"));
		NodeList nodes = doc.getDocumentElement().getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++)
			if (nodes.item(i).getNodeType() == Element.ELEMENT_NODE) {
				Element e = ((Element) nodes.item(i));
				try {
					FunctionalGroupDescriptor d = new FunctionalGroupDescriptor();
					d.setParameters(new String[] { 
							e.getAttribute("smarts"),
							e.getAttribute("name"),
							e.getAttribute("hint")});
					process.process(d);
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
	}

	public static List<FunctionalGroupDescriptor> create() throws Exception {
		final ArrayList<FunctionalGroupDescriptor> list = new ArrayList<FunctionalGroupDescriptor>();
		getDescriptors(new IProcessDescriptor<FunctionalGroupDescriptor>() {
			public void process(FunctionalGroupDescriptor descriptor)
					throws Exception {
				list.add(descriptor);
			}
		});
		return list;
	}
	
	public static void getFragments(Document doc,SmartsFragmentsList fragments) throws Exception {
		fragments.clear();
		NodeList nodes = doc.getDocumentElement().getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++)
			if (nodes.item(i).getNodeType() == Element.ELEMENT_NODE) {
				Element e = ((Element) nodes.item(i));
				try {
					fragments.addItem(new FunctionalGroup(
							e.getAttribute("name"),
							e.getAttribute("smarts"),
							e.getAttribute("hint"),
							e.getAttribute("family")
							));
					
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
	}	
	public static void saveFragments(OutputStream writer,SmartsFragmentsList fragments) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

		Element top = doc.createElement("funcgroups");
		doc.appendChild(top);
		for (int i=0; i < fragments.size();i++) {
			FunctionalGroup group = (FunctionalGroup)fragments.getItem(i);
			top.appendChild(group.toXML(doc));
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
