/**
 * CompoundPanel
 * @author Nina Jeliazkova <br>
 * <b>Created</b> 2005-4-30
 */
package ambit.ui.data.molecule;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit.data.descriptors.DescriptorDefinition;
import ambit.data.experiment.Experiment;
import ambit.data.experiment.ExperimentList;
import ambit.data.experiment.TemplateField;
import ambit.data.molecule.DataContainer;
import ambit.misc.AmbitCONSTANTS;
import ambit.ui.data.HashtableModel;





/**
 * A {@link javax.swing.JPanel} descendant, displaying 2D structure diagram of the molecule at the bottom
 * and a table with molecule properties read by {@link org.openscience.cdk.interfaces.IMolecule#getProperties() }.
 * See example for {@link ambit.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-30
 */
public class CompoundPanel extends JPanel implements   Observer  {
    
	/**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 2945504935753073616L;
    HashtableModel identifiersModel = null;
    HashtableModel descriptorsModel = null;
    HashtableModel experimentsModel = null;
    protected DataContainer dataContainer;
	
	protected Panel2D picturePanel = null;
	protected Panel3D jmolPanel = null;
	protected NavigationPanel navPanel = null;
	protected AbstractAction editAction  = null;
	protected JTabbedPane detailsPanel = null;
	GridBagLayout gridbag;
	protected int split = JSplitPane.VERTICAL_SPLIT;
	/**
	 * 
	 */
	public CompoundPanel(DataContainer model, AbstractAction editAction, Color bgColor, Color fColor,int split) {
		super();
		this.split = split;
		this.dataContainer = model;
		this.editAction = editAction;
		model.addObserver(this);
		initLayout(bgColor,fColor);
		addWidgets(bgColor,fColor);
		model.setSelectedIndex(model.getSelectedIndex());
	}
	/* (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    	if (dataContainer.isEnabled())
    		display();
    }
	private void initLayout(Color bgColor, Color fColor) {
		//gridbag = new GridBagLayout() ;
		setLayout(new BorderLayout());
		setBackground(bgColor);
		setForeground(fColor);
		setBorder(BorderFactory.createMatteBorder(5,5,5,5,bgColor));
	
	}

	protected void addWidgets(Color bgColor, Color fColor) {
		setBackground(bgColor);
        
		detailsPanel = new JTabbedPane();
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
		JPanel propertiesPanel = propertiesPanel(identifiersModel,bgColor,fColor);
		detailsPanel.addTab("Molecule",propertiesPanel);
		
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
							for (int i=0; i < r.size(); i++) {
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
		propertiesPanel = propertiesPanel(descriptorsModel,bgColor,fColor);
		detailsPanel.addTab("Descriptors",propertiesPanel);

		experimentsModel = new HashtableModel(null) {
			protected boolean accept(Object key,Object value) {
				return (key instanceof TemplateField) 
                        || (value instanceof Experiment)
                        || (value instanceof ExperimentList)
                        ;
			}
		};
		propertiesPanel = propertiesPanel(experimentsModel,bgColor,fColor);
		detailsPanel.addTab("Experiments",propertiesPanel);
		
		JTabbedPane picturePanel = new JTabbedPane();
		JPanel structurePanel = structurePanel(bgColor,fColor);
		JPanel jmolPanel = jmolPanel(bgColor,fColor);
		navPanel = new NavigationPanel(dataContainer,bgColor,fColor);

		picturePanel.addTab("Structure diagram",structurePanel);
		picturePanel.addTab("3D",jmolPanel);
		/*
		JPanel allPanels = new JPanel(new GridLayout(1,2));
		allPanels.add(detailsPanel);
		allPanels.add(picturePanel);

		*/
        JSplitPane splitPanel = new JSplitPane(
                split,
                detailsPanel, picturePanel);
        splitPanel.setBackground(bgColor);
        splitPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        //splitPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"Title"));
        splitPanel.setOneTouchExpandable(false);
        splitPanel.setDividerLocation(256);
        add(splitPanel, BorderLayout.CENTER);
        
        /*
        add(splitPanel, BorderLayout.EAST);
        
		add(allPanels, BorderLayout.CENTER);
		*/
        add(navPanel, BorderLayout.SOUTH);

        display();

	}
	protected JPanel jmolPanel(Color bgColor, Color fColor) {
		JPanel strucPanel = new JPanel();
		strucPanel.setLayout(new BorderLayout());
		
		JLabel label = new JLabel("<html><b><u>3D</u></b></html>");
		label.setToolTipText("Click here to invoke the structure diagram editor");
        label.setOpaque(true);
        label.setBackground(bgColor);
        label.setForeground(fColor);
        label.setSize(120,32);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createMatteBorder(5,0,0,0,bgColor));
        strucPanel.add(label,BorderLayout.NORTH);
        if (editAction != null) {
            label.addMouseListener(new MouseAdapter() {
    	   		public void mouseClicked(MouseEvent e) {
    	   			editAction.actionPerformed(null);
    	   		}
    	    });	
        }
        
        jmolPanel = new Panel3D();
        jmolPanel.setBorder(BorderFactory.createLineBorder(fColor));
        //picturePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        jmolPanel.setBackground(new Color(255, 255, 255));
		strucPanel.add(jmolPanel,BorderLayout.CENTER);
		
		return strucPanel;
	}	
	protected JPanel structurePanel(Color bgColor, Color fColor) {
		JPanel strucPanel = new JPanel();
		strucPanel.setLayout(new BorderLayout());
		
		JLabel label = new JLabel("<html><b><u>Structure diagram</u></b></html>");
		label.setToolTipText("Click here to invoke the structure diagram editor");
        label.setOpaque(true);
        label.setBackground(bgColor);
        label.setForeground(fColor);
        label.setSize(120,32);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createMatteBorder(5,0,0,0,bgColor));
        strucPanel.add(label,BorderLayout.NORTH);
        if (editAction != null) {
            label.addMouseListener(new MouseAdapter() {
    	   		public void mouseClicked(MouseEvent e) {
    	   			editAction.actionPerformed(null);
    	   		}
    	    });	
        }
        
        picturePanel = new Panel2D();
        picturePanel.setBorder(BorderFactory.createLineBorder(fColor));
        //picturePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		picturePanel.setBackground(new Color(255, 255, 255));
		strucPanel.add(picturePanel,BorderLayout.CENTER);
		
		return strucPanel;
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
	
	protected void display() {
		IAtomContainer ac = dataContainer.getMolecule();
	    if (picturePanel != null) 
	    	picturePanel.setAtomContainer(ac,true);
	    if (jmolPanel != null)
	    		jmolPanel.setMol(ac);
	    	try {
	    		//tableModel.setAtomContainer(ac);
	    		if (ac != null) {
		    		identifiersModel.setTable(ac.getProperties());
		    		descriptorsModel.setTable(ac.getProperties());
		    		experimentsModel.setTable(ac.getProperties());
	    		} else {
		    		identifiersModel.setTable(null);
		    		descriptorsModel.setTable(null);
		    		experimentsModel.setTable(null);
	    		
	    		}
	    	} catch (Exception x) {
	    		x.printStackTrace();
	    	}
	}
	public void addTab(String caption,JComponent component) {
		detailsPanel.add(caption,component);
	}
	public DataContainer getDataContainer() {
		return dataContainer;
	}
	public void setDataContainer(DataContainer dataContainer) {
		this.dataContainer = dataContainer;
	}

}
