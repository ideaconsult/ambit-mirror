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

package ambit.applications.qmrf;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import ambit.data.qmrf.QMRFData;
import ambit.data.qmrf.QMRFGUITools;
import ambit.data.qmrf.QMRFPanel;
import ambit.log.AmbitLogger;
import ambit.ui.CoreApp;

/**
 * QSAR Model Report Format Editor.
 *

<pre>
usage: QMRFEditor
 -d,--dtd <url>          DTD schema location - URL where DTD schema
                         resides (e.g. -dfile:///D:/src/ambit/qmrf.dtd or
                         -dhttp://ambit.acad.bg/qmrf/qmrf.dtd)
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
2) Chapter 1 – field 1.3 - In the main window it would be necessary to report more detailed information on how to fill in the field. What about something like this?
Add information about the software coding the model by adding a new item or selecting it from the catalogue:
a) Selecting an item from the catalogue: select the catalogue key on the left (magnifying lens), select the software you are interested in (if any), select OK.
b) Adding a new item in the field: select the plus key on the left, fill in the required information …
c) Adding an item on your catalogue: …

it is a bit confusing how the + and delete keys work. Furthermore I don’t understand the following:
-         the pencil key function
-         if it is necessary to check out an item to have it saved in the final xml.
Probably the catalogue should be empty at the beginning or contain one single item. What do you think?

3) Chapter 2 – please note that when trying to open chapter 3, the editor shows the end of the page (this can be confusing). Same problems with other chapters (eg. Chapter 3).

4) Chapter 4 – we should add more explanations on what to write on the algorithm part and what on the equation part.
 This is a general thing we should keep in mind: 
 add as much (short) instructions as possible on how to fill in the fields (from the technical and content point of view).

5) Chapter 4 – Field 4.4 – Descriptors in the model
We should inform the user that the name specified here should correspond to the one used in the supporting information,
 as you correctly pointed out some time ago. 

 * 
 * TODO Editor for algorithms and descriptors (embedded reference) 
 * TODO Menu option for XSLT transformation
 * DONE Menu option for PDF (use FOP)
 * DONE be able to use symbols, font, subscripts in the text
 * TODO change DTD to XSchema.
 * TODO use descriptor and algorithm dictionaries from Blue Obelisk
 * TODO include in manifest , build and jnlp file (for PDF handling) 
 * fop.jar
 * avalon-framework-4.2.0.jar commons-io-1.1.jar commons-logging-1.0.4.jar 
 * TODO read http://msdn2.microsoft.com/en-us/library/aa203691(office.11).aspx
 * TODO Java to WordML http://blogs.msdn.com/dotnetinterop/archive/2005/03/29/403331.aspx
 * TODO Excel http://msdn2.microsoft.com/en-us/library/aa203737(office.11).aspx
 * TODO Excel XML toolbox http://www.microsoft.com/downloads/details.aspx?familyid=e315c516-2c2c-4870-a189-d47a5d7ffeb3&displaylang=en
 * TODO http://fo2wordml.sourceforge.net/
 * TODO Word FO designer http://www.cambridgedocs.com/products/downloads/WordFODownload.htm
 * http://www.cambridgedocs.com/products/downloads/WordFODownload.htm
 * TODO http://mathcast.sourceforge.net/home.html 
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


