/*
 * Created on 2006-2-18
 *
 */
package ambit2.core.io;

import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;

import ambit2.base.data.Property;

/**
 * A descendant of {@link IteratingMDLReader} able to ask for type of each SDF property
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-2-18
 */
public class InteractiveIteratingMDLReader extends IteratingMDLReader {
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
            
            Map properties = ((IChemObject)o).getProperties();
            if (properties.size()>0) {
            	fireIOSettingQuestion(new StringIOSetting("",IOSetting.MEDIUM,Property.IO_QUESTION.IO_START.toString(),""));
	            Iterator i = properties.keySet().iterator();
	            while (i.hasNext()) { 
	            	String name = i.next().toString();
		            fireIOSettingQuestion(new StringIOSetting(name,IOSetting.MEDIUM,Property.IO_QUESTION.IO_TRANSLATE_NAME.toString(),name));
		        }
            	fireIOSettingQuestion(new StringIOSetting("",IOSetting.MEDIUM,Property.IO_QUESTION.IO_STOP.toString(),""));	            
            }
        }
        return o;
    }

    public String toString() {
    	return "Reading from SDF file";
    }
    
    
}
