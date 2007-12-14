package ambit.ui.data;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ambit.data.AmbitCellEditor;
import ambit.data.descriptors.DescriptorsHashtable;
import ambit.data.experiment.Study;
import ambit.data.experiment.TemplateField;
import ambit.data.model.Model;
import ambit.data.molecule.MolProperties;
import ambit.exceptions.AmbitException;
import ambit.exceptions.PropertyNotInTemplateException;
import ambit.io.IColumnTypeSelection;
import ambit.ui.UITools;
import ambit.ui.data.model.ModelTable;

/**
 * Refactor to use {@link PropertiesPanel}
 * @author Nina Jeliazkova
 *
 */
public class MolPropertiesPanel extends JPanel implements Observer {
	
    protected MolProperties properties;
    protected JTable propsTable;
    protected JTable idsTable;
    protected JTable descriptorsTable;
    protected JTable experimentsTable;
    protected JTabbedPane right;    
    protected JButton saveButton;
    protected JButton loadButton;
	public MolPropertiesPanel(MolProperties properties) {
		super();
		addWidgets(properties);
		
		//setPreferredSize(new Dimension(400,400));
	}
	protected void addWidgets(MolProperties properties) {
	    this.properties = properties;
	    properties.addObserver(this);
	    setLayout(new BorderLayout());
	    propsTable = new JTable(new HashtableModel(properties.getProperties(),true) {
	    	public String getColumnName(int arg0) {
	    		switch (arg0) {
	    		case 0: return "Property";
	    		case 1: return "Type or value";
	    		default: return "";
	    		}
	    	}
	    	public boolean isCellEditable(int rowIndex, int columnIndex) {
	    		return false;
	    	}
	    },createColumnModel(null));
	    propsTable.setSurrendersFocusOnKeystroke(true);
	    propsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    ListSelectionModel m = new DefaultListSelectionModel();
	    propsTable.setSelectionModel(m);
	    m.addListSelectionListener(new ListSelectionListener() {
	       /* (non-Javadoc)
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e) {
 	        if (e.getValueIsAdjusting()) return;

 	        ListSelectionModel lsm =
 	            (ListSelectionModel)e.getSource();
 	        if (!lsm.isSelectionEmpty()) { 
 	        	setSelected(propsTable.getValueAt(lsm.getMinSelectionIndex(),0));
 	        }
	    }});
     	
	    JScrollPane left = new JScrollPane(propsTable);
	    left.setBorder(BorderFactory.createTitledBorder("All available properties"));
	    left.setMinimumSize(new Dimension(100,100));
		
	    idsTable = new JTable(new HashtableModel(properties.getIdentifiers(),true),
	            createColumnModel(
	    	            new String[] {
	    	            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctSMILES],
	    	            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctChemName],
	    	            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctCAS],
	    	            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctRowID],
	    	            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctUnknown]}
	                    ));
	    idsTable.setSurrendersFocusOnKeystroke(true);
		descriptorsTable = new JTable(new HashtableModel(properties.getDescriptors(),true),
		        createColumnModel(
			            new String[] {
			            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctX],
			            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctUnknown]
			                                               }		                
		                ));
		descriptorsTable.setSurrendersFocusOnKeystroke(true);
	    experimentsTable = new JTable(new HashtableModel(properties.getExperimental(),true));
	    experimentsTable.setSurrendersFocusOnKeystroke(true);
		right = new JTabbedPane();
		right.setBorder(BorderFactory.createTitledBorder("Selected properties"));
		right.add("Identifiers",createIDsPanel(idsTable));
		right.add("Descriptors",createDescriptorsPanel(descriptorsTable));
		right.add("Experimental",createExperimentsPanel(experimentsTable));
		
		
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,left,right);
		pane.setDividerLocation(200);
		
		add(pane,BorderLayout.CENTER);
		

		add(loadsaveButtons(),BorderLayout.SOUTH);
		
	}
	protected JToolBar loadsaveButtons() {
		loadButton = new JButton(new AbstractAction("Load saved properties") {
			  /* (non-Javadoc)
	         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	         */
	        public void actionPerformed(ActionEvent e) {
	            try {
	                FileInputStream in = new FileInputStream("properties.conf");
	                loadProperties(in);
	                in.close();
	            } catch (Exception x) {
	                x.printStackTrace();

	            }

	        }  
			});
			    
			
			saveButton = new JButton(new AbstractAction("Save properties") {
			    /* (non-Javadoc)
	             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	             */
	            public void actionPerformed(ActionEvent e) {
	                // TODO Auto-generated method stub
	                try {
	                    FileOutputStream out = new FileOutputStream("properties.conf");
	                    saveProperties(out);
	                    out.close();

	                } catch (Exception x) {
	                    x.printStackTrace();

	                }
	            }
					});
			JToolBar toolbar = new JToolBar();
			toolbar.add(loadButton);
			toolbar.add(saveButton);
			return toolbar;	    
	}
	protected void loadProperties(InputStream in) throws Exception {
	    Object o = new ObjectInputStream(in).readObject();
        if (o instanceof MolProperties) {
            setProperties((MolProperties)o);
        }
	}
	protected void setProperties(MolProperties newProps) throws Exception {
            properties.setIdentifiers((DescriptorsHashtable)newProps.getIdentifiers());
            ((HashtableModel) idsTable.getModel()).setTable(properties.getIdentifiers());
            
            DescriptorsHashtable d =(DescriptorsHashtable) newProps.getDescriptors();
            properties.setDescriptors(d);
            ((HashtableModel) descriptorsTable.getModel()).setTable(properties.getDescriptors());
            
            properties.setExperimental(newProps.getExperimental());
            ((HashtableModel) experimentsTable.getModel()).setTable(properties.getExperimental());

            properties.setQSAR(newProps.getQSAR());
            properties.setQsarModel(newProps.getQsarModel());
            properties.setStudy(newProps.getStudy());
	}
	protected void saveProperties(OutputStream out) throws Exception {
	    ObjectOutputStream s = new  ObjectOutputStream(out);
	    s.writeObject(properties);
	}
	public TableColumnModel createColumnModel(String[] options) {
	    TableColumnModel m = new DefaultTableColumnModel();
	    m.addColumn(new TableColumn(0));
	    m.addColumn(new TableColumn(1));
        JComboBox comboBox;
        
        if (options != null) {
			comboBox = new JComboBox();
			for (int i =0; i < options.length; i++)
				comboBox.addItem(options[i]);
			comboBox.setEnabled(true);
			m.getColumn(1).setCellEditor(new DefaultCellEditor(comboBox));
        } else m.getColumn(1).setCellEditor(new AmbitCellEditor());
        return m;
	}
	protected void setSelected(Object property) {
		properties.setSelectedProperty(property);
	}
	public JPanel createIDsPanel(JTable table) {
	    return new MyHashtablePanel(properties,table,"Identifiers") {
	        /* (non-Javadoc)
             * @see ambit.ui.data.HashtablePanel#moveTo()
             */
            protected void moveTo() {
                properties.moveToIdentifiers(properties.getSelectedProperty());
                HashtableModel model = (HashtableModel)propsTable.getModel();
                model.setTable(model.getTable());
                super.moveTo();
            }
            protected void moveBack() {
            	
            	super.moveBack();
            	HashtableModel model = (HashtableModel)propsTable.getModel();
                model.setTable(model.getTable());            	
            }
            protected void guess() {
            	properties.guessIdentifiers();
            	super.guess();
            	HashtableModel model = (HashtableModel)propsTable.getModel();
                model.setTable(model.getTable());            	
            }
	    };
	    
	}
	public JPanel createDescriptorsPanel(JTable table) {
	    return new MyHashtablePanel(properties,table,"Descriptors") {
	        /* (non-Javadoc)
             * @see ambit.ui.data.HashtablePanel#moveTo()
             */
            protected void moveTo() {
                properties.moveToDescriptors(properties.getSelectedProperty());
                HashtableModel model = (HashtableModel)propsTable.getModel();
                model.setTable(model.getTable());                
                super.moveTo();
            }
            protected void moveBack() {
            	super.moveBack();
            	HashtableModel model = (HashtableModel)propsTable.getModel();
                model.setTable(model.getTable());            	
            }            
            protected void guess() {
            	properties.guessDescriptors();
            	super.guess();
            	HashtableModel model = (HashtableModel)propsTable.getModel();
                model.setTable(model.getTable());            	
            }
            
	    };
	}
	public JPanel createExperimentsPanel(JTable table) {
	    return new ExperimentsPanel(properties,table,"Experimental data")            {
	    	
	        /* (non-Javadoc)
             * @see ambit.ui.data.HashtablePanel#moveTo()
             */
            protected void moveTo() {
                try {
                	try {
                		properties.moveToExperimental(properties.getSelectedProperty());
	            	} catch (PropertyNotInTemplateException x) {
	            		handleMissingProperty(x);
	            	} 
                    HashtableModel model = (HashtableModel)propsTable.getModel();
                    model.setTable(model.getTable());                    
                    super.moveTo();
                } catch (Exception x) {
                    JOptionPane.showMessageDialog(this,x.getMessage());
                }
            }
            protected void moveBack() {
            	super.moveBack();
            	HashtableModel model = (HashtableModel)propsTable.getModel();
                model.setTable(model.getTable());            	
            }
            protected void guess() {
            	try {
	            	try {
	            		properties.guessExperiments();
	            	} catch (PropertyNotInTemplateException x) {
	            		handleMissingProperty(x);
	            	} 
            	} catch (Exception x) {
            		
            	}
            	super.guess();
            	HashtableModel model = (HashtableModel)propsTable.getModel();
                model.setTable(model.getTable());            	
            }
            
	    };
	}	
	protected void handleMissingProperty(PropertyNotInTemplateException x) {
		Object selected = JOptionPane.showInputDialog(this,
				"Import \""+x.getKey()+"\"\n as a field from template \""+x.getTemplate().getName() +"\"",
				x.getMessage(),
				JOptionPane.PLAIN_MESSAGE,
				null, 
				x.getTemplate().toArray()
				, x.getTemplate().getField("Result"));
		if (selected != null) {
		    properties.moveToExperimental( x.getKey(),(TemplateField)selected);
		}
	}
	public JPanel createQSARPanel(JTable table) {
	    return new QSARPanel(properties,table,"QSAR model"
	          ) {
	    	
	        /* (non-Javadoc)
             * @see ambit.ui.data.HashtablePanel#moveTo()
             */
            protected void moveTo() {
                try {
                    properties.moveToQSAR(properties.getSelectedProperty());
                    HashtableModel model = (HashtableModel)propsTable.getModel();
                    model.setTable(model.getTable());                    
                    super.moveTo();
                } catch (Exception x) {
                    JOptionPane.showMessageDialog(this,x.getMessage());
                }
            }
            protected void moveBack() {
            	super.moveBack();
            	HashtableModel model = (HashtableModel)propsTable.getModel();
                model.setTable(model.getTable());            	
            }
            protected void guess() {
            	properties.guessQSAR();
            	super.guess();
            	HashtableModel model = (HashtableModel)propsTable.getModel();
                model.setTable(model.getTable());            	
            }
            
	    };
	}	
	/* (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        if (o instanceof MolProperties) {
            try {
                properties.deleteObserver(this);
                setProperties((MolProperties) o);
                properties.addObserver(this);
            } catch (Exception x) {
                
            }
        }    

    }
}




abstract class MyHashtablePanel extends JPanel {
	protected Object selectedProperty = null;
    protected MolProperties properties;
    protected JTable table;
    protected JToolBar toolbar;
    protected String caption;
    public MyHashtablePanel(MolProperties properties, JTable table, String caption) {
        super(new BorderLayout());
        this.caption = caption;
        setMinimumSize(new Dimension(100,100));
        setPreferredSize(new Dimension(100,200));
        this.properties = properties;
        this.table = table;
        add(new JScrollPane(table),BorderLayout.CENTER);
        table.setToolTipText("<html>Select row and then click move buttons to move the property>/html>");
	    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    ListSelectionModel m = new DefaultListSelectionModel();
	    table.setSelectionModel(m);
	    m.addListSelectionListener(new ListSelectionListener() {
	       /* (non-Javadoc)
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e) {
 	        if (e.getValueIsAdjusting()) return;

 	        ListSelectionModel lsm =
 	            (ListSelectionModel)e.getSource();
 	        if (!lsm.isSelectionEmpty()) { 
 	        	setSelected(lsm.getMinSelectionIndex());
 	        }
	    }});
	    
        toolbar = new JToolBar();
        toolbar.setOrientation(JToolBar.VERTICAL);
        toolbar.setFloatable(false);
        //left.setLayout(new BoxLayout(left,BoxLayout.PAGE_AXIS));
        JButton b;
        Dimension d = new Dimension(72,24);
        toolbar.setPreferredSize(d);
        add(toolbar,BorderLayout.WEST);
        b = new JButton(new AbstractAction("Guess",UITools.createImageIcon("ambit/ui/images/guess-16.png")){
	        /* (non-Javadoc)
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                guess();
            }
	    });
        b.setPreferredSize(d);
        b.setToolTipText("Guess which properties can be "+caption);
        toolbar.add(b);
        
        b = new JButton(new AbstractAction(">>",UITools.createImageIcon("ambit/ui/images/arrowright_green_16.png")){
	        /* (non-Javadoc)
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                moveTo();
            }
	    });
        b.setToolTipText("Move selected property to "+caption);
        b.setPreferredSize(d);
        toolbar.add(b);
        b = new JButton(new AbstractAction("<<",UITools.createImageIcon("ambit/ui/images/arrowleft_green_16.png")){
        	
	        /* (non-Javadoc)
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                moveBack();
            }
            
	    });
        b.setPreferredSize(d);
        b.setToolTipText("Move selected "+caption + " back to the property studyList");
        
        toolbar.add(b);
        
    }
	protected void setSelected(int selectedIndex) {
     	selectedProperty = table.getValueAt(selectedIndex,0);
	}
    protected void moveTo() {
        HashtableModel model = (HashtableModel)table.getModel();
        model.setTable(model.getTable());
    }
    
    
    protected void moveBack() {
        HashtableModel model = (HashtableModel)table.getModel();
        properties.moveBack(selectedProperty,model.getTable());
        model.setTable(model.getTable());

    }
    protected void guess() {
        HashtableModel model = (HashtableModel)table.getModel();
        model.setTable(model.getTable());

    }     
       
}


class ExperimentsPanel extends MyHashtablePanel {
	public ExperimentsPanel(MolProperties properties, JTable table,String caption) {
		super(properties,table,caption);
	       JButton b = new JButton(new AbstractAction("Study \""+properties.getQsarModel().getStudy() + '"'
	        		,UITools.createImageIcon("ambit/ui/images/template.png")){
		        /* (non-Javadoc)
	             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	             */
	            public void actionPerformed(ActionEvent e) {
	                Study t = showStudy();
	                if (t != null) {

	                	putValue(AbstractAction.NAME, "Study \""+t+'"');
	                }
	            }
		    });		
		/*
        JButton b = new JButton(new AbstractAction("Template \""+properties.getTemplate() + '"'
        		,UITools.createImageIcon("ambit/ui/images/template.png")){
            public void actionPerformed(ActionEvent e) {
                StudyTemplate t = showTemplate();
                if (t != null)
                putValue(AbstractAction.NAME, "Template \""+t+'"');
            }
	    });
	    */
        b.setToolTipText("Experimental data recognition depends on a template. Click here to view the current template or load another one.");
        add(b,BorderLayout.SOUTH);
	}
	protected Study showStudy() {
		try {
			properties.getStudy().editor(true).view(this,true,"Study");
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		//JOptionPane.showMessageDialog(this,p,"Study",JOptionPane.PLAIN_MESSAGE);
    	//properties.setTemplate(properties.getTemplate());
		return properties.getStudy();
	}

}

class QSARPanel extends MyHashtablePanel {
	public QSARPanel(MolProperties properties, JTable table,String caption) {
		super(properties,table,caption);
		/*
        JButton b = new JButton(new AbstractAction("QSAR Model \""+properties.getQsarModel() + '"'
        		,UITools.createImageIcon("ambit/ui/images/template.png")){

            public void actionPerformed(ActionEvent e) {
                try {
                Model m = showModel();
                putValue(AbstractAction.NAME, m.toString());
                } catch (AmbitException x) {
                    
                }
            }
	    });
        
        b.setToolTipText("QSAR model");
        	    add(b,BorderLayout.SOUTH);
        	    */
        	    
        /*
        JScrollPane p = new JScrollPane(new JTable(new ModelTable(properties.getQsarModel()
        		)));
		JPanel mp = new JPanel(new BorderLayout());
		mp.add(p,BorderLayout.CENTER);
		mp.add(properties.getQsarModel().getReference().editor().getJComponent(),BorderLayout.SOUTH);
		
        mp.setBorder(BorderFactory.createTitledBorder("Model"));
        mp.setPreferredSize(new Dimension(200,300));
        add(mp,BorderLayout.SOUTH);
        */
        
        /*
        AbstractAmbitEditor ae = new AbstractAmbitEditor("Model",properties.getQsarModel()) {
        	protected AbstractPropertyTableModel createTableModel(ambit.data.AmbitObject object) {
        		return new ModelTable((Model)object);
        	};
        };
        add(ae,BorderLayout.SOUTH);
        
        setPreferredSize(new Dimension(ae.getPreferredSize()));
        */
        //add(properties.getQsarModel().editor().getJComponent(),BorderLayout.SOUTH);
	}
	protected Model showModel() throws AmbitException {
        AbstractAmbitEditor ae = new AbstractAmbitEditor("QSAR Model",properties.getQsarModel()) {
        	protected AbstractPropertyTableModel createTableModel(ambit.data.AmbitObject object) {
        		return new ModelTable((Model)object);
        	};
        };
        ae.setLayout(new BoxLayout(ae,BoxLayout.PAGE_AXIS));
        ae.setPreferredSize(new Dimension(400,(int)ae.getPreferredSize().getHeight()));
        ae.view(this,true, "");
		//properties.getQsarModel().editor().view(this,true);
		return properties.getQsarModel();
	}
	
}