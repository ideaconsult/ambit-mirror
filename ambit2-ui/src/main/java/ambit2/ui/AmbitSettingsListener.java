/*
 * Created on 2006-2-18
 *
 */
package ambit2.ui;

//TODO derive from SimpleIOListener
import javax.swing.JOptionPane;

import org.openscience.cdk.io.ReaderEvent;
import org.openscience.cdk.io.listener.IReaderListener;
import org.openscience.cdk.io.listener.IWriterListener;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.IOSetting.Importance;

import ambit2.base.data.Profile;
import ambit2.base.data.ProfileListModel;
import ambit2.base.data.Property;
import ambit2.ui.editors.SelectFieldsPanel;

/**
 * An implementation of {@link org.openscience.cdk.io.listener.IReaderListener} and {@link org.openscience.cdk.io.listener.IWriterListener}.
 * On each {@link #processIOSettingQuestion(IOSetting)} call launches a 
 * {@link SelectFieldsPanel} visualizing current list of properties 
 * {@link Property}. 
 * The user may change the assignment of the properties.<br> 
 * To be used as {@link org.openscience.cdk.io.listener.IChemObjectIOListener}
 * in order to process properties {@link Property}. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2008-12-13
 */
public class AmbitSettingsListener implements IReaderListener, IWriterListener{
    protected Importance level;
    protected Profile<Property> properties;
    protected int counter= 0;

    /**
     * 
     * @param level
     */
    public AmbitSettingsListener(Importance level) {
        super();
        this.level = level;
        properties =  new Profile();

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.listener.SwingGUIListener#processIOSettingQuestion(org.openscience.cdk.io.setting.IOSetting)
     */
    public void processIOSettingQuestion(IOSetting setting) {
        if (setting.getLevel().ordinal() <= this.level.ordinal()) { 
        	try {
        		Property.IO_QUESTION question = Property.IO_QUESTION.valueOf(setting.getQuestion());
        		switch (question) {
        		case IO_START: { counter = 0;}
        		case IO_STOP: { if (counter > 0) launchUI(); }
        		case IO_TRANSLATE_NAME: { 
        			if (!"".equals(setting.getName().trim())) {
        				//if (properties.get(setting.getName())==null) {
        					Property p = Property.getInstance(setting.getName(),"I/O");
        					p.setLabel(setting.getDefaultSetting());
        					p.setOrder(counter);
        					properties.add(p);
        					counter++;
        				//}	
        			}
        			}
        		default: {}
        		}
        	} catch (Exception x) {
        		x.printStackTrace();
        	}
        } else {
        	try {
        		Property.IO_QUESTION question = Property.IO_QUESTION.valueOf(setting.getQuestion());
        		switch (question) {
        		case IO_START: { counter = 0;}
        		case IO_STOP: {  }
        		case IO_TRANSLATE_NAME: { 
        			if (!"".equals(setting.getName().trim())) {
        				//if (properties.get(setting.getName())==null) {
        					Property p = Property.getInstance(setting.getName(),"I/O");
        					p.setLabel(setting.getDefaultSetting());
        					p.setOrder(counter);
        					p.setEnabled(true);
        					properties.add(p);
        					counter++;
        				//}	
        			}
        			}
        		default: {}
        		}
        	} catch (Exception x) {
        		x.printStackTrace();
        	}        	
        }
    }
    
    protected void launchUI() {
    	SelectFieldsPanel sp = new SelectFieldsPanel(new ProfileListModel(properties),"All available fields that can be exported are listed in the left pane. Select those fields you want to be exported and click \">\" button to move them to right pane.");
    	JOptionPane.showMessageDialog(null,sp,"",JOptionPane.PLAIN_MESSAGE);
    }

    public void frameRead(ReaderEvent event) {
    	
    	
    }
	public Profile<Property> getProperties() {
		return properties;
	}
	public void setProperties(Profile properties) {
		this.properties = properties;
	}
}
