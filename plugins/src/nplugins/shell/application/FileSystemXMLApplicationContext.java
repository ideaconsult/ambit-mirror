/* FileSystemXMLApplicationContext.java
 * Author: Nina Jeliazkova
 * Date: Jul 5, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
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

package nplugins.shell.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nplugins.core.Introspection;
import nplugins.core.NPluginsException;
import nplugins.shell.INPApplicationContext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class FileSystemXMLApplicationContext implements INPApplicationContext {
    protected final String tag_bean = "bean";
    protected final String tag_ref = "ref";
    protected final String tag_property = "property";
    protected final String tag_value = "value";
    protected final String tag_map = "map";
    protected final String tag_entry = "entry";
    
    protected final String attr_id = "id";
    protected final String attr_class = "class";
    protected final String attr_name = "name";
    protected final String attr_local = "local";

    protected TaskMonitor monitor;
    protected Document context;
    protected Map<String,Element> lookup;
    protected Logger logger = Logger.getLogger("nplugins.shell.application.FileSystemXMLApplicationContext");
    public FileSystemXMLApplicationContext(Class clazz) throws NPluginsException, FileNotFoundException  {
        this(new InputSource(new FileReader(new File(clazz.getName()+".xml"))));
    }
    public FileSystemXMLApplicationContext(InputSource source) throws NPluginsException  {
        monitor = new TaskMonitor();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            context = builder.parse(source);
            context.normalize();
            lookup = buildLookup(context);
        } catch (Exception x) {
            throw new NPluginsException(x);
        }
        
    }
    public FileSystemXMLApplicationContext(String url) throws NPluginsException  {
        monitor = new TaskMonitor();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            context = builder.parse(url);
            context.normalize();
            lookup = buildLookup(context);
        } catch (Exception x) {
            throw new NPluginsException(x);
        }
        
        
    }
    protected Map<String,Element> buildLookup(Document doc) {
        Map<String,Element> lookup = new Hashtable<String,Element>();
        Element root = doc.getDocumentElement();
        NodeList nodes = root.getElementsByTagName(tag_bean);
        for (int i=0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) 
                try {
                lookup.put(((Element)n).getAttribute(attr_id), (Element) n);
                } catch (Exception x) {
                    
                }
        }
        nodes = null;
        
        return lookup;
    }
    public Object getBean(String id) throws NPluginsException {
        Element element = lookup.get(id);
        if (element == null) throw new NPluginsException("No element with id "+id);
        return getBean(element);        
    }
    protected Object getBean(Element element) throws NPluginsException {
        try {
            Class clazz = Introspection.getClassDefinition(element.getAttribute(attr_class));
            Object object = clazz.newInstance();
            
            NodeList properties = element.getElementsByTagName(tag_property);
            for (int i=0; i < properties.getLength();i++) {
                String name = ((Element)properties.item(i)).getAttribute(attr_name);
                //reference to objects
                NodeList refs = ((Element)properties.item(i)).getElementsByTagName(tag_ref);
                
                for (int j=0; j < refs.getLength();j++) {
                	Object ref = getBean(((Element)refs.item(j)).getAttribute(attr_local));
                	invokeSetMethod(object, name, ref);
        	
                }
            	//values
                NodeList values = ((Element)properties.item(i)).getElementsByTagName(tag_value);
                for (int j=0; j < values.getLength();j++) {
                	invokeSetMethod(object, name, ((Element)values.item(j)).getTextContent());
                }
                //TODO collection injection
                /*
            	//map
                NodeList map = ((Element)properties.item(i)).getElementsByTagName(tag_map);
                for (int j=0; j < map.getLength();j++) {
                	NodeList entries = ((Element)properties.item(i)).getElementsByTagName(tag_entry);
                	for (int k=0; k < entries.getLength();k++) {
                		String key = ((Element)entries.item(k)).getAttribute(attr_key);
                		invokePutMethod(object, key, ((Element)values.item(j)).getTextContent());
                	}
                } 
                */               
            }
            return object;
        } catch (ClassNotFoundException x) {
            throw new NPluginsException(x);
        } catch (IllegalAccessException x) {
            throw new NPluginsException(x);
        } catch (InstantiationException x) {
            throw new NPluginsException(x);
        } catch (InvocationTargetException x) {
            throw new NPluginsException(x);
        } catch (NoSuchMethodException x) {
            throw new NPluginsException(x);
        }
    }    

    protected void invokePutMethod(Object object, Object key, Object param) throws NoSuchMethodException, 
			 InvocationTargetException, 
			 IllegalAccessException{
		Class clazz = param.getClass();
		Class[] parameterType = new Class[2];
		Object[] value={key,param};
		
		while (clazz != null)
			try {
				parameterType[0] = key.getClass();
				parameterType[1] = clazz;
				Method putMethod = object.getClass().getMethod("put", parameterType);
				
				putMethod.invoke(object,value);
				return;
			} catch (NoSuchMethodException x) {
				logger.warning(x.getMessage());
				clazz = clazz.getSuperclass();
			}
		
		Class[] ifs = param.getClass().getInterfaces();
		for (Class f : ifs) 
			try {
				parameterType[0] = key.getClass();
				parameterType[1] = f;
				Method putMethod = object.getClass().getMethod("put", parameterType);
				putMethod.invoke(object,value);
				return;
			} catch (NoSuchMethodException x) {
				logger.warning(x.getMessage());
			} 
}


    protected void invokeSetMethod(Object object, String field, Object param) throws NoSuchMethodException, 
    																				 InvocationTargetException, 
    																				 IllegalAccessException{
    	Class clazz = param.getClass();
        Class[] parameterType = new Class[1];
        Object[] value={param};
        
        while (clazz != null)
        try {
            parameterType[0] = clazz;
            Method setMethod = object.getClass().getMethod("set"+field, parameterType);
                    	
        	setMethod.invoke(object,value);
        	return;
        } catch (NoSuchMethodException x) {
        	logger.warning(x.getMessage());
        	clazz = clazz.getSuperclass();
        }
        
        Class[] ifs = param.getClass().getInterfaces();
        for (Class f : ifs) 
	        try {
	            parameterType[0] = f;
	            Method setMethod = object.getClass().getMethod("set"+field, parameterType);
	        	setMethod.invoke(object,value);
	        	return;
	        } catch (NoSuchMethodException x) {
	        	logger.warning(x.getMessage());
	        } 
    }

    
    
    public TaskMonitor getTaskMonitor() {
        return monitor;
    }

}
