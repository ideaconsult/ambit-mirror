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

package ambit2.qmrf.swing;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import ambit2.base.log.AmbitLogger;
import ambit2.qmrf.QMRFData;
import ambit2.swing.common.CoreApp;

/**
 * QSAR Model Report Format Editor.
 *

<pre>
usage: QMRFEditor
 -d,--dtd <url>          DTD schema location - URL where DTD schema
                         resides (e.g. -dfile:///D:/src/ambit/qmrf.dtd or
                         -dhttp://ambit.sourceforge.net/qmrf/qmrf.dtd)
 -u,--user <username>    User (if -u admin then Chapter 10 will be
                         editable, otherwise readonly
 -c,--cleancatalogs      When saving as XML, include only catalog entries
                         which have an idref reference
 -e,--external <URL>     URL to retrieve external catalogs in XML format
                         as defined by <!ELEMENT Catalogs > in QMRF DTD schema
 -h,--help               This screen
 -t,--ttf <URL>          URL to retrieve TrueType font
 -x,--xmlcontent <URL>   URL to retrieve XML content
</pre> 

 * @author Nina Jeliazkova
 *
 */
public class QMRFEditor extends CoreApp implements Observer {
	protected QMRFData qmrfData;
    
	protected final String QmrfEditorVersion = "0.05";
	public QMRFEditor(String title, int w , int h,String[] args) {
		super(title, w, h,args);
        int state = mainFrame.getExtendedState();
	    
        // Set the maximized bits
        state |= Frame.MAXIMIZED_BOTH;
    
        // Maximize the frame
        mainFrame.setExtendedState(state);
		//centerScreen();
        AmbitLogger.configureLog4j(true);
		Package adPackage = Package.getPackage("ambit.data.qmrf");
		//version will be only available if started from jar file
		//version is specified in package manifest 
		// See MANIFEST_AD.MFT file
		String pTitle = null;
		String version = null;
		if (adPackage != null) {
		    pTitle = adPackage.getSpecificationTitle();
			version = adPackage.getImplementationVersion();
		}
		
		if (pTitle == null) pTitle = title;
		if (version == null) version = QmrfEditorVersion;
		
		//mainFrame = new JFrame(pTitle+version);
		setCaption(pTitle+version);
        
        //qmrfData.setParameters(args);
		
	}	
    protected JPanel createStatusBar() {
    	statusBar = QMRFGUITools.createStatusBar(qmrfData, w,24);
        return statusBar;   

    }   
	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected JMenuBar createMenuBar() {
		return QMRFGUITools.createMenuBar(toolBar,qmrfData,mainFrame);
	}
	@Override
	protected JComponent createToolbar() {
		return null;
	}
	

	@Override
	protected void createWidgets(JFrame aFrame, JPanel aPanel) {
		QMRFPanel p = new QMRFPanel(qmrfData.getQmrf());
		aPanel.setLayout(new BorderLayout());
		aPanel.add(p,BorderLayout.CENTER);
		aFrame.setLayout(new BorderLayout());

	}

	@Override
	protected void initSharedData(String[] args) {
 
		qmrfData = new QMRFData(args,"qmrfeditor.xml",false);
        qmrfData.addObserver(this);

	}
	@Override
	protected boolean canClose() {
		// TODO Auto-generated method stub
		boolean c = super.canClose();
		if (c) qmrfData.saveConfiguration();
		return c;
	}
	public static void main(final String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                QMRFEditor app = new QMRFEditor("QMRF Editor ",580,360,args);
            }
        });
   }	
	public void update(Observable o, Object arg) {
        StringBuffer b = new StringBuffer();
        b.append("QMRF Editor ");
        b.append(QmrfEditorVersion);
        b.append(" ");
        b.append( qmrfData.getQmrf().getSource());
        if (qmrfData.getQmrf().isModified()) b.append( " *");
	    mainFrame.setTitle( b.toString());
	    
	}
}


