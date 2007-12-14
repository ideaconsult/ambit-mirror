/* AmbitDbApp.java
 * Author: Nina Jeliazkova
 * Date: 2005-3-20 
 * Revision: 1.0 
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
package ambit.applications;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import ambit.data.AmbitList;
import ambit.data.AmbitObject;
import ambit.data.AmbitUser;
import ambit.data.descriptors.DescriptorFactory;
import ambit.data.experiment.DefaultTemplate;
import ambit.data.experiment.Experiment;
import ambit.data.experiment.ExperimentFactory;
import ambit.data.experiment.Study;
import ambit.data.literature.AuthorEntry;
import ambit.data.literature.JournalEntry;
import ambit.data.literature.ReferenceFactory;
import ambit.data.model.ModelFactory;
import ambit.data.molecule.AmbitPoint;
import ambit.data.molecule.Compound;
import ambit.data.molecule.CompoundFactory;
import ambit.data.molecule.SourceDataset;
import ambit.database.DbAdmin;
import ambit.database.DbConnection;
import ambit.domain.AllData;
import ambit.domain.DataCoverageDescriptors;
import ambit.domain.QSARDataset;
import ambit.domain.QSARDatasetFactory;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;
import ambit.ui.AmbitColors;
import ambit.ui.CoreApp;
import ambit.ui.query.AmbitDbObjectPanel;
import ambit.ui.query.QDbConnectionDialog;


/**
 * A GUI application to view, insert and modify database records  
 * @author Nina Jeliazkova <br>
 * @deprecated Use {@link ambit.applications.dbadmin.AmbitDatabase}
 * <b>Modified</b> 2005-4-7
 */
public class AmbitDbApp extends CoreApp {
	protected String host="localhost";
	protected String port="33060";
	protected String database="ambit";
	protected String user="";
	protected String password="";
	protected DbConnection conn = null;	
	
	protected ArrayList actions;
	protected AmbitDbObjectPanel panel;
	
	protected AmbitObject object = null;
	
	/**
	 * @param title
	 * @param width
	 * @param height
	 */
	public AmbitDbApp(String title, int width, int height,String args[]) {
		super(title, width, height,args);
		mainFrame.invalidate();
	}

	protected void initializeActions() {
		int i =0;
		actions = new ArrayList();
		AbstractAction action = new AbstractAction("Drop tables") { 
			public void actionPerformed(ActionEvent e) {
				 if (conn != null) {
				 	try {
				 	DbAdmin dba = new DbAdmin(conn);
				 		
				 	//dba.dropQSAR_REFSTables();
				 	dba = null;
				 	
				 	} catch (AmbitException x) {
				 		x.printStackTrace();
				 	}
				 }
			}
		}; 
		action.putValue("TYPE","System");			
		actions.add(action);
		action = new AbstractAction("Create tables") { 
			public void actionPerformed(ActionEvent e) {
				 if (conn != null) {
				 	try {
					 	DbAdmin dba = new DbAdmin(conn);
				 		
					 	dba.createTables("ambittest");
					 	dba = null;				 	
				 	} catch (AmbitException x) {
				 		x.printStackTrace();
				 	}

				 }
			}
		}; 
		action.putValue("TYPE","System");			
		actions.add(action);
		createModelActions();
		createQSARActions();		
		createExperimentActions();
		createReferenceActions();
		createDescriptorActions();
		createMoleculeActions() ;		
		createUserActions() ;		
	}
	protected void createDescriptorActions() {
		AbstractAction action = new AbstractAction("New") { 
			public void actionPerformed(ActionEvent e) {
				setObject(DescriptorFactory.createEmptyDescriptor());
			}	
		};
		action.putValue("TYPE","Descriptors");		
		actions.add(action);
		
		action = new AbstractAction("Demo: logP") { 
			public void actionPerformed(ActionEvent e) {
				setObject(DescriptorFactory.createLogP(ReferenceFactory.createKOWWinReference()));
			}	
		};
		action.putValue("TYPE","Descriptors");		
		actions.add(action);
		
	}
	protected void createMoleculeActions() {
		AbstractAction action = new AbstractAction("New") { 
			public void actionPerformed(ActionEvent e) {
				setObject(new Compound());			
			}
		};
		action.putValue("TYPE","Compounds");	
		actions.add(action);
		
		action = new AbstractAction("Demo : studyList of compounds") { 
			public void actionPerformed(ActionEvent e) {
				AmbitList al = new AmbitList();
				al.addItem(CompoundFactory.createBenzene());
				al.addItem(CompoundFactory.create4Aminobiphenyl());
				al.addItem(CompoundFactory.create2Aminofluorene());
				al.addItem(CompoundFactory.create2Aminonaphthalene());				
				setObject(al);
			}
		};
		action.putValue("TYPE","Compounds");	
		actions.add(action);	
		
		action = new AbstractAction("New source dataset") { 
			public void actionPerformed(ActionEvent e) {
				setObject(new SourceDataset());			
			}
		};
		action.putValue("TYPE","Compounds");	
		actions.add(action);
		
		
	}
	protected void createQSARActions() {
		AbstractAction action = new AbstractAction("New") { 
			public void actionPerformed(ActionEvent e) {
				setObject(new QSARDataset());			
			}
		};
		action.putValue("TYPE","QSAR");	
		actions.add(action);
		action = new AbstractAction("Demo: Debnath dataset") { 
			public void actionPerformed(ActionEvent e) {
			    try {
					QSARDataset ds = QSARDatasetFactory.createDebnathDataset("d:\\src\\ambit\\data\\Debnath.csv");
					DataCoverageDescriptors coverage = new DataCoverageDescriptors(2);
					ds.estimateCoverage(coverage);
					
					setObject(ds);
			    } catch (AmbitException x) {
			        JOptionPane.showMessageDialog(null,x.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			    }
			}
		};
		action.putValue("TYPE","QSAR");	
		actions.add(action);



	}	
	protected void createModelActions() {
		AbstractAction action = new AbstractAction("New") { 
			public void actionPerformed(ActionEvent e) {
				setObject(ModelFactory.createEmptyModel());			
			}
		};
		action.putValue("TYPE","Models");	
		actions.add(action);


		action = new AbstractAction("Demo: Debnath model") { 
			public void actionPerformed(ActionEvent e) {
			    try {
				setObject(ModelFactory.createDebnathMutagenicityQSAR());
			    } catch (Exception x) {
			        
			    }
			}
		};
		action.putValue("TYPE","Models");	
		actions.add(action);

		
		action = new AbstractAction("Demo: data") { 
			public void actionPerformed(ActionEvent e) {
				AllData d = new AllData();
				setObject(d);			
			}
		};
		action.putValue("TYPE","Models");	
		actions.add(action);
		
		action = new AbstractAction("New point") { 
			public void actionPerformed(ActionEvent e) {
				AmbitPoint d = new AmbitPoint(CompoundFactory.createBenzene());
				d.setXvalues(new double[4]);
				d.setCoverage(0.99);
				//d.setStructuralCoverage(0.59);
				setObject(d);			
			}
		};
		action.putValue("TYPE","Models");	
		actions.add(action);			
	}
	protected void createUserActions() {
		
		AbstractAction action = new AbstractAction("Current User") { 
			public void actionPerformed(ActionEvent e) {
				if (conn != null)
					setObject(conn.getUser());			
			}
		};
		action.putValue("TYPE","Users");	
		actions.add(action);
		
		action = new AbstractAction("New") { 
			public void actionPerformed(ActionEvent e) {
				setObject(new AmbitUser());			
			}
		};
		action.putValue("TYPE","Users");	
		actions.add(action);


		action = new AbstractAction("Demo: user") { 
			public void actionPerformed(ActionEvent e) {
				setObject(new AmbitUser("nina"));			
			}
		};
		action.putValue("TYPE","Users");	
		actions.add(action);
		
	}	
	protected void createExperimentActions() {
		AbstractAction action = new AbstractAction("New") { 
			public void actionPerformed(ActionEvent e) {
				setObject(new Experiment());
			}	
		};
		action.putValue("TYPE","Experiments");		
		actions.add(action);
		
		action = new AbstractAction("Demo: LC50") { 
			public void actionPerformed(ActionEvent e) {
			    try {
				setObject(ExperimentFactory.createBenzeneLC50());
			    } catch (Exception x) {
			        
			    }

			}	
		};
		action.putValue("TYPE","Experiments");		
		actions.add(action);
		
		action = new AbstractAction("Demo: Glende Experiments") { 
			public void actionPerformed(ActionEvent e) {
			    try {
				setObject(ExperimentFactory.createGlendeExperiments());
			    } catch (Exception x) {
			        
			    }

			}	
		};
		action.putValue("TYPE","Experiments");		
		actions.add(action);

		action = new AbstractAction("New") { 
			public void actionPerformed(ActionEvent e) {
				setObject(new Study("",new DefaultTemplate("default")));
			}	
		};
		action.putValue("TYPE","Study");		
		actions.add(action);		
		
		action = new AbstractAction("Demo: LC50 Fish") { 
			public void actionPerformed(ActionEvent e) {
			    try {
				setObject(ExperimentFactory.createLC50Fish96hr());
			    } catch (Exception x) {
			        
			    }
			}	
		};
		action.putValue("TYPE","Study");		
		actions.add(action);		
/*
		action = new AbstractAction("New") { 
			public void actionPerformed(ActionEvent e) {
				setObject(new Species("",""));
			}	
		};
		action.putValue("TYPE","Species");		
		actions.add(action);		
		
		action = new AbstractAction("Demo: Mouse") { 
			public void actionPerformed(ActionEvent e) {
				setObject(new Species("Mouse","M"));
			}	
		};
		action.putValue("TYPE","Species");		
		actions.add(action);		
	*/	
	}
	protected void createReferenceActions() {
		AbstractAction action = new AbstractAction("New") { 
			public void actionPerformed(ActionEvent e) {
				setObject(ReferenceFactory.createEmptyReference());
			}	
		};
		action.putValue("TYPE","References");		
		actions.add(action);
		
		action = new AbstractAction("Demo: BCFWIN reference") { 
			public void actionPerformed(ActionEvent e) {
				setObject(ReferenceFactory.createBCFWinReference());
			}	
		};
		action.putValue("TYPE","References");		
		actions.add(action);

		action = new AbstractAction("Demo: KOWWIN reference") { 
			public void actionPerformed(ActionEvent e) {
				setObject(ReferenceFactory.createKOWWinReference());
			}	
		};
		action.putValue("TYPE","References");		
		actions.add(action);
		
				
		
		action = new AbstractAction("New") { 
			public void actionPerformed(ActionEvent e) {
				setObject(new AuthorEntry(""));
			}	
		};
		action.putValue("TYPE","Authors");		
		actions.add(action);		
		
		action = new AbstractAction("Demo: Debnath A.K.") { 
			public void actionPerformed(ActionEvent e) {
				setObject(new AuthorEntry("Debnath A.K."));
			}	
		};
		action.putValue("TYPE","Authors");		
		actions.add(action);
		

		action = new AbstractAction("New") { 
			public void actionPerformed(ActionEvent e) {
				JournalEntry journal = new JournalEntry("","");
				setObject(journal);
			}	
		};
		action.putValue("TYPE","Journals");		
		actions.add(action);
		
		action = new AbstractAction("Demo: Mutation Res.") { 
			public void actionPerformed(ActionEvent e) {
				JournalEntry journal = new JournalEntry("Mutation Res.","Mutation Research");
				journal.setPublisher("Elsevier");
				setObject(journal);
			}	
		};
		action.putValue("TYPE","Journals");		
		actions.add(action);
		
	}
	/* (non-Javadoc)
	 * @see ambit.ui.CoreApp#initSharedData()
	 */
	protected void initSharedData(String[] args) {
	    //TODO initshareddata AmbitDatabase
		
	}
    protected void doClose() {
		 if (conn != null) try { conn.close();}
		 catch (SQLException ex) { ex.printStackTrace();};
		 conn = null;
		 super.doClose();
   }
	
	
	protected void setObject(AmbitObject ao) {
		//actionPanel.setAO(ao);
		panel.setAO(ao);
		object = null;
		object = ao;
	}

	/* (non-Javadoc)
	 * @see ambit.ui.CoreApp#createMenuBar()
	 */
	protected JMenuBar createMenuBar() {
		   initializeActions();
	       JMenuItem menuItem = null;
	       JMenuBar menuBar = new JMenuBar();
	       menuBar.setPreferredSize(new Dimension(200,18));
	       menuBar.add(createQueryMenu("Database"));
	       menuBar.add(createMenu("Users"));
	       menuBar.add(createMenu("QSAR"));	       
	       menuBar.add(createMenu("Models"));
	       JMenuItem subMenu = new JMenu("Experiments");
	       subMenu.add(createMenu("Experiments"));	       
	       subMenu.add(createMenu("Study"));
	       subMenu.add(createMenu("Species"));	       
	       menuBar.add(subMenu);
	       subMenu = new JMenu("References");
	       subMenu.add(createMenu("References"));
	       subMenu.add(createMenu("Authors"));
	       subMenu.add(createMenu("Journals"));	       
	       menuBar.add(subMenu);
	       menuBar.add(createMenu("Descriptors"));	       
	       menuBar.add(createMenu("Compounds"));
	       menuBar.add(createMenu("System"));
	       
	       //menuBar.add();       
	       return menuBar;
		}
	protected JMenu createMenu(String type) {
		JMenu menu = new JMenu(type);
		for (int i = 0; i < actions.size(); i++) {
			AbstractAction a = (AbstractAction) actions.get(i);
			Object o = (a).getValue("TYPE");
			if ( (o != null) && type.equals(o))  
				menu.add(a);
		}
		return menu;
	}
	protected JMenu createQueryMenu(String caption) {
		String [] modelActions = {"Open","Exit"};
		Action action = null;
		JButton button = null;
        JMenu modelMenu = new JMenu(caption);
        for (int i = 0; i < modelActions.length; i++) {
        	switch (i) {
        	case 1: {
				action = new AbstractAction(modelActions[i]) { 
					public void actionPerformed(ActionEvent e) {

						 if (conn != null) try { conn.close();}
						 catch (SQLException ex) { ex.printStackTrace();};
						 conn = null;
		        		 Runtime.getRuntime().runFinalization();						 
		        		 Runtime.getRuntime().exit(0);
		        		 
					}
				};	
        		 break;
        	}
			case 0: {
				action = new AbstractAction(modelActions[i]) { 
					public void actionPerformed(ActionEvent e) {
						QDbConnectionDialog d = new QDbConnectionDialog(mainFrame,true);
						System.err.println(host);
						d.setHost(host);
						d.setDatabase(database);
						d.setUser(user);
						d.setPassword(password);
						try {
						d.centerParent();
						} catch (Exception x) {
						    
						}
						d.setVisible(true);
						if (d.getResult() == JOptionPane.OK_OPTION) {
							host = d.getHost();
							database = d.getDatabase();
							user = d.getUser();
							password = d.getPassword();

							if (d.isModified())
								if (conn != null) {
									try {	conn.close();
									} catch (SQLException ex) {
									//TODO SQLexception on close
									ex.printStackTrace();
									}
								conn = null;
							}
							if (conn == null) {
								conn = openConnection();
							}
							
						}
						d = null;
					}
					
				};
				break; }
			
			}
        	
            modelMenu.add(action);
//            button = toolBar.add(action);
        }
        return modelMenu;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		// TODO actionperformed

	}

	/* (non-Javadoc)
	 * @see ambit.ui.CoreApp#createWidgets(javax.swing.JFrame, javax.swing.JPanel)
	 */
	protected void createWidgets(JFrame aFrame, JPanel aPanel) {
		//this are the mainframe and the main panel
		Color back = new Color(255,255,255);
		Color fore = AmbitColors.DarkClr;
		toolBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,Color.gray,Color.lightGray));		
		/**
		actionPanel = new QSearchPanel("Search",back,fore);
		actionPanel.setPreferredSize(new Dimension(110,440));
		actionPanel.setBorder(BorderFactory.createEtchedBorder(Color.black,AmbitColors.DarkClr));
		actionPanel.setVisible(false);
		aPanel.add(actionPanel);
		*/
		
		aPanel.setLayout(new BoxLayout(aPanel,BoxLayout.LINE_AXIS));
		panel = createPanel(back,fore,null);
		panel.setPreferredSize(new Dimension(330,440));
		aPanel.add(panel);
		
		
	}	
	
	protected AmbitDbObjectPanel createPanel(Color back, Color fore, 
			AmbitObject object) {

		AmbitDbObjectPanel panel = new AmbitDbObjectPanel(caption,back,fore,
				object,conn) ;
		return panel;
	}
	protected DbConnection openConnection() {
		DbConnection c = new DbConnection(host,port,database,user,password);
		try {
			c.open();

			System.out.println("\nConnection opened: host\t"+host+"\tdatabase\t"+database+"\tuser\t"+user);
			panel.setConnection(c);
			//actionPanel.setConn(c.getConn());			
			//actionPanel.setVisible(true);
		} catch (AmbitException ex) {
			showMessage(ex.toString(),"Error connecting to database!");
			ex.printStackTrace();
			c = null;
		}
		return c;
	}

	public static void main(final String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                AmbitDbApp app = new AmbitDbApp("Ambit DATABASE"+AmbitCONSTANTS.version,580,580,args);
            }
        });
   }
}
