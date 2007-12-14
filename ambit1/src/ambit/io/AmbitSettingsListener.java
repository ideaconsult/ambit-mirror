/*
 * Created on 2006-2-18
 *
 */
package ambit.io;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.openscience.cdk.io.ReaderEvent;
import org.openscience.cdk.io.listener.IReaderListener;
import org.openscience.cdk.io.listener.IWriterListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit.data.molecule.MolProperties;
import ambit.ui.data.MolPropertiesPanel;

/**
 * An implementation of {@link org.openscience.cdk.io.listener.IReaderListener} and {@link org.openscience.cdk.io.listener.IWriterListener}.
 * On each {@link #processIOSettingQuestion(IOSetting)} call launches an {@link ambit.ui.data.MolPropertiesPanel} visualizing current properties {@link ambit.data.molecule.MolProperties}.. 
 * The user may change the assignment of the properties.<br> 
 * To be used as {@link org.openscience.cdk.io.listener.IChemObjectIOListener}
 * in order to process {@link ambit.io.MolPropertiesIOSetting}, a descendant of {@link org.openscience.cdk.io.setting.IOSetting}.
 * See exapmle at {@link ambit.database.writers.DbSubstanceWriter}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-2-18
 */
public class AmbitSettingsListener implements IReaderListener, IWriterListener{
    protected int level;
    protected int propertiesCount = 0;
    protected Component frame;
    /**
     * @param frame
     * @param level
     */
    public AmbitSettingsListener(Component frame, int level) {
        super();
        this.frame = frame;
        this.level = level;
        
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.listener.SwingGUIListener#processIOSettingQuestion(org.openscience.cdk.io.setting.IOSetting)
     */
    public void processIOSettingQuestion(IOSetting setting) {
        if (setting.getLevel() < this.level) 
	        if (setting instanceof MolPropertiesIOSetting) {
	        	MolProperties properties = ((MolPropertiesIOSetting)setting).getProperties();
	        	
	        	if (propertiesCount != properties.getProperties().size()) 
		            JOptionPane.showMessageDialog(frame,
		            		createUI(properties),
		            		setting.getName(),JOptionPane.PLAIN_MESSAGE);
	        	propertiesCount = properties.getProperties().size();	        	
	        	/*
	            JDescriptorsDialog d = new JDescriptorsDialog((Frame)null,setting.getName(),"",true,
	                    ((MolPropertiesIOSetting) setting).getDescriptors());
	            d.show();
	            if (d.getResult() == JOptionPane.OK_OPTION) {
	                ((MolPropertiesIOSetting) setting).setDescriptors(d.getXDescriptors());
	            }
	            d = null;
	            */
	        } 
    }
    protected JComponent createUI(MolProperties properties) {
    	return new MolPropertiesPanel(properties);
    }
    public void frameRead(ReaderEvent arg0) {
    	// TODO Auto-generated method stub
    	
    }
}
