/*
 * Created on 2006-2-18
 *
 */
package ambit2.core.io;

import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;
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
public class InteractiveIteratingMDLReader extends MyIteratingMDLReader {
    /**
     * @param in
     */
	public InteractiveIteratingMDLReader(Reader in,IChemObjectBuilder builder) {
		this(in,builder,false);
	}
    public InteractiveIteratingMDLReader(Reader in,IChemObjectBuilder builder,boolean skip) {
        super(in,builder,skip);

    }

    /**
     * @param in
     */
    public InteractiveIteratingMDLReader(InputStream in, IChemObjectBuilder builder) {
    	this(in,builder,false);
    }
    public InteractiveIteratingMDLReader(InputStream in, IChemObjectBuilder builder,boolean skip) {
        super(in,builder,skip);

    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.iterator.IteratingMDLReader#next()
     */
    public IAtomContainer next() {
        Object o =  super.next();
        if (o instanceof IAtomContainer) {
            
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
            return (IAtomContainer)o;
        }
        return null;
    }

    public String toString() {
    	return "Reading from SDF file";
    }
    
    
}
