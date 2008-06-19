/* ActionsTest.java
 * Author: Nina Jeliazkova
 * Date: Sep 1, 2006 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
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

package ambit.test.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Observable;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;

import junit.framework.TestCase;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.query.DescriptorQueryList;
import ambit.database.query.ExperimentConditionsQuery;
import ambit.database.query.ExperimentQuery;
import ambit.ui.AmbitStatusBar;
import ambit.ui.actions.ActionFactory;
import ambit.ui.actions.dbadmin.DbConnectionStatusAction;
import ambit.ui.actions.dbadmin.DbOpenAction;
import ambit.ui.actions.file.FileExportAction;
import ambit.ui.actions.search.DbCASSearchAction;
import ambit.ui.actions.search.DbDescriptorsSearchAction;
import ambit.ui.actions.search.DbExactSearchAction;
import ambit.ui.actions.search.DbExperimentsSearchAction;
import ambit.ui.actions.search.DbFormulaSearchAction;
import ambit.ui.actions.search.DbNameSearchAction;
import ambit.ui.actions.search.DbResultsDestinationAction;
import ambit.ui.actions.search.DbSMILESSearchAction;
import ambit.ui.actions.search.DbSimilaritySearchAction;
import ambit.ui.actions.search.DbStructureToQuery;
import ambit.ui.actions.search.DbSubstructureSearchAction;
import ambit.ui.actions.search.ExperimentsByStudyAction;
import ambit.ui.actions.search.SelectDatasetAction;
import ambit.ui.actions.search.QueryOptionsAction;
import ambit.ui.data.molecule.CompoundPanel;
import ambit.ui.query.DescriptorQueryPanel;
import ambit.ui.query.ExperimentsQueryPanel;
import ambit.ui.query.StructureQueryPanel;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class ActionsTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ActionsTest.class);
    }
    public void testCASSearch() {
        //JFrame frame = new JFrame();
        JFrame frame = null;
     	AmbitDatabaseToolsData dbadminData = new AmbitDatabaseToolsData(false);
     	DbOpenAction openAction = new DbOpenAction(dbadminData,frame);
     	DbConnectionStatusAction statusAction = new DbConnectionStatusAction(dbadminData,frame,"CAS search");
     	
     	DbResultsDestinationAction resultsAction = new DbResultsDestinationAction(dbadminData,frame,"Results destination");
     	
     	DbCASSearchAction casSearchAction = new DbCASSearchAction(dbadminData,frame,"CAS search");
     	DbNameSearchAction nameSearchAction = new DbNameSearchAction(dbadminData,frame,"Chemical name search");
     	DbFormulaSearchAction formulaSearchAction = new DbFormulaSearchAction(dbadminData,frame,"Chemical formula search");
     	DbSMILESSearchAction smilesSearchAction = new DbSMILESSearchAction(dbadminData,frame,"SMILES search");
     	
     	CompoundPanel panel = new CompoundPanel(dbadminData.getMolecules(),null, Color.white,Color.black,JSplitPane.HORIZONTAL_SPLIT);
     	
     	JPanel mainPanel = new JPanel(new BorderLayout());
     	//Container mainPanel = frame.getContentPane();
     	mainPanel.setLayout(new BorderLayout());
     	mainPanel.setPreferredSize(new Dimension(600,400));
     	JToolBar toolbar = new JToolBar();
     	toolbar.add(openAction);
     	toolbar.add(statusAction);
     	toolbar.add(resultsAction);
     	toolbar.add(casSearchAction);
     	toolbar.add(nameSearchAction);
     	toolbar.add(formulaSearchAction);
     	toolbar.add(smilesSearchAction);
     	
     	mainPanel.add(toolbar,BorderLayout.NORTH);
     	mainPanel.add(panel,BorderLayout.CENTER);
     	
     	
     	JOptionPane.showMessageDialog(null,mainPanel);
     	
    }
    public void testSearch() {
        //A fully functional demo for structure, descriptors and experimental data queries
        JFrame frame = null;
     	AmbitDatabaseToolsData dbadminData = new AmbitDatabaseToolsData(true);
     	
     	dbadminData.loadData();
     	
     	FileExportAction exportToFile = new FileExportAction(dbadminData,frame);
     	
     	DbOpenAction openAction = new DbOpenAction(dbadminData,frame);
     	DbConnectionStatusAction statusAction = new DbConnectionStatusAction(dbadminData,frame,"CAS search");
     	
     	DbResultsDestinationAction resultsAction = new DbResultsDestinationAction(dbadminData,frame,"Results destination");

     	AbstractAction optionsAction =  new QueryOptionsAction(dbadminData,frame,"Options");
     	
     	ActionMap structureActions = new ActionMap();
     	AbstractAction exactSearchAction =  new DbExactSearchAction(dbadminData,frame,"Exact search");
     	AbstractAction subSearchAction =  new DbSubstructureSearchAction(dbadminData,frame,"Substructure");
     	AbstractAction simSearchAction =  new DbSimilaritySearchAction(dbadminData,frame,"Similarity");
     	
     	structureActions.put("Exact search",exactSearchAction);
     	structureActions.put("Substructure search",subSearchAction);
     	structureActions.put("Similarity search",simSearchAction);
     	structureActions.put("CurrentStructure",new DbStructureToQuery(dbadminData,frame));
     	ActionFactory.setParentActions(structureActions,structureActions);		
     	dbadminData.setStructureActions(structureActions);
     	
     	DbDescriptorsSearchAction descriptorsSearchAction = new DbDescriptorsSearchAction(dbadminData,frame,"Search by descriptors");
     	ActionMap descriptorsActions = new ActionMap();
     	descriptorsActions.put("Search by descriptors",descriptorsSearchAction);
     	dbadminData.setDescriptorActions(descriptorsActions);
     	
     	DbExperimentsSearchAction experimentSearchAction = new DbExperimentsSearchAction(dbadminData,frame,"Search by experimental data");
     	ActionMap experimentActions = new ActionMap();
     	experimentActions.put("Search by experiments",experimentSearchAction);
		experimentActions.put("Experiments by Study", new ExperimentsByStudyAction(dbadminData,frame,"Experiments by Study"));
     	dbadminData.setExperimentsActions(experimentActions);
     	
     	SelectDatasetAction datasetAction = new SelectDatasetAction(dbadminData,frame,"Dataset");
     	
     	//add all actions to the toolbar
     	JToolBar toolbar = new JToolBar();
     	toolbar.add(openAction);
     	toolbar.add(statusAction);
     	toolbar.add(resultsAction);
     	toolbar.add(datasetAction);
     	toolbar.add(optionsAction);
     	toolbar.add(exportToFile);

     	//results panel
     	CompoundPanel panel = new CompoundPanel(dbadminData.getMolecules(),null, Color.white,Color.black,JSplitPane.VERTICAL_SPLIT);
     	panel.setPreferredSize(new Dimension(300,300));
     	panel.setBorder(BorderFactory.createTitledBorder("Results"));
     	
     	//Query panel
     	JTabbedPane queryPanel = new JTabbedPane();
     	queryPanel.setBorder(BorderFactory.createTitledBorder("Query"));
     	
     	StructureQueryPanel structurePanel  = new StructureQueryPanel(dbadminData.getQueries(),dbadminData.getStructureActions());
     	queryPanel.addTab("Structure query",structurePanel);
     	
     	//Descriptor query panel
     	DescriptorQueryPanel descriptorsQueryPanel = new DescriptorQueryPanel(
				(DescriptorQueryList) dbadminData.getDescriptors(),dbadminData.getDescriptorActions(),false);
     	queryPanel.addTab("Descriptors query",descriptorsQueryPanel);

     	//Experiments, study query panel
     	ExperimentsQueryPanel experimentsQueryPanel = new ExperimentsQueryPanel(
				(ExperimentQuery) dbadminData.getExperiments(),
				(ExperimentConditionsQuery) dbadminData.getStudyConditions(),
				dbadminData.getExperimentsActions());
     	queryPanel.addTab("Experiments query",experimentsQueryPanel);
     	
     	
		AmbitStatusBar statusBar = new AmbitStatusBar(new Dimension(100,24));
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		dbadminData.getJobStatus().addObserver((AmbitStatusBar)statusBar);
		if (dbadminData.getBatchStatistics() instanceof Observable)
			((Observable)dbadminData.getBatchStatistics()).addObserver((AmbitStatusBar)statusBar);

		
		
     	JPanel mainPanel = new JPanel(new GridLayout(3,1));
     	//Container mainPanel = frame.getContentPane();
     	mainPanel.setLayout(new BorderLayout());
     	mainPanel.setPreferredSize(new Dimension(800,600));

     	
     	mainPanel.add(toolbar,BorderLayout.NORTH);
     	mainPanel.add(panel,BorderLayout.CENTER);
     	mainPanel.add(queryPanel,BorderLayout.EAST);
     	mainPanel.add(statusBar,BorderLayout.SOUTH);
     	
     	
     	//show everything in a dialog box
     	JOptionPane.showMessageDialog(null,mainPanel,"",JOptionPane.PLAIN_MESSAGE,null);
     	
    }    
}

