/*
 * Created on 2006-2-18
 *
 */
package ambit2.io;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.openscience.cdk.io.setting.IOSetting;

import ambit2.data.descriptors.DescriptorsList;
import ambit2.data.molecule.MolProperties;

/**
 * An {@link org.openscience.cdk.io.setting.IOSetting} descendant to provide means for transforming property names. See
 * {@link ambit2.data.molecule.MolProperties} and {@link ambit2.io.AmbitSettingsListener}. See exapmle at {@link ambit2.database.writers.DbSubstanceWriter}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-2-18
 */
public class MolPropertiesIOSetting extends IOSetting {

    protected MolProperties properties;
    /**
     * @param name
     * @param level
     * @param question
     * @param defaultSetting
     */
    public MolPropertiesIOSetting(String name, int level, String question,
            String defaultSetting) {
        super(name, level, question, defaultSetting);
        Hashtable t = new Hashtable();
        t.put(name, name);
        properties = new MolProperties(t);
    }
    
    public MolPropertiesIOSetting(ArrayList names, int level, String question) {
        super("Columns", level, question, "");
        this.properties = new MolProperties(names);
    }
    
    public MolPropertiesIOSetting(Map properties, int level, String question) {
        super(question, level, question, "");
        this.properties = new MolProperties(properties);
    }
    public synchronized DescriptorsList getSelectedProperties() {
        return properties.getSelectedPropertiesList();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return properties.toString();
    }
    public int addProperties(Map properties) {
    	return this.properties.addProperties(properties);
    }
    /*
    public int addProperties(Hashtable properties) {
        int added = 0;
        Enumeration e = properties.keys();
        while (e.hasMoreElements()) {
            String name = e.nextElement().toString();
            boolean found  = false;
            for (int i=0;i<descriptors.size();i++) 
                if (descriptors.getDescriptorName(i).equals(name)) {
                    found = true;
                    break;
                }
            if (!found) {
                Descriptor d = (Descriptor) descriptors.createNewItem();
                d.setName(name);
                int idt = d.getTypeInModel().guessType(name,properties.get(name));
                if (idt >= 0) d.getTypeInModel().setId(idt);
                descriptors.addItem(d);
                added ++;
            }
        }
        if (added > 0) level = IOSetting.HIGH;
        else level = IOSetting.LOW;
        return added;
    }
    */

	public MolProperties getProperties() {
		return properties;
	}

	public void setProperties(MolProperties properties) {
		this.properties = properties;
	}
}
