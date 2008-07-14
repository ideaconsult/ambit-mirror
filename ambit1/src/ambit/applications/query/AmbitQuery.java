/* AmbitQuery.java
 * Author: Nina Jeliazkova
 * Date: 2008-7-12 
 * Revision: 1.30
 * 
 * Copyright (C) 2005-2008  
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
package ambit.applications.query;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Observable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import sicret.SicretRules;
import toxTree.tree.cramer.CramerRules;
import verhaar.VerhaarScheme;
import ambit.database.aquire.AquireRetrieveAction;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.data.ISharedDbData;
import ambit.exceptions.AmbitException;
import ambit.ui.AboutAction;
import ambit.ui.AmbitStatusBar;
import ambit.ui.CoreApp;
import ambit.ui.UITools;
import ambit.ui.actions.ActionFactory;
import ambit.ui.actions.process.Builder3DAction;
import ambit.ui.actions.process.DescriptorCalculatorAction;
import ambit.ui.actions.process.MetaboliteAction;
import ambit.ui.actions.process.MoleculeMOPACAction;
import ambit.ui.actions.process.RetrieveDataAction;
import ambit.ui.actions.process.ToxTreeAction;
import ambit.ui.actions.search.DbDescriptorsSearchAction;
import ambit.ui.actions.search.DbExperimentsSearchAction;
import ambit.ui.actions.search.DbSaveAsDataset;
import ambit.ui.actions.search.ExperimentsByStudyAction;
import ambit.ui.data.molecule.AmbitDetailsPanel;
import ambit.ui.query.DbQueryOptionsPanel;

/**
 * A GUI application for database query, data entry and database administration.
 * 

 * @author Nina Jeliazkova
 *
 */
/*
 * TODO delete experiments by template
delete experiment FROM experiment,study where experiment.idstudy=study.idstudy and idtemplate=8
delete  FROM study where idtemplate=8
delete  FROM template where idtemplate=8

introduce reference field into templates
 */
public class AmbitQuery extends CoreApp {
	protected AmbitDatabaseToolsData dbadminData;
	protected ActionMap structureSearchActions;
	protected ActionMap smartsSearchActions;
	protected ActionMap optionsActions;
	protected ActionMap searchActions;
	//protected ActionMap batchSearchActions;
	//protected ActionMap calculationActions;
	protected ActionMap databaseActions;
	protected ActionMap connectActions;
	protected ActionMap databaseAdminActions;
	protected ActionMap mysqlActions;
	protected ActionMap allActions;
	
	protected AbstractAction moleculeEditAction = null;
	/*
     * TODO add molecules to the dataset loaded.
     * TODO delete dataset
delete from dvalues where idstructure in (select s.idstructure from structure as s join struc_dataset using(idstructure) where id_srcdataset=7);
delete from experiment where idstructure in (select s.idstructure from struc_dataset as s where id_srcdataset=7);
delete from struc_user where idstructure in (select s.idstructure from struc_dataset as s where id_srcdataset=7);
delete from alias where idstructure  in (select s.idstructure from struc_dataset as s where id_srcdataset=7);
delete from cas where idstructure  in (select s.idstructure from struc_dataset as s where id_srcdataset=7);
delete from name where idstructure  in (select s.idstructure from struc_dataset as s where id_srcdataset=7);

delete from structure where idstructure in (select s.idstructure from struc_dataset as s where id_srcdataset=7);

delete from src_dataset where id_srcdataset=7; 
	 */
	/**
	 * @param title
	 * @param width
	 * @param height
	 */
	public AmbitQuery(String title, int width, int height,String [] args) {
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
	 * @see ambit.ui.CoreApp#initSharedData()
	 */
	protected void initSharedData(String[] args) {
		dbadminData = new AmbitDatabaseToolsData(false);
		dbadminData.setResultDestination(ISharedDbData.RESULTS_QUERY);
	}

	/* (non-Javadoc)
	 * @see ambit.ui.CoreApp#createMenuBar()
	 */
	protected JMenuBar createMenuBar() {
	    allActions = new ActionMap();
		structureSearchActions = new ActionMap();
		smartsSearchActions = new ActionMap();
		//batchSearchActions = new ActionMap();
		searchActions = new ActionMap();
		dbadminData.setDescriptorActions(new ActionMap());
		dbadminData.setExperimentsActions(new ActionMap());
		
		//calculationActions = new ActionMap();
		databaseActions = new ActionMap();		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu;
		
		
		menu = new JMenu("File");
		menuBar.add(menu);
		ActionMap fileActions = new ActionMap();
		ActionFactory.createSaveExitActions(fileActions,dbadminData,mainFrame);
		ActionFactory.addToMenu(fileActions,menu);
		ActionFactory.setParentActions(fileActions,allActions);
		
		menu = new JMenu("Database");
		menuBar.add(menu);
		connectActions = new ActionMap();
		ActionFactory.createDatabaseConnectActions(connectActions,dbadminData,mainFrame);
		ActionFactory.addToMenu(connectActions,menu);
		ActionFactory.setParentActions(connectActions,allActions);
		JMenu submenu  = new JMenu("Database processing");
		menu.add(submenu);
		ActionFactory.createDatabaseActions(databaseActions,dbadminData,mainFrame);
		ActionFactory.addToMenu(databaseActions,submenu);
		ActionFactory.setParentActions(databaseActions,allActions);		
		
		submenu = new JMenu("Administration");
		menu.add(submenu);
		databaseAdminActions = new ActionMap();
		ActionFactory.createDatabaseAdminActions(databaseAdminActions,dbadminData,mainFrame);
		ActionFactory.addToMenu(databaseAdminActions,submenu);
		ActionFactory.setParentActions(databaseAdminActions,allActions);			
		
		/*
		submenu  = new JMenu("Molecule");
		menuBar.add(submenu);
		moleculeEditAction = new MoleculeEditAction(null,dbadminData,mainFrame,"Structure diagram editor");
		calculationActions.put("Edit",moleculeEditAction);
		createMoleculeCalculationActions(calculationActions,dbadminData,mainFrame);
		ActionFactory.addToMenu(calculationActions,submenu);
		ActionFactory.setParentActions(calculationActions,allActions);
		*/				
		
		menu = new JMenu("Search");
		menuBar.add(menu);
		ActionFactory.createSearchActions(searchActions,dbadminData,mainFrame);
		ActionFactory.createBatchProcessingActions(searchActions,dbadminData,mainFrame);
		ActionFactory.setParentActions(searchActions,allActions);		
		ActionFactory.addToMenu(searchActions,menu);
		
		submenu = new JMenu("Search Options");
		menu.add(submenu);
		optionsActions = new ActionMap();
		ActionFactory.createOptionsActions(optionsActions,dbadminData,mainFrame);
		ActionFactory.setParentActions(optionsActions,allActions);		
		ActionFactory.addToMenu(optionsActions,submenu);
		
		/*
		Action queryEditAction  = new MoleculeEditAction(null,dbadminData,mainFrame,"Edit query compound");
		((MoleculeEditAction) queryEditAction).setQuery(true);
		structureSearchActions.put("Edit",queryEditAction);
		*/
		Action a;
		submenu = new JMenu("Advanced search");
		menu.add(submenu);
		ActionFactory.createStructureSearchActions(structureSearchActions,dbadminData,mainFrame);
		ActionFactory.setParentActions(structureSearchActions,allActions);		
		ActionFactory.addToMenu(structureSearchActions,submenu);
		dbadminData.setStructureActions(structureSearchActions);

		ActionFactory.createSMARTSSearchActions(smartsSearchActions,dbadminData,mainFrame);
		ActionFactory.setParentActions(smartsSearchActions,allActions);		
		ActionFactory.addToMenu(smartsSearchActions,submenu);
		dbadminData.setSMARTSActions(smartsSearchActions);
		
		submenu = new JMenu("Descriptors");
		menu.add(submenu);
		String c = "Search by descriptors and distance between atoms";
		a = new DbDescriptorsSearchAction(dbadminData,mainFrame,"Search");
		dbadminData.getDescriptorActions().put(c, a);
		ActionFactory.setParentActions(dbadminData.getDescriptorActions(),allActions);		
		ActionFactory.addToMenu(dbadminData.getDescriptorActions(),submenu);

		submenu = new JMenu("Experiments");
		menu.add(submenu);
		c = "Search by experiments";
		a = new DbExperimentsSearchAction(dbadminData,mainFrame,"Search");
		dbadminData.getExperimentsActions().put(c, a);
        
		c = "Experiments by Study";
		a = new ExperimentsByStudyAction(dbadminData,mainFrame,c);
		dbadminData.getExperimentsActions().put(c, a);
		ActionFactory.setParentActions(dbadminData.getExperimentsActions(),allActions);		
		ActionFactory.addToMenu(dbadminData.getExperimentsActions(),submenu);

		
		JMenu menu1 = new JMenu("MySQL");
		menuBar.add(menu1);
		mysqlActions = new ActionMap();
		ActionFactory.createMySQLActions(mysqlActions,dbadminData,mainFrame);
		ActionFactory.addToMenu(mysqlActions,menu1);
		ActionFactory.setParentActions(mysqlActions,allActions);		

		
		menu = new JMenu("Help");
		menuBar.add(menu);
		/*
		ambit.ui.actions.HelpAction helpAction = new ambit.ui.actions.HelpAction("AMBIT Database Tools HELP",mainFrame);
		menu.add(helpAction);
		*/
		
		AboutAction aboutAction = new AboutAction(getAboutString("ambit.applications.query"),mainFrame);
		aboutAction.setIconFile("ambit/ui/images/ambit_logo.jpg");
		menu.add(aboutAction);

		
		return menuBar;
	}

	/* (non-Javadoc)
	 * @see ambit.ui.CoreApp#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ambit.ui.CoreApp#createWidgets(javax.swing.JFrame, javax.swing.JPanel)
	 */
	protected void createWidgets(JFrame aFrame, JPanel aPanel) {
		aPanel.setLayout(new BorderLayout());
		aPanel.add(new DbQueryOptionsPanel(dbadminData),BorderLayout.CENTER);
		toolBar.setVisible(false);
		//toolBar.add(entryPanel);
		
	}
	protected JComponent createQueryPanel() {

		AmbitDetailsPanel pane = new AmbitDetailsPanel();
        pane.setPreferredSize(new Dimension(100,300));
        dbadminData.getMolecules().addObserver(pane);
        return pane;
/*        
        return new CompoundPanel(dbadminData.getMolecules(),
				moleculeEditAction
				,Color.white,Color.BLUE,JSplitPane.HORIZONTAL_SPLIT);
  */      

        
	}

	protected JPanel createStatusBar() {
		statusBar = new AmbitStatusBar(new Dimension(w,24));
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		dbadminData.getJobStatus().addObserver((AmbitStatusBar)statusBar);
		if (dbadminData.getBatchStatistics() instanceof Observable)
			((Observable)dbadminData.getBatchStatistics()).addObserver((AmbitStatusBar)statusBar);
		return statusBar;	

	}	

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
	      javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	                AmbitQuery app = new AmbitQuery(getTitle("ambit.applications.query"),580,580,args);
	                
	                Thread t = new Thread(app.dbadminData);
	                t.start();
	            }
	        });

	}
    protected boolean canClose() {
        return (!dbadminData.isLoading()) && (JOptionPane.showConfirmDialog(null,"Are you sure to exit ?","Please confirm",JOptionPane.YES_NO_OPTION)
        		==JOptionPane.YES_OPTION);
     }	
    protected void doClose() {
        try {
            dbadminData.close();
            dbadminData.stopMySQL();
         } catch (AmbitException x) {
             x.printStackTrace();
         }
        super.doClose();
   }
    private class MyShutdownHook extends Thread {
        public void run() {
            System.out.println("shutdown ...");
            try {
                dbadminData.close();
                dbadminData.stopMySQL();
             } catch (AmbitException x) {
                 x.printStackTrace();
             }
        }
    }


    /* (non-Javadoc)
     * @see ambit.ui.CoreApp#centerScreen()
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
			ImageIcon icon = UITools.createImageIcon("ambit/ui/images/ambit_logo.jpg");
			return icon;
		} catch (Exception x) {
			return null;
		}
}
    public static void createMoleculeCalculationActions(ActionMap actions, Object userData, JFrame mainFrame) {
        try {
        actions.put("4-1",new ToxTreeAction(new VerhaarScheme(),userData,mainFrame));            
        actions.put("4-2",new ToxTreeAction(new CramerRules(),userData,mainFrame));
        actions.put("4-3",new ToxTreeAction(new SicretRules(),userData,mainFrame));        
        } catch (Exception x) {
            x.printStackTrace();
        }
        actions.put("1Descriptors",new DescriptorCalculatorAction(userData,mainFrame,"Descriptors calculator"));
        
        actions.put("2",new MoleculeMOPACAction(userData,mainFrame,"Calculate electronic parameters by MOPAC"));
        actions.put("3",new Builder3DAction(userData,mainFrame,"Build 3D"));
        actions.put("6",new MetaboliteAction(userData,mainFrame,"Metabolites"));
        actions.put("5",new AquireRetrieveAction(userData,mainFrame,"AQUIRE data"));
        actions.put("0",new RetrieveDataAction(userData,mainFrame,"Advanced data retrieval"));
        actions.put("7",new DbSaveAsDataset(userData,mainFrame,"Save as dataset"));
        
        
    }    
    
}

