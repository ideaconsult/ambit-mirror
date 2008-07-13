/* ActionFactory.java
 * Author: Nina Jeliazkova
 * Date: 2006-5-1 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import ambit.database.aquire.AquireSearchAction;
import ambit.ui.UITools;
import ambit.ui.actions.dbadmin.DBGenerator;
import ambit.ui.actions.dbadmin.DbBatchImportAction;
import ambit.ui.actions.dbadmin.DbBatchImportExperiments;
import ambit.ui.actions.dbadmin.DbBatchImportQSARModels;
import ambit.ui.actions.dbadmin.DbConnectionStatusAction;
import ambit.ui.actions.dbadmin.DbCreateDatabaseAction;
import ambit.ui.actions.dbadmin.DbCreateUserAction;
import ambit.ui.actions.dbadmin.DbOpenAction;
import ambit.ui.actions.dbadmin.DbStatisticsAction;
import ambit.ui.actions.dbadmin.PackUniqueSMILES;
import ambit.ui.actions.dbadmin.StartMySQLAction;
import ambit.ui.actions.dbadmin.StopMySQLAction;
import ambit.ui.actions.file.FileBatchProcessingAction;
import ambit.ui.actions.file.FileExportQueryAction;
import ambit.ui.actions.file.FileNewAction;
import ambit.ui.actions.file.FileOpenAction;
import ambit.ui.actions.file.LoadFragmentsAction;
import ambit.ui.actions.file.SaveFragmentsAction;
import ambit.ui.actions.process.CalculateMOPACAction;
import ambit.ui.actions.process.DBCalculateAtomDistancesAction;
import ambit.ui.actions.process.DBCalculateDescriptorsAction;
import ambit.ui.actions.process.MoleculeEditAction;
import ambit.ui.actions.search.DatasetSearchAction;
import ambit.ui.actions.search.DbAliasSearchAction;
import ambit.ui.actions.search.DbCASSearchAction;
import ambit.ui.actions.search.DbCombinedSearchAction;
import ambit.ui.actions.search.DbDescriptorsSearchAction;
import ambit.ui.actions.search.DbExactSearchAction;
import ambit.ui.actions.search.DbFormulaSearchAction;
import ambit.ui.actions.search.DbNameSearchAction;
import ambit.ui.actions.search.DbSMILESSearchAction;
import ambit.ui.actions.search.DbSimilaritySearchAction;
import ambit.ui.actions.search.DbSmartsSearchAction;
import ambit.ui.actions.search.DbStructureToQuery;
import ambit.ui.actions.search.DbSubstructureSearchAction;
import ambit.ui.actions.search.QueryOptionsAction;
import ambit.ui.actions.search.SelectDatasetAction;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-1
 */
public class ActionFactory {

    public static void createFileActions(ActionMap actions, Object userData, JFrame mainFrame) {
		actions.put("New",new FileNewAction(null,userData,mainFrame,"New"));
		actions.put("Open",new FileOpenAction(userData,mainFrame,"Open"));
		createSaveExitActions(actions, userData, mainFrame);
    }
    public static void createSaveExitActions(ActionMap actions, Object userData, JFrame mainFrame) {
		actions.put("Exit",new ExitAction(userData,mainFrame,"Exit"));
    }    
    public static void createEditActions(ActionMap actions, Object userData, JFrame mainFrame) {
		actions.put("Edit",new MoleculeEditAction(null,userData,mainFrame,"Structure diagram editor"));
    }
    public static void createDescriptorsActions(ActionMap actions, Object userData, JFrame mainFrame) {
		String c = "Search by descriptors and distance between atoms";
		actions.put(c,new DbDescriptorsSearchAction(userData,mainFrame,"Search"));
    }
    public static void createSearchActions(ActionMap actions, Object userData, JFrame mainFrame) {
		actions.put("0-CAS RN search",new DbCASSearchAction(userData,mainFrame,"CAS RN"));
		actions.put("1-NAME",new DbNameSearchAction(userData,mainFrame,"Chemical name"));
        actions.put("2-ALIAS",new DbAliasSearchAction(userData,mainFrame,"Aliases"));
        
		actions.put("3-SMILES",new DbSMILESSearchAction(userData,mainFrame,"SMILES"));
		actions.put("4-Molecular formula",new DbFormulaSearchAction(userData,mainFrame,"Molecular formula"));
		actions.put("5-Advanced search",new DbCombinedSearchAction(userData,mainFrame));
        actions.put("6-Search AQUIRE",new AquireSearchAction(userData,mainFrame));
		/*
		actions.put("Advanced search",new AmbitAction(userData,mainFrame,"Structure search",UITools.createImageIcon("ambit/ui/images/search.png")) {
			public void actionPerformed(ActionEvent arg0) {
				putValue(AbstractAction.SHORT_DESCRIPTION, "Search by structure (exact, substructure,similarity), by descriptors and by experimental data");
				JFrame q = DbQueryOptionsPanel.getDbQueryOptions((AmbitDatabaseToolsData)userData);
				AmbitDatabase.centerScreen(q);
				q.setVisible(true);
			}
		});
		*/
		actions.put(DatasetSearchAction.caption,new DatasetSearchAction(userData,mainFrame));
		
    }
    public static void createStructureSearchActions(ActionMap actions, Object userData, JFrame mainFrame) {
		actions.put("Exact search",new DbExactSearchAction(userData,mainFrame,"Exact search"));
		actions.put("Substructure search",new DbSubstructureSearchAction(userData,mainFrame,"Substructure"));
		actions.put("Similarity search",new DbSimilaritySearchAction(userData,mainFrame,"Similarity"));
		actions.put("CurrentStructure",new DbStructureToQuery(userData,mainFrame));
		
    }
    
    public static void createSMARTSSearchActions(ActionMap actions, Object userData, JFrame mainFrame) {
		actions.put("SMARTS",new DbSmartsSearchAction(userData,mainFrame,"Search"));
		actions.put("SMARTS Open",new LoadFragmentsAction(userData,mainFrame));
		actions.put("SMARTS Save",new SaveFragmentsAction(userData,mainFrame));
		
    }    
    
    public static void createOptionsActions(ActionMap actions, Object userData, JFrame mainFrame) {
		actions.put("Dataset",new SelectDatasetAction(userData,mainFrame,"Dataset"));
		//actions.put("Results output",new DbResultsDestinationAction(userData,mainFrame,"Results Output"));
		//included into QueryOptionsAction
		actions.put("Similarity method",new QueryOptionsAction(userData,mainFrame,"Options"));
    }    
    
    
  
    
    public static void createBatchProcessingActions(ActionMap actions, Object userData, JFrame mainFrame) {
    	actions.put("Batch",new FileBatchProcessingAction(userData,mainFrame,"Batch search"));
    }

    public static void createMySQLActions(ActionMap actions, Object userData, JFrame mainFrame) {
    	actions.put("Create user",new DbCreateUserAction(userData,mainFrame,"CreateUser"));
		actions.put("Create database",new DbCreateDatabaseAction(userData,mainFrame,"Create database"));    	
		actions.put("StartMySQL",new StartMySQLAction(userData,mainFrame));        
		actions.put("StopMySQL",new StopMySQLAction(userData,mainFrame));
    }        
    public static void createDatabaseActions(ActionMap actions, Object userData, JFrame mainFrame) {
        actions.put("Import compounds",new DbBatchImportAction(userData,mainFrame));
        actions.put("Import descriptors",new AmbitAction(userData,mainFrame,"Import descriptors",UITools.createImageIcon("ambit/ui/images/chart_bar.png")) {
        	public void actionPerformed(ActionEvent arg0) {
        		JOptionPane.showMessageDialog(mainFrame,"Use \"Import compounds\" menu and select descriptors to be imported when asked.");
        	}
        });
        actions.put("Import experimental data",new DbBatchImportExperiments(userData,mainFrame));
        actions.put("Import QSAR models",new DbBatchImportQSARModels(userData,mainFrame,"Import QSAR models"));      
        
    }
    public static void createDatabaseConnectActions(ActionMap actions, Object userData, JFrame mainFrame) {
        actions.put("Connect",new DbOpenAction(userData,mainFrame));
        actions.put("Get status",new DbConnectionStatusAction(userData,mainFrame));
        actions.put("Statistics",new DbStatisticsAction(userData,mainFrame));        
    }
    public static void createDatabaseAdminActions(ActionMap actions, Object userData, JFrame mainFrame) {
        actions.put("Generate",new DBGenerator(userData,mainFrame));
		actions.put("Descriptors",new DBCalculateDescriptorsAction(userData,mainFrame));
		actions.put("MOPACdb",new CalculateMOPACAction(userData,mainFrame));        
        //actions.put("GenerateSmiles",new DbHousekeeping(userData,mainFrame));
		actions.put("Distances",new DBCalculateAtomDistancesAction(userData,mainFrame));        

		actions.put("Clean up database",new PackUniqueSMILES(userData,mainFrame,"Clean up database"));
		
    	/*
    	actions.put("Create user",new DbCreateUserAction(userData,mainFrame,"CreateUser"));
		actions.put("Create database",new DbCreateDatabaseAction(userData,mainFrame,"Create database"));
		actions.put("Delete database",new DbDropDatabaseAction(userData,mainFrame,"Delete database"));
		*/
		
    }        
    public static void addToMenu(ActionMap actions,JMenu menu) {
        Object[] o = actions.keys();
        for (int i=0; i < o.length;i++)
            menu.add(actions.get(o[i]));
    }
    public static void setParentActions(ActionMap actions,ActionMap parentActions) {
        Object[] o = actions.keys();
        for (int i=0; i < o.length;i++) {
            
            Object a = actions.get(o[i]);
            parentActions.put(o[i],(Action)a); 
            if (a instanceof AmbitAction) 
                ((AmbitAction) a).setActions(parentActions);
        }    
    }    
}
