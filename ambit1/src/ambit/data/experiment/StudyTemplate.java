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

package ambit.data.experiment;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Hashtable;

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

import ambit.data.AmbitList;
import ambit.data.AmbitObject;
import ambit.data.IAmbitEditor;
import ambit.exceptions.AmbitIOException;
import ambit.ui.data.experiment.StudyTemplateEditor;

/**
 * Study templates are introduced in order to provide generic framework for storing experimental data results, without predefined fieldnames, which may not be relevant to all endpoints (e.g. species strain or vehicle is not relevant to all species or endpoints). On other hand, fields with the same meaning have to have the same name to make queries feasible (e.g. “species” field should be the same across all endpoints in order to be able to search for species). <br>
 * A template consists of a name, fields defining study conditions and fields defining study results. Each field has a name, units and a flag specifying whether it is a condition or a result. See {@link ambit.data.experiment.DefaultTemplate} ,
 * {@link ambit.data.experiment.DSSToxCarcinogenicityTemplate}, {@link ambit.data.experiment.DSSToxERBindingTemplate},
 * {@link ambit.data.experiment.DSSToxLC50Template} for specific examples. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-2
 */
public class StudyTemplate extends AmbitList {
	protected String source = "";
    protected TemplateField query = null;
    /**
     * 
     */
    public StudyTemplate(String name) {
        super();
        setName(name);
    }

    /**
     * @param initialCapacity
     */
    public StudyTemplate(int initialCapacity) {
        super(initialCapacity);

    }

    /**
     * @param c
     */
    public StudyTemplate(Collection c) {
        super(c);

    }
    public void addField(TemplateField field) {
        super.addItem(field);
    }
    public TemplateField getField(int index) {
        return (TemplateField) getItem(index);
    }
    public void addFields(String fieldName,String units, boolean numeric,boolean isResult) {
        addField(new TemplateField(fieldName,units,numeric,isResult));
    }
    public TemplateField getField(Object field) {
        if (field instanceof TemplateField) {
            int i = indexOf(field);
            if (i == -1) return null; else return getField(i);
        }
        else {
            if (query == null) query = new TemplateField();
            query.setName(field.toString());
            int i = indexOf(query);
            if (i == -1) return null; else return getField(i);
        }
    }
    /* (non-Javadoc)
     * @see ambit.data.AmbitList#clone()
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
	        setNotModified();			
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
	        for (int i=0; i < size(); i++) {
	        	Element field = doc.createElement("field");
	        	TemplateField tf = (TemplateField)getItem(i);
	        	field.setAttribute("name",tf.getName());
	        	field.setAttribute("units",tf.getUnits());
	        	
	        	if (tf.isNumeric()) 
	        		field.setAttribute("numeric","True");
	        	else
	        		field.setAttribute("numeric","False");
	        	
	        	if (tf.isResult()) 
	        		field.setAttribute("result","True");
	        	else
	        		field.setAttribute("result","False");	        	
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
		                    TemplateField field = new TemplateField(element.getAttribute("name").toString());
		                    field.setUnits(element.getAttribute("units").toString());
		                    String numeric=element.getAttribute("numeric").toString();
		                    field.setNumeric(numeric.toLowerCase().equals("true"));
		                    String result=element.getAttribute("result").toString();
		                    field.setResult(result.toLowerCase().equals("true"));
		                    addField(field);
			        	}
		        	}
		        }
		        return true;
	        } else return false;
        } catch (Exception x) {
        	throw new AmbitIOException(x);
        }

    }
    /*
	public boolean load(InputStream in) throws AmbitIOException {
		try {
		    DataInputStream data = new DataInputStream(in);
		    XMLReader parser = MyIOUtilities.initParser();
            parser.setFeature("http://xml.org/sax/features/validation", false);
            parser.setContentHandler(new DefaultHandler() {
                
                public void startElement(String uri, String localName,
                        String qName, Attributes attributes) throws SAXException {
                    super.startElement(uri, localName, qName, attributes);
                    if (localName.equals("template")) {
                        String key = attributes.getValue("name");
                        setName(key);
                    } else if (localName.equals("field")) {
                        TemplateField field = new TemplateField(attributes.getValue("name").toString());
                        field.setUnits(attributes.getValue("units").toString());
                        String numeric=attributes.getValue("numeric").toString();
                        field.setNumeric(numeric.toLowerCase().equals("true"));
                        String result=attributes.getValue("result").toString();
                        field.setResult(result.toLowerCase().equals("true"));
                        addField(field);
                    }

                }
            });
            parser.parse(new InputSource(in));
            parser = null;
            return true;
		} catch (SAXException x) {
			throw new AmbitIOException(x);
		} catch (IOException x) {
			throw new AmbitIOException(x);
		}
	}
	*/
	public String toString() {
		return getName();
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	public void getStudyConditions(Hashtable properties) {
		for (int i=0; i < size(); i++ ) {
			if (!getField(i).isResult) {
			    TemplateField f = getField(i);
			    if (f.getName().equals(DefaultTemplate.endpoint))
			        properties.put(f,getName());
			    else    
			        properties.put(f,"");
			}
		}
	}
	public void getStudyResults(Hashtable properties) {
		for (int i=0; i < size(); i++ )
			if (getField(i).isResult) properties.put(getField(i),"");
	}	
	@Override
	public IAmbitEditor editor(boolean editable) {
		return new StudyTemplateEditor(this,editable,"");
	}
	@Override
	public AmbitObject createNewItem() {
		return new TemplateField();
	}
}
