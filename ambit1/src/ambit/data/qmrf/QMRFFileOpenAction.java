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

package ambit.data.qmrf;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import ambit.data.ISharedData;
import ambit.io.MyIOUtilities;
import ambit.ui.UITools;

public class QMRFFileOpenAction extends QMRFAction {
	
	public QMRFFileOpenAction(QMRFData userData, Container mainFrame) {
		this(userData, mainFrame,"Open");
	}

	public QMRFFileOpenAction(QMRFData userData, Container mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/open_document_16.png"));
	}

	public QMRFFileOpenAction(QMRFData userData, Container mainFrame,
			String name, Icon icon) {
		super(userData, mainFrame, name, icon);
		putValue(SHORT_DESCRIPTION,"Loads QMRF document from .xml file");
	}
	@Override
	public void run(ActionEvent arg0) {
		super.run(arg0);
		String defaultDir = "";
		if (userData instanceof ISharedData) defaultDir = ((ISharedData) userData).getDefaultDir();
		

        readFile(defaultDir);
        
        
	}
	public void readFile(String defaultDir) {
		if (getQMRFData().getQmrf().isModified()) {
    		if (JOptionPane.showConfirmDialog(mainFrame,
    				"<html>QMRF document <b>\"" + getQMRFData().getQmrf().getSource() + "\"</b> has been modified. <br>Discard changes?</html>",
    			    "Confirm",		
    			    JOptionPane.OK_CANCEL_OPTION,
    			    JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION)
    			return;
		}
        File file = MyIOUtilities.selectFile(mainFrame,null,
                defaultDir,
                new String[] {".xml"},new String[]{"QMRF files (*.xml)"},true);
        if (file == null) return;
        try {
			String s = file.getAbsolutePath();
			int p = s.lastIndexOf(File.separator);
			if (p > 0)
			    defaultDir = s.substring(0,p);
			else defaultDir = s;        	
        	
        	if (file.getAbsolutePath().toLowerCase().endsWith(".xml")) {
        		if (userData instanceof ISharedData)  
        			((ISharedData) userData).setDefaultDir(defaultDir);
        		
        			try {
	        			FileInputStream in = new FileInputStream(file);
	        			getQMRFData().getQmrf().setSource(s);
	        			getQMRFData().getQmrf().transform_and_read(new InputStreamReader(in,"UTF-8"),true);
	        			in.close();
        			} catch (Exception x) {
        				logger.error(x);
                        logger.info("Transformation failed, reading file without XSLT transform");
	        			FileInputStream in = new FileInputStream(file);
	        			getQMRFData().getQmrf().setSource(s);
	        			getQMRFData().getQmrf().read(in);
	        			in.close();
        			}
                 
        			getQMRFData().getJobStatus().setMessage(file.getName() + " loaded.");
        	}	 

    	} catch (Exception x) {
    		JOptionPane.showMessageDialog(mainFrame,
    				x.getMessage(),
    			    "Error when loading file ",						
    			    JOptionPane.INFORMATION_MESSAGE);                
    	}
	}	
}


