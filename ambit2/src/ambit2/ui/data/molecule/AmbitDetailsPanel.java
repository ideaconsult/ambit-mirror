package ambit2.ui.data.molecule;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import toxTree.query.MolFlags;
import ambit2.config.AmbitCONSTANTS;
import ambit2.ui.editors.AmbitListEditor;
import ambit2.data.descriptors.DescriptorDefinition;
import ambit2.data.descriptors.FunctionalGroupDescriptor;
import ambit2.data.experiment.Experiment;
import ambit2.data.experiment.ExperimentList;
import ambit2.data.molecule.Compound;
import ambit2.data.molecule.CompoundsList;
import ambit2.data.molecule.DataContainer;
import ambit2.ui.AmbitColors;
import ambit2.ui.EditorPanel;
import ambit2.ui.data.CompoundsGridPane;
import ambit2.ui.data.HashtableModel;

public class AmbitDetailsPanel extends JTabbedPane implements Observer {
	//AquirePanel aquirePanel ;
	
	AmbitListEditor editor;
	EditorPanel editorPanel;
	CompoundsGridPane metabolitesPanel;
	CompoundsList metabolites;
	Panel3D jmolPanel;
	Panel2D panel2d;
	JSplitPane splitPane;
    HashtableModel identifiersModel = null;
    HashtableModel descriptorsModel = null;
    HashtableModel experimentsModel = null;	
	protected IAtomContainer selectedMolecule = null;
	public AmbitDetailsPanel() {
		super();
		addWidgets();
	}

	public AmbitDetailsPanel(int arg0) {
		super(arg0);
		addWidgets();
	}

	public AmbitDetailsPanel(int arg0, int arg1) {
		super(arg0, arg1);
		addWidgets();
	}
	public void addWidgets() {
		identifiersModel = new HashtableModel(null) {
			protected boolean accept(Object key,Object value) {
                if (value instanceof Experiment) return false;
                else return 
					(key instanceof String)	&& 
					(!key.equals(AmbitCONSTANTS.AMBIT_IDSTRUCTURE)) 
					&& (!key.equals(AmbitCONSTANTS.AMBIT_IDSUBSTANCE))
					&& (!key.equals("CRAMERFLAGS"))
					&& (!key.equals(CDKConstants.ALL_RINGS))
					&& (!key.equals(CDKConstants.SMALLEST_RINGS))
                    
					;
			};
		};
		JPanel propertiesPanel = propertiesPanel(identifiersModel,AmbitColors.BrightClr,AmbitColors.DarkClr);
		addTab("Identifiers",propertiesPanel);
		
		descriptorsModel = new HashtableModel(null) {
			NumberFormat f = new DecimalFormat();
			public Object getValueAt(int row, int col) {
				f.setMaximumFractionDigits(4);
			    Object key = keys.get(row);
			    if (key == null) return "NA";
				switch (col) {
				case 0: {
					 if (key instanceof DescriptorSpecification) { 
						 String s =((DescriptorSpecification) key).getImplementationTitle();
						 return s.substring(s.lastIndexOf('.')+1);
					 } else if (key instanceof FunctionalGroupDescriptor) {
						 return key.toString();									 
					 } else if (key instanceof IDescriptor) {
						 String s =((DescriptorSpecification)((IDescriptor) key).getSpecification()).getImplementationTitle();
						 return s.substring(s.lastIndexOf('.')+1);
					 } else return key;
				}
				case 1: { Object o = table.get(key); 
					if (o==null) return "NA"; else if (o instanceof DescriptorValue) {
						IDescriptorResult v = ((DescriptorValue) o).getValue();
						if (v instanceof DoubleResult) 
                                return f.format(((DoubleResult)v).doubleValue());
						else if (v instanceof IntegerResult) 
                            return new Integer(((IntegerResult)v).intValue());
						else if (v instanceof DoubleArrayResult) {
							StringBuffer b = new StringBuffer();
							
							DoubleArrayResult r = (DoubleArrayResult) v;
							for (int i=0; i < r.length(); i++) {
								b.append(f.format(r.get(i)));
								b.append(' ');
							}
							return b.toString();
						} else return v;
					} else return o; 
				}
				case 2: return new Boolean(enabled[row]);
				default: return "";
				}
				
			}
			protected boolean accept(Object key,Object value) {
				return (key instanceof DescriptorDefinition) 
                || (key instanceof DescriptorSpecification) 
                || (key instanceof IDescriptor)
                ;
			}
			@Override
			protected void sort(ArrayList keys) {
			    Collections.sort(keys,new Comparator(){
			            public int compare(Object o1, Object o2) {
			                if ((o1 instanceof IDescriptor) || (o2 instanceof IDescriptor))
                                return ((IDescriptor)o1).getClass().getName().compareTo(((IDescriptor)o2).getClass().getName());
                            else if ((o1 instanceof DescriptorDefinition) || (o2 instanceof DescriptorDefinition))
                                return ((DescriptorDefinition)o1).compareTo((DescriptorDefinition)o2);
                            else return -1;
			            }
                }
                    );
                //super.sort(keys);
			}
		};		
		propertiesPanel = propertiesPanel(descriptorsModel,AmbitColors.BrightClr,AmbitColors.DarkClr);
		addTab("Descriptors",propertiesPanel);
		
		jmolPanel = new Panel3D();
		panel2d = new Panel2D();
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panel2d,jmolPanel);
		splitPane.setDividerLocation(300);
		
		addTab("Structure",splitPane);
		metabolites = new CompoundsList();
		editorPanel = new EditorPanel();
        editorPanel.setPreferredSize(new Dimension(100,150));
        addTab("Experimental data",editorPanel);
        metabolitesPanel = new CompoundsGridPane(metabolites,3,new Dimension(150,150));
        addTab("Metabolites",metabolitesPanel);
        setPreferredSize(new Dimension(200,100));
        
        addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {

        		updateTab(getSelectedIndex(), selectedMolecule);
        	}
        	
        });
	}
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if (o == null) return;
	    if (o instanceof DataContainer) {
	        IAtomContainer m = ((DataContainer) o).getMolecule();
	        updateTab(getSelectedIndex(),m);


	        
	    }
	
	}
	protected void updateTab(int tab, IAtomContainer m) {
		selectedMolecule = m;
		switch (tab) {
		case 0: {
	        if (m == null) 
	    		identifiersModel.setTable(null);
	        else
	        	identifiersModel.setTable(m.getProperties());
			return;
		}
		case 1: {
	        if (m == null) 
	    		descriptorsModel.setTable(null);
	        else
	        	descriptorsModel.setTable(m.getProperties());			
			return;

		}
		case 2: {
	        jmolPanel.setMol(m);
	        panel2d.setAtomContainer(m,true);			
			return;
		}		
		case 3: {
			if (m==null) 
				editor.setAmbitList(null);
			else {
				Object a = m.getProperty(AmbitCONSTANTS.AQUIRE);
		        if (a ==null)
		        	a = m.getProperty(AmbitCONSTANTS.EXPERIMENT_LIST);
		        if (a != null) {
		        	//setSelectedIndex(0);
		        	if (a instanceof ExperimentList) {
			        	if (editor == null) {
			        		editor  = new AmbitListEditor((ExperimentList)a,JSplitPane.HORIZONTAL_SPLIT,false);
			        		editorPanel.setEditor(editor);
			        	} else 
			        		editor.setAmbitList((ExperimentList)a);
		        	    	
		        	}    
	
		        } else if (editor != null) editor.setAmbitList(null);
			}
			return;
		}
		case 4: {
	        metabolites.clear();
	        if (m == null) return;
	        Object a = m.getProperty(MolFlags.MOLFLAGS);
	        if ((a != null) && (a instanceof MolFlags)) {
	        	setSelectedIndex(1);
	        	
			    MolFlags mf = (MolFlags) a;
			    
			    IAtomContainerSet sc = mf.getHydrolysisProducts();
			    if (sc != null) {
			    	setSelectedIndex(1);
				    StringBuffer b = new StringBuffer();
				    for (int i=0; i < sc.getAtomContainerCount();i++)  
				    	metabolites.addItem(new Compound((Molecule)sc.getAtomContainer(i)));
			    } 
	        }
	        return;
		}
		default: {
			
		}

		}
	}
	protected JPanel propertiesPanel(HashtableModel tableModel, Color bgColor, Color fColor) {
		JPanel propPanel = new JPanel();
		propPanel.setLayout(new BorderLayout());
        JLabel labelA = new JLabel("<html><b>Available structure attributes</b></html>");
        labelA.setOpaque(true);
        labelA.setBackground(bgColor);
        labelA.setForeground(fColor);
        labelA.setSize(120,32);
        labelA.setAlignmentX(CENTER_ALIGNMENT);
        labelA.setBorder(BorderFactory.createMatteBorder(5,0,0,0,bgColor));
        propPanel.add(labelA,BorderLayout.NORTH);
        
        //molecule properties instead cas/name text boxes
        
		propPanel.add(createTable(tableModel),BorderLayout.CENTER);
		return propPanel;
	}
	protected JScrollPane createTable(HashtableModel tableModel) {
		JTable table = new JTable(tableModel) {
        public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int rowIndex, int vColIndex) {
            Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
            if (c instanceof JComponent) {
                JComponent jc = (JComponent)c;
                Object value = getValueAt(rowIndex, vColIndex);
                if (value != null)
                    jc.setToolTipText(value.toString());
            }
            return c;

        };
        };
		table.setTableHeader(null);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(true);
		table.setPreferredScrollableViewportSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(true);
		table.setOpaque(true);
		
		
		JScrollPane p = new JScrollPane(table);
     	p.setPreferredSize(new Dimension(256,3*24+2));
     	p.setMinimumSize(new Dimension(256,3*24+2));
     	//p.setBackground(bgColor);
     	p.setOpaque(true);
     	table.setBackground(Color.white);
		return p;
     	
	}	
	
	
}
