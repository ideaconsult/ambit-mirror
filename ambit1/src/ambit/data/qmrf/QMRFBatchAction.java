package ambit.data.qmrf;

import java.awt.Container;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.xml.sax.InputSource;

import ambit.io.MyIOUtilities;
import ambit.ui.UITools;

public class QMRFBatchAction extends QMRFFileOpenAction {

	public QMRFBatchAction(QMRFData userData, Container mainFrame) {
		this(userData, mainFrame,"Batch PDF Generation");
	}


	public QMRFBatchAction(QMRFData userData, Container mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/disk_multiple.png"));
	}

	public QMRFBatchAction(QMRFData userData, Container mainFrame,
			String name, Icon icon) {
		super(userData, mainFrame, name, icon);
		putValue(SHORT_DESCRIPTION,"Creates .pdf files from multiple QMRF documents without loading the files into QMRF editor.");
	}
	@Override
	public void readFile(String defaultDir) {
        File[] files = MyIOUtilities.selectFiles(mainFrame,null,
                defaultDir,
                new String[] {".xml"},new String[]{"QMRF files (*.xml)"},true,null);
        if (files != null) {
        	int error = 0;
        	mainFrame.setCursor(hourglassCursor);
	        Qmrf_Xml_Pdf converter = new Qmrf_Xml_Pdf(((QMRFData)userData).qmrf.getTtfFontUrl());
	        for (int i=0; i < files.length;i++) 
	        try {
	        	InputStreamReader reader = 	new InputStreamReader(new FileInputStream(files[i]),"UTF-8");
	        	String extension = ".pdf";
	    	    
	        	String suffix = null;
	    	    String fname= files[i].getAbsolutePath(); 
	    	    int p = fname.lastIndexOf('.');

	    	    File pdffile = null;
	    	    if(p > 0 && p < fname.length() - 1)
	    	    	pdffile = new File(fname.substring(0,p)+extension); 
	    	    else
	    	    	pdffile = new File(fname+extension);
	    	    
	        	OutputStream pdf = new FileOutputStream(pdffile);
	        	converter.xml2pdf(new InputSource(reader), pdf);
	        	reader.close();
	        } catch (Exception x) {
	        	error ++;
	        	logger.error(x);
	        }
	        StringBuffer b = new StringBuffer();
	        b.append(files.length);
	        b.append(" files processed; ");
	        b.append(files.length-error);
	        b.append(" pdf files generated.");
	        mainFrame.setCursor(normalCursor);
    		JOptionPane.showMessageDialog(mainFrame,
    				b.toString(),
    			    "PDF Generation ",						
    			    JOptionPane.INFORMATION_MESSAGE);        	        
        }
        
	}
}
