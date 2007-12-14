package ambit.data.molecule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit.data.descriptors.AmbitColumnType;
import ambit.data.descriptors.Descriptor;
import ambit.data.descriptors.DescriptorDefinition;
import ambit.data.descriptors.DescriptorsHashtable;
import ambit.data.descriptors.DescriptorsList;
import ambit.data.experiment.DefaultTemplate;
import ambit.data.experiment.Study;
import ambit.data.experiment.StudyTemplate;
import ambit.data.experiment.TemplateField;
import ambit.data.literature.ReferenceFactory;
import ambit.data.model.Model;
import ambit.exceptions.AmbitException;
import ambit.exceptions.PropertyNotInTemplateException;
import ambit.io.IColumnTypeSelection;

/**
 * 
 * This class provides means for configuring correspondence between molecule properties names found in a file 
 * and to what type and names these properties should be transformed for further processing.<br>
 * For example a moleucle may have property "CAS#"="50-00-0" , but it should be transformed to "CAS_RN"="50-00-0". 
 * In this case the identifiers Hashtable will have the pair ("CAS#","CAS_RN")<br>
 * A bit more complex case if a property "XLogP"="2.5" is to be transformed to {@link org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor}.
 * Then the descriptors hashtable will store the pair ("XLogP",{@link org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor})<br>
 * Another example is the transformation of experimental data. 
 * A property "LC50" would be transformed to a {@link ambit.data.experiment.TemplateField} with name "LC50" from a template {@link ambit.data.experiment.DSSToxLC50Template}
 * and the experimental Hashtable will store the pair ("LC50",template.getField("LC50")).<br>
 * The template can be acessed by {@link #getStudy()} {@link ambit.data.experiment.Study#getTemplate()}.<br>
 * The class also has a slot to store information of a QSAR model {@link ambit.data.model.Model} in case
 * the data imported is a QSAR model.<br>
 * All the processing is done automatically (asking the user if necessary) if a {@link ambit.io.AmbitSettingsListener} is assigned to {@link org.openscience.cdk.io.IChemObjectReader}.<br>
 * Used to hold data for {@link ambit.io.MolPropertiesIOSetting}. <br>
 * See {@link ambit.data.molecule.MolPropertiesFactory} for examples. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class MolProperties extends PropertyTranslator implements Serializable {
	protected transient Object selectedProperty = null;
	protected Hashtable identifiers;
	protected DescriptorsHashtable descriptors;
	protected Hashtable experimental;
	protected Hashtable qsar;
	protected Study study = new Study("Default",new DefaultTemplate("Default"));
	protected Model qsarModel = new Model();
	
	public MolProperties() {
		this(new Hashtable());
	}	
	public MolProperties(Hashtable properties) {
		super();
		this.properties = new Hashtable(properties);
		identifiers = new Hashtable();
		createType(PropertyTranslator.type_identifiers, identifiers);
		descriptors = new DescriptorsHashtable();
		createType(PropertyTranslator.type_descriptors, descriptors);
		experimental = new Hashtable();
		createType(PropertyTranslator.type_results, experimental);
		qsar = new Hashtable();
		createType(PropertyTranslator.type_qsar, qsar);
		qsarModel = new Model();
		qsarModel.setReference(ReferenceFactory.createEmptyReference());
		//qsarModel.setStudy(new Study("Default",template));
		qsarModel.setDescriptors(new DescriptorsList());
		
	}
	public MolProperties(ArrayList names) {
		this(new Hashtable());
		for (int i =0; i < names.size();i++)
			properties.put(names.get(i),"");
	}	
	public int addProperties(Hashtable properties) {
		int i = 0;
		Enumeration e = properties.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			if ((identifiers.get(key) == null) && 
				(descriptors.get(key) == null) &&
				(experimental.get(key) == null) &&
				(qsar.get(key) == null) &&
				(this.properties.get(key) == null)) {
				this.properties.put(key, properties.get(key));
				i++;
			}	
		}
		return i;
	}
	public void addIdentifier(String name,IColumnTypeSelection type) {
	    identifiers.put(name,type);
	}
	public void addQSAR(String name,IColumnTypeSelection type) {
	    qsar.put(name,type);
	}
		
	public void addDescriptor(String name,DescriptorDefinition descriptor) {
	    identifiers.put(name,descriptor);
	}	
	public void moveBack(Object key,Hashtable collection) {
		if (key == null) return;
		properties.put(key, collection.get(key));
		collection.remove(key);
	}
	public void moveTo(Object key,Hashtable collection, Object newType) {
	    if (key == null) return;
		properties.remove(key);
		collection.put(key, newType);
	}
	
	public AmbitColumnType moveToIdentifiers(Object key) {
	    if (key == null) return null;
		properties.remove(key);
		AmbitColumnType acType = new AmbitColumnType(AmbitColumnType._ctUnknown);
		int id = acType.guessType(key.toString());
		if (id >=0)
		acType.setId(id);
		identifiers.put(key,acType);
		return acType;
	}
	public AmbitColumnType moveToQSAR(Object key) {
	    if (key == null) return null;
		properties.remove(key);
		AmbitColumnType acType = new AmbitColumnType(AmbitColumnType._ctUnknown);
		int id = acType.guessType(key.toString());
		if (id >=0)
		    acType.setId(id);
		qsar.put(key,acType);
		return acType;
	}	
	public DescriptorDefinition moveToDescriptors(Object key) {
	    if (key == null) return null;
		Descriptor d = new Descriptor(key.toString(),ReferenceFactory.createEmptyReference());
		int id = d.getTypeInModel().guessType(key.toString());
		if (id >= 0) d.getTypeInModel().setId(id);
		properties.remove(key);
		descriptors.addDescriptorPair(key, d);	
		return d;
	}		
	public void moveToExperimental(Object key) throws AmbitException {
	    if (key == null) return;
	    if (key.equals("")) return;
		if (getTemplate() == null) throw new AmbitException("Template not defined!");
		TemplateField field = getTemplate().getField(key);
		if (field == null) throw new PropertyNotInTemplateException(key,getTemplate());
		else {
			properties.remove(key);
			experimental.put(key, field);
		}
	}
	public void moveToExperimental(Object key, TemplateField field) {
		properties.remove(key);
		experimental.put(key,field);
	}
	public void guessExperiments() throws AmbitException {
		Enumeration e = properties.keys();
		while (e.hasMoreElements()) 
			moveToExperimental(e.nextElement());
		
	}
	public void guessIdentifiers() {
		AmbitColumnType acType = new AmbitColumnType(AmbitColumnType._ctUnknown);
		Enumeration e = properties.keys();
		while (e.hasMoreElements()) 
			try  {
				Object key = e.nextElement();
				int id = acType.guessType(key.toString());
				switch (id) {
				case AmbitColumnType._ctCAS:{  moveToIdentifiers(key); break;}
				case AmbitColumnType._ctChemName: {  moveToIdentifiers(key); break;}
				case AmbitColumnType._ctRowID:{  moveToIdentifiers(key); break;}
				case AmbitColumnType._ctSMILES:{  moveToIdentifiers(key); break;}
				default: {
 
				}
				}
			} catch (Exception x) {
			}	
	}
	public void guessQSAR() {
		AmbitColumnType acType = new AmbitColumnType(AmbitColumnType._ctUnknown);
		Enumeration e = properties.keys();
		while (e.hasMoreElements()) 
			try  {
				Object key = e.nextElement();
				int id = acType.guessType(key.toString());
				switch (id) {
				case IColumnTypeSelection._ctEquation: {moveToQSAR(key); break;}
				case IColumnTypeSelection._ctYpredicted:{moveToQSAR(key); break;}
				//case IColumnTypeSelection._ctYobserved:{moveToQSAR(key); break;}
				//case IColumnTypeSelection._ctYresidual:{moveToQSAR(key); break;}
				default: id = -1;
				}
			} catch (Exception x) {
			}	
	}	

	public void guessDescriptors() {
		AmbitColumnType acType = new AmbitColumnType(AmbitColumnType._ctUnknown);
		Enumeration e = properties.keys();
		while (e.hasMoreElements()) 
			try  {
				Object key = e.nextElement();
				//check if in the template
				TemplateField field = getTemplate().getField(key);
				if (field != null) continue;
				Object value = properties.get(key);
				if (value instanceof String)
					try {
						Double.parseDouble(value.toString());
						moveToDescriptors(key);	
					} catch (Exception x) {
						
					}
				else if (value instanceof Number) {
					moveToDescriptors(key);	
				} else if (value instanceof DescriptorDefinition) {
					moveToDescriptors(key);
				} else if (value instanceof IDescriptorResult) {
					moveToDescriptors(key);
				}
			} catch (Exception x) {
			}	
	}
	
	public StudyTemplate getTemplate() {
		return study.getTemplate();
	}
	public void setTemplate(StudyTemplate template) {
		this.study.setTemplate(template);
		this.study.setName(template.getName());
	}			
	public int getIdentifier(Object key) {
		return ((Integer)identifiers.get(key)).intValue();
	}
	public DescriptorDefinition getDescriptor(Object key) {
		return descriptors.getAmbitDescriptor(key);
	}
	public TemplateField getExperimental(Object key) {
		return (TemplateField)experimental.get(key);
	}		
    public synchronized DescriptorsHashtable getDescriptors() {
        return descriptors;
    }
    public synchronized void setDescriptors(DescriptorsHashtable descriptors) {
        this.descriptors = descriptors;
        setChanged();
        notifyObservers();
    }
    public synchronized Hashtable getExperimental() {
        return experimental;
    }
    public synchronized void setExperimental(Hashtable experimental) {
        this.experimental = experimental;
        setChanged();
        notifyObservers();
    }    
    public synchronized Hashtable getQSAR() {
        return qsar;
    }    
    public synchronized void setQSAR(Hashtable qsar) {
        this.qsar = qsar;
        setChanged();
        notifyObservers();
    }
    public synchronized Hashtable getIdentifiers() {
        return identifiers;
    }
    public synchronized void setIdentifiers(Hashtable identifiers) {
        this.identifiers = identifiers;
        setChanged();
        notifyObservers();
    }
    public synchronized Hashtable getProperties() {
        return properties;
    }
    public synchronized void setProperties(Hashtable properties) {
        this.properties = properties;
        setChanged();
        notifyObservers();
    }
	public DescriptorsList getSelectedPropertiesList() {
		DescriptorsList list = new DescriptorsList();
		Enumeration e = identifiers.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			Object value = identifiers.get(key);
			if (value instanceof DescriptorDefinition)
			    list.addItem((DescriptorDefinition) value);
			else    
			list.addItem(
					new Descriptor(key.toString(),2,((AmbitColumnType) value).getId(),ReferenceFactory.createEmptyReference())
					);
		}
		e = descriptors.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			Object value = descriptors.get(key);
			list.addItem((Descriptor) value);
		}
		e = qsar.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			Object value = qsar.get(key);
			list.addItem(
					new Descriptor(key.toString(),2,((AmbitColumnType) value).getId(),ReferenceFactory.createEmptyReference())
					);
		}
		
		e = experimental.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			Object value = experimental.get(key);
			list.addItem(
					new Descriptor(key.toString(),2,AmbitColumnType._ctYobserved,ReferenceFactory.createEmptyReference())
					);
		}		
		return list;
		
	}
	public DescriptorsList getDescriptorsList() {
		DescriptorsList list = new DescriptorsList();
		Enumeration e = descriptors.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			Object value = descriptors.get(key);
			if (value instanceof Descriptor)
			    list.addItem((Descriptor) value);
			else {
			    //TODO pop up window for entering descriptors details
			    list.addItem(new Descriptor(value.toString(),ReferenceFactory.createEmptyReference()));
			}
		}
		return list;
	}
	
	public void assign(IChemObject object) {
		Hashtable p = object.getProperties();
		if (p==null) return;
		Enumeration e = p.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			Object value = p.get(key);
			
			Object newKey = isIdentifier(key);
			if (newKey != null) {
				p.remove(key);
				if (((AmbitColumnType)newKey).getId() ==AmbitColumnType._ctUnknown) 
					newKey = key;
				p.put(newKey.toString(), value);
			} else {
				newKey = isDescriptor(key);
				if (newKey != null) {
					p.remove(key);
					p.put(newKey, value);					
				} else {
					newKey = isTemplateField(key);
					if (newKey != null) {
						p.remove(key);
						p.put(newKey, value);					
					}	
				}
			}
			
		}
	}
	protected AmbitColumnType isIdentifier(Object key) {
		Object o = identifiers.get(key);
		if (o == null) return null;
		else return (AmbitColumnType) o;
	}
	protected DescriptorDefinition isDescriptor(Object key) {
		Object o = descriptors.get(key);
		if (o == null) return null;
		else return (DescriptorDefinition) o;
	}	
	protected TemplateField isTemplateField(Object key) {
		Object o = experimental.get(key);
		if (o == null) return null;
		else return (TemplateField) o;
	}
    public synchronized Model getQsarModel() {
        return qsarModel;
    }
    public synchronized void setQsarModel(Model qsarModel) {
        this.qsarModel = qsarModel;
		setChanged();
		notifyObservers();
    }
	public Study getStudy() {
		return study;
	}
	public void setStudy(Study study) {
		this.study = study;
		setChanged();
		notifyObservers();
	}
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("Properties\n");
    	b.append(properties.toString());
    	b.append('\n');
    	b.append("Identifiers\n");
    	b.append(identifiers);
    	b.append('\n');
    	b.append("Descriptors\n");
    	b.append(descriptors);
    	b.append('\n');
    	b.append("Experiments\n");
    	b.append(experimental);
    	b.append('\n');
    	b.append("QSAR\n");
    	b.append(qsar);
    	b.append('\n');
    	b.append("Study\n");
    	b.append(study);
    	b.append('\n');
    	b.append("QSAR model\n");
    	b.append(qsarModel);
    	return b.toString();
    }
}
