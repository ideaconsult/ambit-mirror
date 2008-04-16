/* AmbitDatabase.java
 * Author: Nina Jeliazkova
 * Date: 2006-7-08 
 * Revision: 1.10 
 * 
 * Copyright (C) 2005-2006  
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */
package ambit2.workflow.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.border.BevelBorder;

import ambit2.ui.AmbitStatusBar;
import ambit2.ui.CoreApp;
import ambit2.ui.UITools;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.WorkflowContextFactory;
import ambit2.workflow.WorkflowFactory;

import com.microworkflow.ui.IWorkflowContextFactory;
import com.microworkflow.ui.WorkflowContextListener;
import com.microworkflow.ui.WorkflowContextPanel;
import com.microworkflow.ui.WorkflowMonitor;
import com.microworkflow.ui.WorkflowPanel;

/**
 * A GUI application for database query, data entry and database administration.
 * 

 * @author Nina Jeliazkova
 *
 */
/*

 */
public class AmbitWorkflowApp extends CoreApp {
	WorkflowFactory workflowFactory;
	IWorkflowContextFactory workflowContextFactory;
	/**
	 * @param title
	 * @param width
	 * @param height
	 */
	public AmbitWorkflowApp(String title, int width, int height,String [] args) {
		super(title, width, height,args);
        int state = mainFrame.getExtendedState();
		    
		        // Set the maximized bits
		state |= Frame.MAXIMIZED_BOTH;
		    
		        // Maximize the frame
		mainFrame.setExtendedState(state);

		//MyShutdownHook shutdownHook = new MyShutdownHook();
        //Runtime.getRuntime().addShutdownHook(shutdownHook);


	}

	/* (non-Javadoc)
	 * @see ambit2.ui.CoreApp#initSharedData()
	 */
	protected void initSharedData(String[] args) {
		workflowFactory = new WorkflowFactory();
		workflowContextFactory = new WorkflowContextFactory();
	}

	/* (non-Javadoc)
	 * @see ambit2.ui.CoreApp#createMenuBar()
	 */
	protected JMenuBar createMenuBar() {
		
		JMenuBar menuBar =  new JMenuBar();
		Iterator<String> workflows = workflowFactory.tasks();
		JMenu menu = new JMenu("Tasks");
		menuBar.add(menu);
		ButtonGroup bg = new ButtonGroup();
		while (workflows.hasNext()) {
			JRadioButtonMenuItem mi = new JRadioButtonMenuItem(
					new SelectWorkflowAction(
					workflowFactory,
					mainFrame,
					workflows.next().toString(),
					null)
			);
			bg.add(mi);
			menu.add(mi);
			
		}
		return menuBar;
	}

	/* (non-Javadoc)
	 * @see ambit2.ui.CoreApp#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ambit2.ui.CoreApp#createWidgets(javax.swing.JFrame, javax.swing.JPanel)
	 */
	protected void createWidgets(JFrame aFrame, JPanel aPanel) {
		aPanel.setLayout(new BorderLayout());
		ValueLatchPairEditor controlListener = new ValueLatchPairEditor(workflowContextFactory);
		Vector<String> props = new Vector<String>();
		props.add(DBWorkflowContext.LOGININFO);
		props.add(DBWorkflowContext.DBCONNECTION_URI);
		props.add(DBWorkflowContext.DATASOURCE);
		controlListener.setProperties(props);
        WorkflowMonitor panel = new WorkflowMonitor(
        		new WorkflowPanel(workflowFactory.getWorkflow()),
        		controlListener,
        		new AmbitWorkflowContextPanel(workflowContextFactory),
        		workflowFactory,workflowContextFactory
        );
        aPanel.add(panel,BorderLayout.CENTER);
	}

	protected JPanel createStatusBar() {
		statusBar = new AmbitStatusBar(new Dimension(w,24));
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		return statusBar;	

	}	

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
	      javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	                AmbitWorkflowApp app = new AmbitWorkflowApp("Ambit Workflow 2.0",580,580,args);
	                
	                /*
	                Thread t = new Thread(app.dbadminData);
	                t.start();
	                */
	            }
	        });

	}
    protected boolean canClose() {
        return (JOptionPane.showConfirmDialog(null,"Are you sure to exit ?","Please confirm",JOptionPane.YES_NO_OPTION)
        		==JOptionPane.YES_OPTION);
     }	
    protected void doClose() {
    	/*
        try {
        	
            dbadminData.close();
            dbadminData.stopMySQL();

         } catch (AmbitException x) {
             x.printStackTrace();
         }
                     */
        super.doClose();
   }
    private class MyShutdownHook extends Thread {
        public void run() {
            System.out.println("shutdown ...");
            /*
            try {
                dbadminData.close();
                dbadminData.stopMySQL();
             } catch (AmbitException x) {
                 x.printStackTrace();
             }
             */
        }
    }


    /* (non-Javadoc)
     * @see ambit2.ui.CoreApp#centerScreen()
     */
    public static void centerScreen(JFrame mainFrame) {
		Dimension dim = mainFrame.getToolkit().getScreenSize();
		dim.height -= 50;
		dim.width -= 10;
		Rectangle abounds = mainFrame.getBounds();
  	    mainFrame.setLocation((dim.width - abounds.width) / 2,  (dim.height - abounds.height) / 2);
  	    //mainFrame.setLocation(0,0);
		mainFrame.getRootPane().setPreferredSize(dim);
    }
    protected ImageIcon getIcon() {
		try {
			ImageIcon icon = UITools.createImageIcon("ambit2/ui/images/ambit_logo.jpg");
			return icon;
		} catch (Exception x) {
			return null;
		}
}
    
}

