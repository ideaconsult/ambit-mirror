/* PropertyTranslator.java
 * Author: Nina Jeliazkova
 * Date: Nov 6, 2006 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
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

package ambit.data.molecule;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.IDescriptor;

import ambit.data.IAmbitEditor;
import ambit.data.experiment.Experiment;
import ambit.data.experiment.ExperimentList;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.IdentifiersProcessor;

public class PropertyTranslator extends Observable {
	public static String type_identifiers = "identifiers";
	public static String type_descriptors = "descriptors";
	public static String type_results = "endpoint";
	public static String type_qsar = "QSAR";
    protected Hashtable<Object,Object> properties;
    protected Hashtable<Object ,Hashtable<Object,Object>> translatedProperties;
    protected Object selectedProperty;
    protected IdentifiersProcessor identifiersProcessor=null;
    /**
     * 
     */
    private static final long serialVersionUID = 846391114247273184L;

    public PropertyTranslator(Hashtable<Object,Object> properties) {
    	super();
    	this.properties = properties;
        translatedProperties = new Hashtable<Object, Hashtable<Object,Object>>();
        identifiersProcessor = new IdentifiersProcessor();
    }
    
    public PropertyTranslator() {
        this(new Hashtable<Object,Object>());
    }
    public PropertyTranslator(String[] types) {
        this();
        for (int i=0; i < types.length;i++)
            createType(types[i]);
    }
    public String[] getTypes() {
    	if (translatedProperties.size()==0) return null;
    	String[] types = new String[translatedProperties.size()];
    	Enumeration e = translatedProperties.keys();
    	int i=0;
    	while (e.hasMoreElements()) {
    		types[i] = e.nextElement().toString();
    		i++;
    	}	
    	return types;
    }
    public synchronized Hashtable<Object,Object>  getIdentifiers() {
        return getProperties(type_identifiers);
    }
    public synchronized void setIdentifiers(Hashtable<Object,Object> identifiers) {
        createType(type_identifiers,identifiers);
        setChanged();
        notifyObservers();
    }
    public synchronized Hashtable<Object,Object>  getDescriptorProperties() {
        return getProperties(type_descriptors);
    }
    public synchronized void setDescriptorProperties(Hashtable<Object,Object> descriptors) {
        createType(type_descriptors,descriptors);
        setChanged();
        notifyObservers();
    }
    public Hashtable<Object,Object> getProperties() {
        return properties;
    }    
    public Hashtable<Object,Object> getProperties(Object type) {
    	if (type==null) return properties;
        return translatedProperties.get(type);
    }
    public Object[] enumerateProperties(Object[]types, String defaultOption) {
        int n = 0;
        for (int i=0; i < types.length ; i++)
            if (getProperties(types[i]) != null) 
                n+=  getProperties(types[i]).values().size();
        n++;
        
        Object[] options = new Object[n];
        n = 1;
        for (int i=0; i < types.length ; i++)
            if (getProperties(types[i]) != null) {
                Iterator it = getProperties(types[i]).values().iterator();
                while (it.hasNext()) {
                    Object o = it.next();
                    options[n] = o.toString();
                    n++;
                }
            }    
        options[0] = defaultOption;
        return options;
    }   
    public void removeProperty(Object type,Object key) {
        Hashtable<Object, Object> p = getProperties(type);
        if (p==null) return;
        p.remove(key);
        setChanged();
        notifyObservers();        
    }    
    protected Hashtable<Object,Object> createType(Object type) {
    	if (type==null) return properties;
        Hashtable<Object, Object> p = new Hashtable<Object, Object>();
        translatedProperties.put(type, p);
        return p;
    }
    protected Hashtable<Object,Object> createType(Object type, Hashtable<Object,Object> table) {
    	if (type==null) return properties;
        translatedProperties.put(type, table);
        return table;
    }
    
    public void addProperty(Object key,Object value) {
    	properties.put(key, value);
    }
    public void addProperty(Object type,Object key,Object value) {
        if (type==null) properties.put(key, value);
        else {
            Hashtable<Object,Object> p = getProperties(type);
            if (p==null) { 
                p = createType(type);
            }    
            p.put(key, value);
        }
        setChanged();
        notifyObservers();
        
    }

    public Object getProperty(Object type,Object key) {
        Hashtable<Object, Object> p = getProperties(type);
        if (p==null) return null; 
        return p.get(key);
    }    
    public void moveBack(Object type,Object key) {
    	if (key == null) return;
        Hashtable<Object, Object> p = getProperties(type);
        if (p==null) return;
        Object value = p.get(key);
        if (value != null) {
	        p.remove(key);
	        properties.put(key,value);
	        setChanged();
	        notifyObservers();
        }
    }
    public void moveTo(Object type,Object key, Object value) {
    	if (key == null) return;
        properties.remove(key);
        addProperty(type, key, value);
        setChanged();
        notifyObservers();
    }        
    public void moveTo(Object type,Object key) {
    	if (key == null) return;
    	moveTo(type, key,processValue(type,key,properties.get(key)));
    }    
    /**
     * This is to process value when moving to the translated properties
     * @param type
     * @param key
     * @param value
     * @return
     */
    protected Object processValue(Object type,Object key, Object value) {
    	try {
    		double d = Double.parseDouble(value.toString());
    		//perhaps a descriptor
    		return key;
    	} catch (Exception x) {
    		Hashtable t = identifiersProcessor.getIdentifiers();
    		Object v = t.get(key);
    		if (v != null) return v.toString();
    	}
        return key;
    }
    public void moveTo(Object type) {
        Enumeration e = properties.keys();
        while (e.hasMoreElements()) {
            Object object = e.nextElement();
            moveTo(type, object, object);
        }            
    }
    public void guess(Object type) {
    	guess(type,null);
    }
    /*
			    	if ((key instanceof String)	&& 
					(!key.equals(AmbitCONSTANTS.AMBIT_IDSTRUCTURE)) 
					&& (!key.equals(AmbitCONSTANTS.AMBIT_IDSUBSTANCE))
					&& (!key.equals("CRAMERFLAGS"))
					&& (!key.equals(CDKConstants.ALL_RINGS))
					&& (!key.equals(CDKConstants.SMALLEST_RINGS)))
			    		translator.addProperty(PropertyTranslator.type_identifiers,key,key);
			    	else if (key instanceof DescriptorSpecification) { 
						 String s =((DescriptorSpecification) key).getImplementationTitle();
						 translator.addProperty(PropertyTranslator.type_descriptors,s,s);
					 } else if (key instanceof IDescriptor) {
						 String s =((DescriptorSpecification)((IDescriptor) key).getSpecification()).getImplementationTitle();
						 translator.addProperty(PropertyTranslator.type_descriptors,s,s);
					 } else
						 translator.addProperty(PropertyTranslator.type_descriptors,key,key);
			    		
     */
    public void guess(Object type, PropertyTranslator dictionary) {
    	if (type ==null) return;
    	else if (type.equals(type_descriptors)) {
            Enumeration e = properties.keys();
            while (e.hasMoreElements()) {
                Object key = e.nextElement();
                if (dictionary != null) {
                	Object newname = dictionary.getProperty(type,key);
                	if (newname != null) {
                		moveTo(type, key, newname);
                		continue;
                	}	
                }
        		try {
        			if ((key instanceof DescriptorSpecification) || (key instanceof IDescriptor)) { 
						 moveTo(type,key,key);
					} else {
	        		    Double.parseDouble(properties.get(key).toString());
	                    moveTo(type, key, key);
					}
        		} catch (Exception x) {
                    
        		}
            }    
    	} else if (type.equals(type_identifiers)) {
    		Enumeration e = properties.keys();
            while (e.hasMoreElements()) {
                Object object = e.nextElement();
                String key = object.toString().toUpperCase();
                if (dictionary != null) {
                	Object newname = dictionary.getProperty(type,object);
                	if (newname != null) {
                		moveTo(type, object, newname);
                		continue;
                	}	
                }
                if (key.indexOf("CAS") >=0) 
                    moveTo(type, object, CDKConstants.CASRN);
                else if (key.indexOf("NAME")>=0)
                    moveTo(type,object,CDKConstants.NAMES);
                else if (key.indexOf("SMILES")>=0)
                    moveTo(type, object, AmbitCONSTANTS.SMILES);
            }
    	} else if (type.equals(type_results))  
    		if (dictionary != null) {
	    		Enumeration e = properties.keys();
	            while (e.hasMoreElements()) {
	                Object object = e.nextElement();
		        	Object newname = dictionary.getProperty(type,object);
		        	if (newname != null) {
		        		moveTo(type, object, newname);
		        	}
	            }
    		}

    }
    public static String guessType(Object key) {
		if ((key.equals(AmbitCONSTANTS.AMBIT_IDSTRUCTURE)) 
				|| (key.equals(AmbitCONSTANTS.AMBIT_IDSUBSTANCE))
				|| (key.equals("CRAMERFLAGS"))
				|| (key.equals(CDKConstants.ALL_RINGS))
				|| (key.equals(CDKConstants.SMALLEST_RINGS))) return null;
    	
		if (AmbitCONSTANTS.AQUIRE.equals(key))
			return PropertyTranslator.type_results;		
    	if (key instanceof String)
		    return type_identifiers;
		else if (key instanceof DescriptorSpecification) { 
			 return PropertyTranslator.type_descriptors;
		} else if (key instanceof IDescriptor) {
			 String s =((DescriptorSpecification)((IDescriptor) key).getSpecification()).getImplementationTitle();
			 return PropertyTranslator.type_descriptors;
		} else if (key instanceof Experiment) {
			return PropertyTranslator.type_results;
		} else if (key instanceof ExperimentList) {
			return PropertyTranslator.type_results;
		} else
			 return null;
    }
    
    public void  guessAndAdd(Object key) {
    	if (key == null) return;
    	String uf = key.toString();
    	String type = null;
		if ((key.equals(AmbitCONSTANTS.AMBIT_IDSTRUCTURE)) 
		|| (key.equals(AmbitCONSTANTS.AMBIT_IDSUBSTANCE))
		|| (key.equals("CRAMERFLAGS"))
		|| (key.equals(CDKConstants.ALL_RINGS))
		|| (key.equals(CDKConstants.SMALLEST_RINGS))) return;
    	
		if (AmbitCONSTANTS.AQUIRE.equals(key))
			type = PropertyTranslator.type_results;
		else if (key instanceof String)
		    type = type_identifiers;
		else if (key instanceof DescriptorSpecification) {
			 uf =((DescriptorSpecification) key).getImplementationTitle();
			 type =  PropertyTranslator.type_descriptors;
		} else if (key instanceof IDescriptor) {
			 String s =((DescriptorSpecification)((IDescriptor) key).getSpecification()).getImplementationTitle();
			 uf = s.substring(s.lastIndexOf('.')+1);
			 type = PropertyTranslator.type_descriptors;
		} else if (key instanceof Experiment) {
			uf = ((Experiment)key).getStudy().toString();
			type = PropertyTranslator.type_results;
		} else if (key instanceof ExperimentList) {
			type = PropertyTranslator.type_results;
		} 
    	if (type != null)
    		addProperty(type,uf,key);	
    }

    
	public Object getSelectedProperty() {
		return selectedProperty;
	}
	public void setSelectedProperty(Object selectedProperty) {
		this.selectedProperty = selectedProperty;
	}
	public IdentifiersProcessor getIdentifiersProcessor() {
		return identifiersProcessor;
	}
	public void setIdentifiersProcessor(IdentifiersProcessor identifiersProcessor) {
		this.identifiersProcessor = identifiersProcessor;
	}
    public void clear() {
        properties.clear();
        Enumeration keys = translatedProperties.keys();
        while (keys.hasMoreElements())
            translatedProperties.get(keys.nextElement()).clear();
    }
    public void add(PropertyTranslator translator) {
        properties.putAll(translator.getProperties());
        Enumeration keys = translator.translatedProperties.keys();
        while (keys.hasMoreElements()) {
            Object type = keys.nextElement();
            Hashtable t = getProperties(type);
            if (t==null) t = createType(type);
            t.putAll(translator.translatedProperties.get(type));
        }    
    }
    @Override
    public String toString() {
        return properties.toString() + "\n" + translatedProperties.toString();
    }
    public boolean isEmpty() {
        int size = properties.size();
        Enumeration keys = translatedProperties.keys();
        while (keys.hasMoreElements()) {
            Object type = keys.nextElement();
            Hashtable t = getProperties(type);
            if (t !=null) size += t.size();
        }    
        return size==0;

    }
    
    public void translate(PropertyTranslator dictionary) {
    	if (dictionary == null) return;
        String[] types =  dictionary.getTypes();
        for (int i=0; i < types.length; i++)
        	guess(types[i],dictionary);
    }
    
    
}
/*
class PropertyPairComparator implements Comparator<PropertyPair> {
    public static final int modeOriginalName=0;
    public static final int modeNewName=1;
    protected int mode;
    public PropertyPairComparator(int mode) {
        super();
        setMode(mode);
    }
    public int compare(PropertyPair o1, PropertyPair o2) {
        switch (mode) {
        case modeOriginalName: {return  o1.originalName.compareTo(o2.originalName);}
        case modeNewName: {return  o1.newName.compareTo(o2.newName);}
        }
        return 0;
    }
    public synchronized int getMode() {
        return mode;
    }
    public synchronized void setMode(int mode) {
        switch (mode) {
        case modeOriginalName: { this.mode = modeOriginalName; return;}
        case modeNewName: { this.mode = modeNewName;return;}
        default: { this.mode = modeOriginalName;return;}
        }
    }
}

class PropertyPair  {
    protected String originalName;
    protected String newName;
    public PropertyPair(String name) {
        this(name,name);
    }
    public PropertyPair(String name, String newName) {
        this.originalName = name;
        this.newName = newName;
    }
    public synchronized String getNewName() {
        return newName;
    }
    public synchronized void setNewName(String newName) {
        this.newName = newName;
    }
    public synchronized String getOriginalName() {
        return originalName;
    }
    public synchronized void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PropertyPair) {
            PropertyPair p = (PropertyPair) obj;
            return p.newName.equals(newName) && p.originalName.equals(originalName);
        } else return false;
    }
}
*/