/* AmbitDiscoveryApp.java
 * Author: Nina Jeliazkova
 * Date: 2005-1-18 
 * Revision: 0.4 
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
package ambit.applications.discovery;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.border.EtchedBorder;

import ambit.data.molecule.CompoundsList;
import ambit.domain.ADomainMethodType;
import ambit.domain.AtomEnvironmentDistanceType;
import ambit.domain.DataModule;
import ambit.domain.DatasetCenterPoint;
import ambit.domain.FingerprintDistanceType;
import ambit.domain.QSARDataset;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;
import ambit.ui.AboutAction;
import ambit.ui.AmbitColors;
import ambit.ui.CoreApp;
import ambit.ui.UITools;
import ambit.ui.data.CompoundsListPanel;
import ambit.ui.domain.ADActionPanel;
import ambit.ui.domain.ADDataSetDialog;
import ambit.ui.domain.ADDataSetPanel;
import ambit.ui.domain.ADDataSetsPanel;
import ambit.ui.domain.ADGUIListener;
import ambit.ui.domain.ADPanel;
import ambit.ui.domain.ADStatsPanel;


/**
 * A GUI application to estimate applicability domain 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitDiscoveryApp extends CoreApp {
	protected ADGUIListener adGUI;
	protected DataModule dm;
	/**
	 * @param title
	 */
	public AmbitDiscoveryApp(String title, int w , int h,String[] args ) {
		super(title, w, h,args);
		centerScreen();
        
		Package adPackage = Package.getPackage("ambit.applications");
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
	
		System.out.println(version);
		if (version == null) version = AmbitCONSTANTS.version;
		
		//mainFrame = new JFrame(pTitle+version);
		setCaption(pTitle+version);
	}

	protected void initSharedData(String[] args) {
		dm = new DataModule();
		adGUI = new ADGUIListener(dm);
		dm.modelData.addAmbitObjectListener(adGUI.getModelListener());
		dm.testData.addListListener(adGUI.getTestListener());		
	}
	protected JMenu createVisualMenu(String caption) {
		String [] fileActions = {"Scatter plot","Density plot"};
		Action action = null;
		JButton button = null;
        JMenu fileMenu = new JMenu("Visualisations");
        for (int i = 0; i < fileActions.length; i++) {
        	switch (i) {
			case 0: {
				action = new AbstractAction(fileActions[i]) { 
					public void actionPerformed(ActionEvent e) {
						//to start with the chart thing ...
						adGUI.showChart("Scatter plot",
						        dm.modelData,
						        dm.testData);
//						adGUI.drawChart(dm.modelData);						
//						adGUI.drawChart(dm.testData);						
					}
				}; break; }	
			case 1: {
				action = new AbstractAction(fileActions[i]) { 
					public void actionPerformed(ActionEvent e) {
						//to start with the chart thing ...
						adGUI.drawDensity(dm.modelData);
					}
				};
				//button = toolBar.add(action);
				break;
				
				}
		}	
			fileMenu.add(action);                    
            
        }
        return fileMenu;
	}

	protected JMenu createTestMenu(String caption) {
		String [] fileActions = {"Edit","View compounds","View chart"};
		Action action = null;
		JButton button = null;
        JMenu fileMenu = new JMenu(caption);
        for (int i = 0; i < fileActions.length; i++) {
        	switch (i) {
			case 0: {
				action = new AbstractAction(fileActions[i]) { 
					public void actionPerformed(ActionEvent e) {
						QSARDataset ds = (QSARDataset)dm.testData.getSelectedItem();
						if (ds != null) {
							ADDataSetDialog.editDataset(ds,"Edit selected test data set");
							/*
							AmbitObjectDialog dlg = new AmbitObjectDialog(mainFrame,
								"Edit test data set",true,ds);
							dlg.centerParent(mainFrame);
							dlg.setVisible(true);
							if (dlg.getResult() == JOptionPane.OK_OPTION) {
								dm.testData.fireAmbitListEvent();
							}
							*/	
							dm.testData.fireAmbitListEvent();
							
					}
					}
				}; break; }
			case 1: {
				action = new AbstractAction(fileActions[i]) { 
					public void actionPerformed(ActionEvent e) {
						QSARDataset ds = (QSARDataset)dm.testData.getSelectedItem();
						if (ds != null) {
						    CompoundsListPanel scrollPane = new CompoundsListPanel(ds.getData(),3,new Dimension(150,150));
						    JOptionPane.showMessageDialog(mainFrame, scrollPane,"Test dataset: " +ds.getName(),JOptionPane.PLAIN_MESSAGE,null);

						}
					}
				}; break; }
				
			case 2: {
				action = new AbstractAction(fileActions[i]) { 
					public void actionPerformed(ActionEvent e) {
						adGUI.showChart("scatter plot",
						        dm.modelData,dm.testData);
//						adGUI.drawChart(dm.modelData);						
//						adGUI.drawChart(dm.testData);						
					}
				}; break; }
			//TODO update random data
			case 3: {
				action = new AbstractAction(fileActions[i]) { 
					public void actionPerformed(ActionEvent e) {
						dm.randomTestData();
					}
				}; break; }			
			}	
            fileMenu.add(action);
            //button = toolBar.add(action);
        }
        return fileMenu;
	}
	protected JMenu createModelMenu(String caption) {
		String [] modelActions = {"Edit","View compounds","View chart"};
		Action action = null;
		JButton button = null;
        JMenu modelMenu = new JMenu(caption);
        for (int i = 0; i < modelActions.length; i++) {
        	switch (i) {
			case 0: {
				action = new AbstractAction(modelActions[i]) { 
					public void actionPerformed(ActionEvent e) {
						ADDataSetDialog.editDataset(dm.modelData,"Edit training data set");
						dm.modelData.fireAmbitObjectEvent();						
/*						
						AmbitObjectDialog dlg = new AmbitObjectDialog(mainFrame,
								"Edit model",true,dm.modelData);
						dlg.centerParent(mainFrame);
						dlg.setVisible(true);	
						if (dlg.getResult() == JOptionPane.OK_OPTION) {
							dm.modelData.fireAmbitObjectEvent();
						}
*/						
					}
				}; break; }
			case 1: {
				action = new AbstractAction(modelActions[i]) { 
					public void actionPerformed(ActionEvent e) {

					    CompoundsListPanel scrollPane = new CompoundsListPanel(dm.modelData.getData(),3,new Dimension(150,150));
					    JOptionPane.showMessageDialog(mainFrame,scrollPane,"Training dataset: " + dm.modelData.getName(),JOptionPane.PLAIN_MESSAGE,null);
					    /*
						AmbitObjectDialog dlg = AmbitObjectDialog.createAndShow(
								true,
								"Compounds (training data set)",
								mainFrame, dm.modelData.getData(),new Dimension(500,500));						
						if (dlg.getResult() == JOptionPane.OK_OPTION) {
							dm.modelData.fireAmbitObjectEvent();
						}
						*/
					}
				}; break; }				
			case 2: {
				action = new AbstractAction(modelActions[i]) { 
					public void actionPerformed(ActionEvent e) {
						adGUI.showChart("scatter plot",dm.modelData,dm.testData);
					}
				}; break; }
			//TODO update random data 
			case 3: {
				action = new AbstractAction(modelActions[i]) { 
					public void actionPerformed(ActionEvent e) {
						dm.randomData();
					}
				}; break; }
			
			}
        	
            modelMenu.add(action);
//            button = toolBar.add(action);
        }
        return modelMenu;
	}
	
	protected JMenu createHelpMenu(String caption) {
		String [] helpActions = {"Help","Java",
				"Demo : Debnath mutagenicity model",
				"Demo : test set for Debnath mutagenicity model",
				"Demo : Grammatica BCF model",
				"About"};
		Action action = null;
        JMenu modelMenu = new JMenu(caption);
        for (int i = 0; i < helpActions.length; i++) {
        	if ((i==2) || (i==4) || (i==5)) modelMenu.addSeparator();
        	switch (i) {
			case 0: {
				action = new AbstractAction(helpActions[i]) { 
					public void actionPerformed(ActionEvent e) {
						showToBeDoneMessage(e.getActionCommand());
					}
				}; break; }
			case 1: {
				action = new AbstractAction(helpActions[i]) { 
					public void actionPerformed(ActionEvent e) {
						showJavaInfo();
					}
				}; break; }
			case 2: {
				action = new AbstractAction(helpActions[i]) { 
					public void actionPerformed(ActionEvent e) {
					    try {
					        dm.createDemoModel(null);
					    } catch (AmbitException x) {
					        JOptionPane.showMessageDialog(mainFrame,x.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
					    }
					}
				}; break; }
			case 3: {
				action = new AbstractAction(helpActions[i]) { 
					public void actionPerformed(ActionEvent e) {
					    try {
						dm.createDemoTestset();
					    } catch (AmbitException x) {
					        JOptionPane.showMessageDialog(mainFrame,x.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
					    }
					}
				}; break; }
			case 4: {
				action = new AbstractAction(helpActions[i]) { 
					public void actionPerformed(ActionEvent e) {
					    try {
						dm.createDemoModel(null);
					    } catch (AmbitException x) {
					        JOptionPane.showMessageDialog(mainFrame,x.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
					    }
					}
				}; break; }		
			case 5: {
			    action = new AboutAction("About",null);
			}
        	}
            modelMenu.add(action);
            
//            button = toolBar.add(action);
        }
        return modelMenu;
	}
	
	
	protected JMenu createFileMenu(String caption) {
		String [] fileActions = {"New","Open","Save"};
		String [] subActions = {"Model (training set)","Test set"};		
		Action action = null;
		JButton button = null;
        JMenu fileMenu = new JMenu(caption);
        for (int f=0; f < fileActions.length; f++) {
        	JMenu subMenu = new JMenu(fileActions[f]);
        	fileMenu.add(subMenu);
            for (int i = 0; i < subActions.length; i++) {
            	switch (f) {
    			case 0: {
    				if (i==0)
    				action = new AbstractAction(subActions[i]) { 
    					public void actionPerformed(ActionEvent e) {
    						dm.createNewModel();
    					}
    				}; 
    				else 
        				action = new AbstractAction(subActions[i]) { 
    					public void actionPerformed(ActionEvent e) {
    						dm.newTestData(); 
    					}
    				};     					
    				break; }
    			case 1: {
    				if (i==0) 
    				    action = AmbitDiscoveryActionFactory.createFileOpenAction(dm,mainFrame);
    				  /*
    				action = new AbstractAction(subActions[i]) { 
    					public void actionPerformed(ActionEvent e) {
    					    try {
    						dm.openModel();
    					    } catch (AmbitException x) {
    					        JOptionPane.showMessageDialog(mainFrame,x);
    					    }
    					}
    				};
    				*/
    				else
        				action = new AbstractAction(subActions[i]) { 
    					public void actionPerformed(ActionEvent e) {
    						dm.openTestData();
    					}
    				};    					
    				break; }
    			case 2: {
    				if (i==0)
    				action = new AbstractAction(subActions[i]) { 
    					public void actionPerformed(ActionEvent e) {
    							if (dm.saveModel())
    							showMessage("Model saved","info");
    					}
    				}; else  
    				action = new AbstractAction(subActions[i]) { 
    					public void actionPerformed(ActionEvent e) {
    						if (dm.saveTestData())
							showMessage("Test data saved","info");    						
    					}
    				};     				
    				break; }
    			
    			}	
                subMenu.add(action);
                //if (f==1)
                //button = toolBar.add(action);
            }
        	
        }
        return fileMenu;
	}
	protected JMenu createCompoundsMenu(String caption) {
		String [] cActions = {"In domain","Out of domain"};
		
        JMenu methodMenu = new JMenu(caption);
        
        JMenuItem menuItem;
        ActionListener listener ;
        for (int f=0; f < cActions.length; f++) {
            menuItem = new JMenuItem(cActions[f]);
            menuItem.setSelected(f==1);
            switch(f) {
            case(0): {
            	listener = new ActionListener(){
            	public void actionPerformed(ActionEvent ae){
            		showCompoundsIn(dm.modelData);
            	}
            	};
            	break;
            }
            case(1): {
            	listener = new ActionListener(){
            	public void actionPerformed(ActionEvent ae){
            		showCompoundsOut(dm.modelData);
            	}
            	};
            	break;
            }
            default: { listener = null;};
            }
            menuItem.addActionListener(listener);
            methodMenu.add(menuItem);
        }    
        return methodMenu;
	}
	protected JMenu createTestCompoundsMenu(String caption) {
		String [] cActions = {"In domain","Out of domain"};
		
        JMenu methodMenu = new JMenu(caption);
        
        JMenuItem menuItem;
        ActionListener listener ;
        for (int f=0; f < cActions.length; f++) {
            menuItem = new JMenuItem(cActions[f]);
            menuItem.setSelected(f==1);
            switch(f) {
            case(0): {
            	listener = new ActionListener(){
            	public void actionPerformed(ActionEvent ae){
            		QSARDataset ds = (QSARDataset)dm.testData.getSelectedItem();
            		showCompoundsIn(ds);
            	}
            	};
            	break;
            }
            case(1): {
            	listener = new ActionListener(){
            	public void actionPerformed(ActionEvent ae){
            		QSARDataset ds = (QSARDataset)dm.testData.getSelectedItem();            		
            		showCompoundsOut(ds);
            	}
            	};
            	break;
            }
            default: { listener = null;};
            }
            menuItem.addActionListener(listener);
            methodMenu.add(menuItem);
        }    
        return methodMenu;
	}
	
	protected JMenu createMethodMenu(String caption) {
		ButtonGroup group = new ButtonGroup();
		
        JMenu methodMenu = new JMenu(caption);
        
        JRadioButtonMenuItem menuItem;
        ActionListener listener = new ActionListener(){
        	public void actionPerformed(ActionEvent ae){
			    try {
			        dm.setMethod(ae.getActionCommand());
				    } catch (AmbitException x) {
				        JOptionPane.showMessageDialog(mainFrame,x);
				    }
        		
        	}
        };        	
        for (int f=0; f < ADomainMethodType.methodName.length; f++) {
            menuItem = new JRadioButtonMenuItem(ADomainMethodType.methodName[f]);
            menuItem.setSelected(f==0);
            group.add(menuItem);
            menuItem.addActionListener(listener);
            menuItem.setEnabled((f != ADomainMethodType._modeMAHALANOBIS) &&
            		(f != ADomainMethodType._modeLEVERAGE));
            methodMenu.add(menuItem);
        }    
        return methodMenu;
	}	
	protected JMenu createFpMenu(String caption) {
		ButtonGroup group = new ButtonGroup();
		
        JMenu methodMenu = new JMenu(caption);
        
        JRadioButtonMenuItem menuItem;
        ActionListener listener = new ActionListener(){
        	public void actionPerformed(ActionEvent ae){
        		dm.setFingerPrintType(ae.getActionCommand());
        	}
        };        	
        for (int f=0; f < FingerprintDistanceType.fpComparison.length; f++) {
            menuItem = new JRadioButtonMenuItem(FingerprintDistanceType.fpComparison[f]);
            menuItem.setSelected(f==0);
            group.add(menuItem);
            menuItem.addActionListener(listener);
            methodMenu.add(menuItem);
        }    
        return methodMenu;
	}	
	protected JMenu createAEMenu(String caption) {
		ButtonGroup group = new ButtonGroup();
		
        JMenu methodMenu = new JMenu(caption);
        
        JRadioButtonMenuItem menuItem;
        ActionListener listener = new ActionListener(){
        	public void actionPerformed(ActionEvent ae){
        		dm.setAtomEnvironmentType(ae.getActionCommand());
        	}
        };        	
        for (int f=0; f < AtomEnvironmentDistanceType.fpComparison.length; f++) {
            menuItem = new JRadioButtonMenuItem(AtomEnvironmentDistanceType.fpComparison[f]);
            menuItem.setSelected(f==0);
            group.add(menuItem);
            menuItem.addActionListener(listener);
            methodMenu.add(menuItem);
        }    
        return methodMenu;
	}	
	protected JMenu createPreprocessingMenu(String caption) {
		String [] methodActions = {"Data standartisation","PCA","Box-Cox"};
		
        JMenu methodMenu = new JMenu(caption);
        
        JRadioButtonMenuItem menuItem;
        ActionListener listener ;
        for (int f=0; f < methodActions.length; f++) {
            menuItem = new JRadioButtonMenuItem(methodActions[f]);
            menuItem.setSelected(f==1);
            switch(f) {
            case(1): {
            	listener = new ActionListener(){
            	public void actionPerformed(ActionEvent ae){
            		dm.togglePCA();
            	}
            	};
            	break;
            }
            default: {
            	listener = new ActionListener(){
                	public void actionPerformed(ActionEvent ae){
                		//TODO boxcox, standardization
                	}
                	};
                menuItem.setEnabled(false);	
            }
            }
            menuItem.addActionListener(listener);
            methodMenu.add(menuItem);
        }    
        return methodMenu;
	}
	
	protected Action createThresholdMenu(String caption) {
		Action taction = new AbstractAction(caption) { 
			public void actionPerformed(ActionEvent e) {
				String[] possibilities = {"100%", "99%", "95%","75%"};
				double[] percents = {1,0.99,0.95,0.75};
				String deflt = "All";
				double val = dm.getThreshold();
			    for (int i = 0; i < percents.length; i++)
			    	if (val == percents[i]) {
			    		deflt = possibilities[i];
			    		break;
			    	}
				
				String s = (String)JOptionPane.showInputDialog(
				                    mainFrame,
				                    "Specify the percent of points \nin the training set,"
				                    + " which will determine \nthe domain of the model:",
				                    "Select AD threshold",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    possibilities,
				                    deflt);

				if ((s != null) && (s.length() > 0)) {
				    for (int i = 0; i < possibilities.length; i++)
				    	if (s.equals(possibilities[i])) {
				    		dm.setThreshold(percents[i]);
				    		break;
				    	}
				    return;
				}
				

			}
		};
		return taction;
	}
	
	protected Action createCenterMenu(String caption) {
		Action taction = new AbstractAction(caption) { 
			public void actionPerformed(ActionEvent e) {
				int val = dm.getCenterMode();
				String deflt = DatasetCenterPoint.centerMode[val];
				
				String s = (String)JOptionPane.showInputDialog(
				                    mainFrame,
				                    "Distances are calculated from a point to \n"
									+"a certain central point of the training data set.\n"
				                    +"This setting has no effect on <Ranges> and <Probability> methods\n"
									+"but could influence results for distance methods.",
				                    "Specify the central point for distance methods:",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    DatasetCenterPoint.centerMode,
									deflt);

				if ((s != null) && (s.length() > 0)) {
				    for (int i = 0; i < DatasetCenterPoint.centerMode.length; i++)
				    	if (s.equals(DatasetCenterPoint.centerMode[i])) {
				    		dm.setCenterMode(i);
				    		break;
				    	}
				}
				

			}
		};
		return taction;
	}
	
	/* (non-Javadoc)
	 * @see ambit.applications.CoreApp#createMenuBar()
	 */
    protected JMenuBar createMenuBar() {
        JMenuItem menuItem = null;
        JMenuBar menuBar = new JMenuBar();
		menuBar.setPreferredSize(new Dimension(200,18));
		JMenu fileMenu = createFileMenu("File");		
        menuBar.add(fileMenu);
        
        menuBar.add(createEditMenu());
        ButtonGroup group = new ButtonGroup();

        menuBar.add(createModelMenu("Model"));
        menuBar.add(createTestMenu("Test data"));

        JMenu dMenu = new JMenu("Domain");
        
        JMenu cMenu = new JMenu("Compounds");
        cMenu.add(createCompoundsMenu("Training set")); 
        cMenu.add(createTestCompoundsMenu("Test set"));        
        dMenu.add(cMenu);
        
        JMenu optionsMenu = new JMenu("Options");        
        optionsMenu.add(createMethodMenu("Method"));
        optionsMenu.add(createPreprocessingMenu("Data preprocessing"));
    	
        optionsMenu.addSeparator();
        optionsMenu.add(createThresholdMenu("Threshold"));
        optionsMenu.addSeparator();
        JMenu distMenu = new JMenu("Distance Options");    
        distMenu.add(createCenterMenu("Center"));
        optionsMenu.add(distMenu);
        
        optionsMenu.add(createFpMenu("Fingerprint Comparison"));
        optionsMenu.add(createAEMenu("Atom Environment Comparison"));        
        
        dMenu.add(optionsMenu);
        
        menuBar.add(dMenu);
        menuBar.add(createVisualMenu("Visualisation"));
        menuBar.add(createHelpMenu("Help"));        
        return menuBar;
    }    
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
	}
	protected JPanel createModelPanel(Color foreClr, Color backClr) {
		JPanel topPanel =new JPanel();
		topPanel.setBackground(foreClr);
		topPanel.setLayout(new BorderLayout());
		
		JPanel p = new JPanel();
		p.setBackground(foreClr);
		p.setForeground(backClr);		
		p.setLayout(new GridLayout(1,2));
		
		ADDataSetPanel modelPanel = new ADDataSetPanel("Model" , foreClr, backClr);
		//new Color(214,223,247)
		ADPanel adpanel = new ADPanel("Domain estimation" , foreClr, backClr);
		ADStatsPanel msPanel = new ADStatsPanel(" training data" , foreClr, backClr);
		
		p.add(modelPanel);
		p.add(msPanel);

		topPanel.add(p,BorderLayout.CENTER);
		topPanel.add(adpanel,BorderLayout.EAST);

		dm.modelData.addAmbitObjectListener(modelPanel);
		dm.modelData.addAmbitObjectListener(msPanel);		
		dm.modelData.addAmbitObjectListener(adpanel);		
		return topPanel;
	}

	
	protected JPanel createTestPanel(Color foreClr, Color backClr) {
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		ADDataSetPanel testPanel = new ADDataSetPanel("Selected data set",backClr, foreClr);
		//new Color(214,223,247)
		ADActionPanel actPanel = new ADActionPanel(dm,"Domain<p>assessment",backClr, foreClr); 
		ADStatsPanel tStatPanel =new ADStatsPanel("selected test data set",backClr, foreClr);
		
		JPanel p = new JPanel();
		p.setBackground(foreClr);
		p.setForeground(backClr);		
		p.setLayout(new GridLayout(1,2));
		p.add(testPanel);
		p.add(tStatPanel);
		
        dm.testData.addAmbitObjectListener(testPanel);
		dm.testData.addListListener(testPanel);		
		dm.testData.addListListener(tStatPanel);		
		
        dm.testData.addAmbitObjectListener(tStatPanel);
        
        
        bottomPanel.setBackground(foreClr);                
        bottomPanel.setForeground(backClr);        
		bottomPanel.add(p,BorderLayout.CENTER);
		//bottomPanel.add(actPanel);		
		bottomPanel.add(actPanel,BorderLayout.EAST);		
		return bottomPanel;
	
	}
	public void showCompoundsIn(QSARDataset ds) {
		CompoundsList cl =  ds.getCoverage().getCompoundsInDomain(ds);
		if (cl != null) {
		    
		    CompoundsListPanel scrollPane = new CompoundsListPanel(cl,3,new Dimension(150,150));
		    JOptionPane.showMessageDialog(mainFrame,scrollPane,"Compounds in domain: "+ds.getName(),JOptionPane.PLAIN_MESSAGE,null);
		    /*
			AmbitObjectDialog dlg = AmbitObjectDialog.createAndShowModal(
					true,
				"Compounds in domain",
				mainFrame, cl);
			//for (int i = (cl.size()-1) ;i >= 0; i--) cl.remove(i);
			 * 
			 */		
		}

	}
	public void showCompoundsOut(QSARDataset ds) {
		if (ds == null) return;
		CompoundsList cl =  ds.getCoverage().getCompoundsOutOfDomain(ds);
		if (cl != null) {
		    CompoundsListPanel scrollPane = new CompoundsListPanel(cl,3,new Dimension(150,150));
		    JOptionPane.showMessageDialog(mainFrame,scrollPane,"Compounds out of domain: "+ ds.getName(),JOptionPane.PLAIN_MESSAGE,null);

		    /*
			AmbitObjectDialog dlg = AmbitObjectDialog.createAndShowModal(
					true,
				"Compounds out of domain",				
				mainFrame,
				cl);
			//for (int i = (cl.size()-1) ;i >= 0; i--) cl.remove(i);
			
			 */
		}	
		
	}	
	/* (non-Javadoc)
	 * @see ambit.applications.CoreApp#createWidgets(javax.swing.JFrame, javax.swing.JPanel)
	 */
	protected void createWidgets(JFrame aFrame, JPanel aPanel) {
		//this are the mainframe and the main panel
		Color back = new Color(255,255,255);
		Color fore = AmbitColors.DarkClr;
		toolBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,Color.gray,Color.lightGray));		
	
		aPanel.setLayout(new BoxLayout(aPanel,BoxLayout.PAGE_AXIS));		
		aPanel.add(createModelPanel(back,fore));
		
		Color almostWhite = AmbitColors.BrightClr;		
		Color darkBlue = AmbitColors.DarkClr;
		ADDataSetsPanel dsPanel = new ADDataSetsPanel("Test data sets",almostWhite,darkBlue); 
		
		dm.testData.addAmbitObjectListener(dsPanel);		
		dm.testData.addListListener(dsPanel);		
		aPanel.add(dsPanel);
		
		aPanel.add(createTestPanel(darkBlue,almostWhite));
        
	}

	 protected ImageIcon getIcon() {
			try {
				ImageIcon icon = UITools.createImageIcon("ambit/ui/images/ambit_logo.jpg");
				return icon;
			} catch (Exception x) {
				return null;
			}
	}
		public static void main(final String[] args) {
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	                AmbitDiscoveryApp app = new AmbitDiscoveryApp("Ambit Discovery ",580,360,args);
	            }
	        });
	   }
		
	  	
}


