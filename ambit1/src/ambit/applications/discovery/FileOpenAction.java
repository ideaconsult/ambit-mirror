/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit.applications.discovery;

import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.listener.IReaderListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit.data.descriptors.DescriptorsList;
import ambit.domain.DataModule;
import ambit.domain.QSARDataset;
import ambit.domain.QSARDatasetWriter;
import ambit.io.AmbitSettingsListener;
import ambit.io.MolPropertiesIOSetting;

/**
 * Opens a file as in inherited class {@link ambit.ui.actions.file.FileOpenAction}
 * but uses {@link QSARDatasetWriter} as to fill in {@link QSARDataset}
 * @author Nina Jeliazkova
 *
 */
public class FileOpenAction extends ambit.ui.actions.file.FileOpenAction {
	protected QSARDatasetWriter writer = null;
	protected boolean training = true;
	
	public FileOpenAction(Object userData, JFrame mainFrame, boolean training) {
		super(userData, mainFrame,"Open");
		this.training = training;
	}

	public FileOpenAction(Object userData, JFrame mainFrame, String name, boolean training) {
		super(userData, mainFrame, name);
		this.training = training;
	}

	public FileOpenAction(Object userData, JFrame mainFrame, String name,
			Icon icon, boolean training) {
		super(userData, mainFrame, name, icon);
		this.training = training;

	}
	public IChemObjectWriter getWriter() {
		if (userData instanceof DataModule) {
		    DataModule dbaData = ((DataModule) userData);
		    if (training)
		    	writer = new QSARDatasetWriter(dbaData.modelData);
		    else {
		    	QSARDataset dataset = (QSARDataset)dbaData.testData.createNewItem();
		    	dbaData.testData.addItem(dataset);
		    	writer = new QSARDatasetWriter(dataset);
		    	
		    }	
			return writer;
		} else return null;	
	}
	protected IReaderListener getReaderListener() {
		//return new QSARSettingsListener(mainFrame,IOSetting.LOW) {
		return new AmbitSettingsListener(mainFrame,IOSetting.LOW) {
			protected DescriptorsList descriptors = null;
	    	public void processIOSettingQuestion(IOSetting setting) {
	    		super.processIOSettingQuestion(setting);
	    		if ((setting instanceof MolPropertiesIOSetting) && (writer != null)) {
	    			if (descriptors == null) {
		    			descriptors = ((MolPropertiesIOSetting) setting).getSelectedProperties(); 
		    			writer.setDescriptors(descriptors);
	    			}
	    		}	
	    	}
	    };
	}

}


