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

package ambit.applets;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JPanel;

import ambit.data.qmrf.QMRFData;
import ambit.data.qmrf.QMRFGUITools;
import ambit.data.qmrf.QMRFPanel;
import ambit.ui.UITools;

/**
 * Parameters:

 * <pre>
 dtd <url>          DTD schema location - URL where DTD schema resides (e.g. -dhttp://ambit.acad.bg/qmrf/qmrf.dtd) 
 user <username>    User (if -u admin then Chapter 10 will be editable, otherwise readonly
 cleancatalogs      When saving as XML, include only catalog entries which have an idref reference
 external <URL>     URL to retrieve external catalogs in XML format as defined by <!ELEMENT Catalogs > in QMRF DTD schema
 xmlcontent <URL>   URL to retrieve XML content
  </pre>

 * @author nina
 *
 */
public class QMRFApplet extends JApplet implements Runnable,Observer {
	protected static final String pinfo[][] = {
		 {"dtd",    "url",    "DTD schema location - URL where DTD schema resides (e.g. -dhttp://ambit.acad.bg/qmrf/qmrf.dtd)"},
		 {"user", "string", "if admin then Section 10 will be editable, otherwise readonly"},
		 {"cleancatalogs",   "boolean",     "When saving as XML, include only catalog entries which have an idref reference"},
		 {"external",   "url",     "URL to retrieve external catalogs in XML format as defined by <!ELEMENT Catalogs > in QMRF DTD schema"},
		 {"xmlcontent",   "url",     "URL to retrieve XML content"},
		 {"readonly-attachments",   "boolean",     "If true forbids adding/deleting attachments "},
		 
	 };
	protected QMRFData qmrfData;
	protected final String QmrfEditorVersion = "0.01";
	public QMRFApplet() throws HeadlessException {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Each element of the array should be a set of three Strings containing the name, the type, and a description. 
	 */
	@Override
	public String[][] getParameterInfo() {
		 return pinfo;
	}
	 


	
		 
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		repaint();

	}
	/* (non-Javadoc)
	 * @see java.applet.Applet#start()
	 */
    public void start() {
    	new Thread(this).start();
    	
    }  
   public void stop() {
   }	
	   /* (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	private void createGUI(String args[],boolean adminUser) {
		super.init();
			
		qmrfData = new QMRFData(args,"qmrfeditor.xml",adminUser);
        qmrfData.addObserver(this);

		QMRFPanel p = new QMRFPanel(qmrfData.getQmrf());

		Container ct = getContentPane();
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        /*
        JComponent toolBar = UITools.createToolbar(200,24);
        topPanel.add(QMRFGUITools.createMenuBar(toolBar, qmrfData,ct),BorderLayout.NORTH);
        topPanel.add(toolBar,BorderLayout.CENTER);
        */
        topPanel.add(QMRFGUITools.createMenuBar(null, qmrfData,ct),BorderLayout.NORTH);
        
        mainPanel.add(topPanel,BorderLayout.NORTH);
        
        mainPanel.add(QMRFGUITools.createStatusBar(qmrfData, 200,24),BorderLayout.SOUTH);
        
        mainPanel.add(p,BorderLayout.CENTER);
        
		ct.setPreferredSize(new Dimension(400,400));
		ct.setMinimumSize(new Dimension(400,400));		
		ct.add(mainPanel);
		

	}

	public String getXML() {
		StringWriter w = new StringWriter();
		try {
			qmrfData.getQmrf().write(w);
			return w.toString();
		} catch (Exception x) {
			return null;
		}
	}
	public void setXML(String xml) {
		if (xml == null) return;
		try {
			//xml = URLDecoder.decode(xml,"UTF-8");
			System.out.println(xml);
			StringReader reader = new StringReader(xml);
			qmrfData.getQmrf().read(reader);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	public void init() {
	    //Execute a job on the event-dispatching thread:
	    //creating this applet's GUI.
	    try {
	        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
	            public void run() {
	            	ArrayList<String> args = new ArrayList<String>();
	            	for (int i=0; i < pinfo.length;i++)
		        		try {
		        			Object param = getParameter(pinfo[i][0]);
		        			if (param != null) {
		        				args.add("--"+pinfo[i][0]);
		        				args.add(param.toString());
		        			}
		        		} catch (Exception x) {
		        		}
	        		
	                createGUI(args.toArray(new String[args.size()] ),false);


	        		try {
	        			String dataurl = getParameter("xmlcontent");
	        			URL url = new URL(dataurl);

	        			//qmrfData.getQmrf().transform_and_read(new InputStreamReader(url.openStream(),"UTF-8"),true);
	        			//qmrfData.getQmrf().read(new StringReader(line));	        			
	        			
	        			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
	        			String line;
	        			StringBuffer b = new StringBuffer();
	        			while ((line = reader.readLine()) != null) {
	        		         b.append(line);
	        		       } // end while 
	        			reader.close();
	        			line = b.toString().trim();
	        			qmrfData.getQmrf().transform_and_read(new StringReader(line),true);
	        			//qmrfData.getQmrf().read(new StringReader(line));
	        			
	        			
	        		} catch (Exception x) {
	        			x.printStackTrace();
	        		}	                

	        		
	            }
	        });
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public void update(Observable o, Object arg) {
        StringBuffer b = new StringBuffer();
        b.append("QMRF Editor ");
        b.append(QmrfEditorVersion);
        b.append(" ");
        b.append( qmrfData.getQmrf().getSource());
        if (qmrfData.getQmrf().isModified()) b.append( " *");
	    //getContentPane().setTitle( b.toString());
	    
	}	

}


