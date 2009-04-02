/*
 * Created on 2006-2-18
 *
 */
package ambit2.core.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.io.setting.IOSetting;

import ambit2.base.data.Property;

/**
 * An {@link org.openscience.cdk.io.setting.IOSetting} descendant to provide means for transforming property names. See
 * {@link ambit2.base.data.Property} and {@link ambit2.ui.AmbitSettingsListener}. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-2-18
 */
public class MolPropertiesIOSetting extends IOSetting {

    protected List<Property> properties;
    /**
     * @param name
     * @param level
     * @param question
     * @param defaultSetting
     */
    public MolPropertiesIOSetting(String name, int level, String question,
            String defaultSetting) {
        super(name, level, question, defaultSetting);
        properties = new ArrayList<Property>();
    }
    
    public MolPropertiesIOSetting(ArrayList names, int level, String question) {
        this("Columns", level, question, "");
    }

    
    public MolPropertiesIOSetting(Map properties, int level, String question) {
        this(question, level, question, "");
        addProperties(properties);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return properties.toString();
    }
    public void addProperties(Map newProperties) {
    	Iterator e = newProperties.keySet().iterator();
    	while (e.hasNext()) {
    		properties.add(Property.getInstance(e.next().toString(),"I/O"));
    	}
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

}
