/* StudyTemplate.java
 * Author: Nina Jeliazkova
 * Date: 2006-5-2 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.base.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import ambit2.base.exceptions.AmbitIOException;

/**
 * Templates are just a (meaningful) set of properties 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2009-2-6
 */
public class Template extends Profile<Property> {
	/**
     * 
     */
    private static final long serialVersionUID = -9152728910421949089L;
    protected int id;
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
     * 
     */
    public Template() {
        this("DEFAULT");
    }
    
    public Template(String name) {
        super();
        setName(name);
    }
	public void addFields(String name, String units, boolean numeric,
			boolean isResult) {
		if (numeric)
			add(name,units,Number.class);
		else
			add(name,units,String.class);
	}    
	public void add(String name, String units, Class clazz) {
		Property p = Property.getInstance(name,getName());
		p.setLabel(name);
		p.setOrder(1);
		p.setClazz(clazz);
		p.setUnits(units);
		add(p);
	}
    public boolean save(OutputStream out) throws AmbitIOException {
    	try {
    		save(new OutputStreamWriter(out,"UTF-8"));
    		return true;
    	} catch (Exception x) {
    		throw new AmbitIOException(x);
    	}
    	

    }
    public boolean save(Writer writer) throws AmbitIOException {
    	Source source;
		try {
			source = new DOMSource(buildDocument());
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
	        xformer.setOutputProperty(OutputKeys.INDENT,"Yes");
	        xformer.setOutputProperty(OutputKeys.STANDALONE,"Yes");

	    
	        Result result = new StreamResult(writer);
	        xformer.transform(source, result);
	        writer.flush();
		
	        return true;
		} catch (Exception x) {
			throw new AmbitIOException(x);
		}
    

    }
    protected Document buildDocument() {
    	try {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        
	        factory.setNamespaceAware(true);      
	        factory.setValidating(true);        
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.newDocument();
	        Element top = doc.createElement("template");
	        top.setAttribute("name",getName());
	        for (Property tf : values()) {
	        	Element field = doc.createElement("field");
	        	field.setAttribute("name",tf.getName());
	        	field.setAttribute("units",tf.getUnits());
	        	field.setAttribute("class",tf.getClazz().getName());	        	
	        	top.appendChild(field);
	        }
	        doc.appendChild(top);
	        return doc;
    	} catch (Exception x) {
    		return null;
    	}
    }
    /*
    public boolean save(Writer writer) throws AmbitIOException {
		try {
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			writer.write("<template ");
			writer.write("name=\"");
			writer.write(getName());
			writer.write("\" >");
			for (int i=0; i < size(); i++ )
				getField(i).save(writer);
			writer.write("\n</template>");
			writer.flush();
			return true;
		} catch ( IOException  x) {
			throw new AmbitIOException(this.getClass().getName(),x);
		}
	}    
    public boolean save(OutputStream out) throws AmbitIOException {
			save(new OutputStreamWriter(out));
			return true;
	}
	        */

    
    public boolean load(InputStream in) throws AmbitIOException {
    	try {
    		return load(new InputStreamReader(in,"UTF-8"));
    	} catch (Exception x) {
    		throw new AmbitIOException(x);
    	}
    }
    public boolean load(Reader reader) throws AmbitIOException {	
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.parse(new InputSource(reader));     
	        Element e = doc.getDocumentElement();
	        if ("template".equals(e.getNodeName())) {
		        setName(e.getAttribute("name"));
		        NodeList nodes = e.getChildNodes();
		        for (int i=0; i < nodes.getLength();i++) {
		        	Node node = nodes.item(i);
		        	if (node instanceof Element) {
		        		Element element = (Element) node;
			        	String name = element.getNodeName();
			        	if ("field".equals(name)) {
		                    Property field = Property.getInstance(element.getAttribute("name").toString(),getName());
		                    field.setUnits(element.getAttribute("units").toString());
		                    String className=element.getAttribute("class").toString();
		                    try {
		                    	field.setClazz(Class.forName(className));
		                    } catch (Exception x) {
		                    	field.setClazz(String.class);
		                    }
		                    add(field);
			        	}
		        	}
		        }
		        return true;
	        } else return false;
        } catch (Exception x) {
        	throw new AmbitIOException(x);
        }

    }
 
	public String toString() {
		return getName();
	}

}
