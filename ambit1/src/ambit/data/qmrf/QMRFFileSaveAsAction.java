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
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.xml.sax.InputSource;

import ambit.data.ISharedData;
import ambit.io.MyIOUtilities;
import ambit.ui.UITools;

public class QMRFFileSaveAsAction extends QMRFAction {
	public QMRFFileSaveAsAction(QMRFData userData, Container mainFrame) {
		this(userData, mainFrame,"Save As");
	}

	public QMRFFileSaveAsAction(QMRFData userData, Container mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/save_16.png"));
	}

	public QMRFFileSaveAsAction(QMRFData userData, Container mainFrame,
			String name, Icon icon) {
		super(userData, mainFrame, name, icon);
		putValue(SHORT_DESCRIPTION,"Saves QMRF document to .xml file");

	}
	@Override
	public void run(ActionEvent arg0) {
		super.run(arg0);
		String defaultDir = "";
		if (userData instanceof ISharedData) defaultDir = ((ISharedData) userData).getDefaultDir();

         saveFile(defaultDir);		
        
	}	
    public File selectFile(String defaultDir) {
        return  MyIOUtilities.selectFile(mainFrame,null,
                defaultDir,
                new String[] {".xml",".pdf",".xls"},new String[]{"QMRF files (*.xml)","PDF files (*.pdf)","XLS files (*.xls)"},false);
        
    }
	public void saveFile(String defaultDir) {
		File file = selectFile(defaultDir);
     
        if (file == null) {
            getQMRFData().getJobStatus().setMessage("No file selected.");
            return;
        }
        try {
			String s = file.getAbsolutePath();
			int p = s.lastIndexOf(File.separator);
			if (p > 0)
			    defaultDir = s.substring(0,p);
			else defaultDir = s;            	
        	FileOutputStream out = new FileOutputStream(file);
            String filename = file.getAbsolutePath().toLowerCase(); 
        	if (filename.endsWith(".xml")) {
        		if (userData instanceof ISharedData) 
        			((ISharedData) userData).setDefaultDir(defaultDir);        		
	            getQMRFData().getQmrf().write(out);
                getQMRFData().getQmrf().setSource(s);
        	} else if (filename.endsWith("html")) {
                
            } else if (filename.endsWith("pdf")) {
            	  Qmrf_Xml_Pdf x = new Qmrf_Xml_Pdf(((QMRFData)userData).qmrf.getTtfFontUrl());
            	
            	  StringWriter w = new StringWriter();	
            	  getQMRFData().getQmrf().write(w);
//            	  System.out.println(w.toString());
            	  x.xml2pdf(w.toString(),out);
            	  
            } else if (filename.endsWith("xls")) {
            	Qmrf_Xml_Excel x = new Qmrf_Xml_Excel();
            	/*
            	PipedInputStream in = new PipedInputStream();
	          	  final PipedOutputStream pipeout = new PipedOutputStream(in);
	          	  new Thread(
	          	    new Runnable(){
	          	      public void run(){
	          	    	  try {
	          	    		  getQMRFData().getQmrf().write(pipeout);
	          	    	  } catch (Exception x) {
	          	    		  x.printStackTrace();
	          	    	  }
	          	      }
	          	    }
	          	  ).start();
	          	  */
            	  StringWriter w = new StringWriter();	
            	  getQMRFData().getQmrf().write(w);
     			  x.xml2excel(new InputSource(new StringReader(w.toString())),out);
            	
            }	

            out.close();
    	} catch (Exception x) {
    		JOptionPane.showMessageDialog(mainFrame,
    				x.getMessage(),
    			    "Error when saving file ",						
    			    JOptionPane.INFORMATION_MESSAGE);                
    	}
	}		
}


