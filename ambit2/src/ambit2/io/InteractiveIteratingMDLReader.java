/*
 * Created on 2006-2-18
 *
 */
package ambit2.io;

import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.io.setting.IOSetting;

/**
 * A descendant of {@link IteratingMDLReader} able to ask for type of each SDF property
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-2-18
 */
public class InteractiveIteratingMDLReader extends IteratingMDLReader {
    protected IOSetting[] headerOptions = null;
    /**
     * @param in
     */
    public InteractiveIteratingMDLReader(Reader in,IChemObjectBuilder builder) {
        super(in,builder);

    }

    /**
     * @param in
     */
    public InteractiveIteratingMDLReader(InputStream in, IChemObjectBuilder builder) {
        super(in,builder);

    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.iterator.IteratingMDLReader#next()
     */
    public Object next() {
        Object o =  super.next();
        if (o instanceof IChemObject) {
            
            headerOptions = setHeaderOptions(((IChemObject)o).getProperties());
	        for (int i=0; i < headerOptions.length;i++) {
	            fireIOSettingQuestion(headerOptions[i]);
	            //if (headerOptions[i] instanceof MolPropertiesIOSetting) {
	            //	((MolPropertiesIOSetting) headerOptions[i]).getProperties().assign((ChemObject)o);
	            //}
	        }    
	        
        }
        return o;
    }
    protected IOSetting[] setHeaderOptions(Map properties) {
        if (headerOptions == null) {
	        IOSetting[] ios = new IOSetting[1];
	        ios[0] = new MolPropertiesIOSetting(properties,IOSetting.HIGH,"Specify column types");
	        return ios;
        } else {
            ((MolPropertiesIOSetting) headerOptions[0]).addProperties(properties);
            return headerOptions;
        }
    }
    /*
	protected IOSetting[] setHeaderOptions(Hashtable properties) {
	    Vector commandOptions = new Vector();
	    IOSetting[] options = new IOSetting[properties.size()];
	    for (int i=0; i < IColumnTypeSelection._columnTypesS.length;i++)
	        commandOptions.add(IColumnTypeSelection._columnTypesS[i]);
	    
	    Enumeration e = properties.keys();
	    int i = 0;
	    while (e.hasMoreElements()) {
	        options[i] = new OptionIOSetting("Select column type",
	                IOSetting.HIGH,
	                e.nextElement().toString(), commandOptions, 
	                IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctX]);
	        i++;
	    }
        return options;
	}
	*/
    public IOSetting[] getIOSettings() {
        return headerOptions;
    }
    public String toString() {
    	return "Reading from SDF file";
    }
    
    
}
