/*
 * Created on 2006-2-24
 *
 */
package ambit.io;

import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit.data.molecule.MolProperties;

/**
 * If {@link org.openscience.cdk.io.setting.IOSetting} is of type {@link ambit.io.MolPropertiesIOSetting},
 * then it assigns the properties read from the file to the internal {@link ambit.data.molecule.MolProperties}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-2-24
 */
public class DefaultAmbitIOListener implements IChemObjectIOListener {
    protected MolProperties properties = null;
    /**
     * 
     */
    public DefaultAmbitIOListener(MolProperties properties) {
        super();
        this.properties = properties;
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.listener.ChemObjectIOListener#processIOSettingQuestion(org.openscience.cdk.io.setting.IOSetting)
     */
    public void processIOSettingQuestion(IOSetting setting) {
        if (setting instanceof MolPropertiesIOSetting) {
            
            ((MolPropertiesIOSetting) setting).setProperties(properties);
            
	    
	    }         
	      
    }

}
