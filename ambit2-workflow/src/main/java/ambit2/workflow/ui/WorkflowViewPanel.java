package ambit2.workflow.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import ambit2.ui.ColorTableCellRenderer;
import ambit2.ui.Utils;

import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.microworkflow.events.WorkflowEvent;
import com.microworkflow.process.Activity;
import com.microworkflow.process.CompositeActivity;
import com.microworkflow.process.Conditional;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;
import com.microworkflow.ui.IWorkflowListenerUI;


public class WorkflowViewPanel extends JPanel implements IWorkflowListenerUI {
    protected Workflow workflow;
    protected WorkflowTableModel wftm;
    protected WorkflowPrefuse display;
    protected JTable table;
    protected boolean animate = true;
    protected JTextArea status;
    protected String title="Workflow";
    protected ImageIcon ptr, executed, notyet, conditional, composite;
    /**
     * 
     */
    private static final long serialVersionUID = -9177580035692770353L;
    public WorkflowViewPanel(Workflow wf,Action action) {
        super();
        try {
        	ptr = Utils.createImageIcon("images/resultset_next.png");
        } catch (Exception x) {
        	ptr = null;
        }
        try {
        	executed = Utils.createImageIcon("images/tick.png");
        } catch (Exception x) {
        	executed = null;
        }
        try {
        notyet =  Utils.createImageIcon("images/cross.png");
        } catch (Exception e) {
			notyet  = null;
		}
        try {
            composite =  Utils.createImageIcon("images/application_put.png");
            } catch (Exception e) {
            	composite  = null;
    		}  
        
        try {
            conditional =  Utils.createImageIcon("images/arrow_divide.png");
            } catch (Exception e) {
            	conditional  = null;
    		}  

            

        wftm = new WorkflowTableModel(null) {
        	@Override
        	public int getColumnCount() {
        		return 4;
        	}
        	@Override
        	public String getColumnName(int col) {
        		if (col > 2) return "";
        		return super.getColumnName(col);
        	}
        	@Override
        	public Object getValueAt(int row, int col) {
                switch (col) {
                case 0:
                    return row+1;
                case 3:
	             		Activity a = getActivity(row);
	              		if (a instanceof Primitive)
               				return (((Primitive)a).hasExecuted()) ? executed : notyet;
	               		else if (a instanceof CompositeActivity)
	               			return composite;
	               		else if (a instanceof Conditional)	    
	               			return conditional;
	               		else
	               			return null;
	
                case 1:
                	if (selected == row)
            				return ptr;
                	else 
                			return null;
               	
                case 2: {
                    return super.getValueAt(row, 1);
                }    
                default:
                    return row;
                }
        	}
        	@Override
        	public Class<?> getColumnClass(int columnIndex) {
        		if ((columnIndex % 2) ==1)
        			return ImageIcon.class;
        		else
        			return super.getColumnClass(columnIndex);
        	}
        };
        setWorkflow(wf);
        
        final ImageRenderer renderer = new ImageRenderer();
        table = new JTable(wftm) {
			public void createDefaultColumnsFromModel() {
				TableModel m = getModel();
				if (m != null) {
					// Remove any current columns
					TableColumnModel cm = getColumnModel();
					while (cm.getColumnCount() > 0) {
						cm.removeColumn(cm.getColumn(0));
					}
					// Create new columns from the data model info
					final int columnSize[] = { 32, 32,0,32,0};
					for (int i = 0; i < m.getColumnCount(); i++) {
						TableColumn newColumn = new TableColumn(i);
						if (columnSize[i] >0) {
							newColumn.setMaxWidth(columnSize[i]);
						}
						addColumn(newColumn);
						if ((i % 2) == 1)
							newColumn.setCellRenderer(renderer);
						else
							newColumn.setCellRenderer(new ColorTableCellRenderer());
					}
				}

			};
		};        
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(false);
		table.setGridColor(table.getBackground());
		table.setTableHeader(null);
		table.setRowHeight(32);
        table.getColumnModel().getColumn(0).setMaxWidth(32);
        table.getColumnModel().getColumn(1).setMaxWidth(32);
        table.getColumnModel().getColumn(3).setMaxWidth(32);        
        table.setShowVerticalLines(false);
        JScrollPane pane = new JScrollPane(table);
        pane.setPreferredSize(new Dimension(200,400));
        pane.setMaximumSize(new Dimension(Integer.MAX_VALUE,200));
        
        status  = new JTextArea();
        status.setBackground(getBackground());
        status.setEditable(false);
        status.setBorder(null);
        status.setPreferredSize(new Dimension(200,200));
        //fake button 
        JButton cancel = new JButton("Stop");
        cancel.setEnabled(false);
        
		FormLayout layout = new FormLayout(
	            "pref:grow",
				"top:pref,top:pref:grow,pref,pref,bottom:pref");
			setLayout(layout);
			
        CellConstraints cc = new CellConstraints();        
        
        add(DefaultComponentFactory.getInstance ().createSeparator( "Workflow steps" ),cc.xy(1,1)) ;
        add(pane, cc.xy(1,2));        
        
        JButton prefuse = new JButton(new AbstractAction("View") {
        	public void actionPerformed(ActionEvent e) {
        		WorkflowPrefuse prefuse = new WorkflowPrefuse(workflow);
        		JOptionPane.showMessageDialog(table,new JScrollPane(prefuse));
        		
        	}
        });
        add(ButtonBarFactory.buildWizardBar(
        		new JButton[] {prefuse}
        		, new JButton(action),cancel,
        		new JButton[] {}),
        		cc.xy(1,3)
        		);
        add(DefaultComponentFactory.getInstance ().createSeparator( "Messages" ),cc.xy(1,4)) ;
        add(new JScrollPane(status),cc.xy(1,5));
	    
	    
    }
    public void setWorkflow(Workflow wf) {
       if (this.workflow != null)
           this.workflow.removePropertyChangeListener(this);

       
       wftm.setActivities(null);
       this.workflow = wf;
       if (wf != null) {
    	   final ArrayList<Activity> activities = getActivityList(wf.getDefinition());
           workflow.addPropertyChangeListener(this);    	   
    	   if (activities.size()>0 ) {
	           wftm.setActivities(activities);

	      	   title = wf.toString();
	      	   return;
    	   }
       } 
       title="No workflow defined!";
    }
    public ArrayList<Activity> getActivityList(Activity activity) {
        final ArrayList<Activity> activities = new ArrayList<Activity>();
        WorkflowTools tools = new WorkflowTools() {
     	   @Override
     	public Object process(Activity[] parentActivity, Activity activity) {
     		if (activity instanceof Sequence) ;
     		else
     			activities.add(activity);
     		return activity;
     	}
        };
        if (activity != null) 
     	   tools.traverseActivity(null,activity,0,true);
        return activities;
    }
    public JComponent getUIComponent() {
        return this;
    }
    public void propertyChange(PropertyChangeEvent arg0) {
        if (WorkflowEvent.WF_DEFINITION.equals(arg0.getPropertyName()))
            if (arg0.getNewValue() instanceof Activity) {
            	title = arg0.toString();
            	wftm.setActivities(null);
            	wftm.setActivities(getActivityList((Activity )arg0.getNewValue()));
            } else ;
        else if (WorkflowEvent.WF_EXECUTE.equals(arg0.getPropertyName())) {
            if (animate) {
                int index = wftm.findRow((Activity)arg0.getNewValue());
                table.scrollRectToVisible(table.getCellRect(index, 0, true));
            } else ;
        } else if (WorkflowEvent.WF_ANIMATE.equals(arg0.getPropertyName())) {
             setAnimate((Boolean) arg0.getNewValue());
        } else if (WorkflowEvent.WF_START.equals(arg0.getPropertyName())) {
            status.setText("");
             
        } else if (WorkflowEvent.WF_COMPLETE.equals(arg0.getPropertyName())) {
            wftm.setSelected(-1);
            //table.scrollRectToVisible(table.getCellRect(0, 0, true));
        } else if (WorkflowEvent.WF_ABORTED.equals(arg0.getPropertyName())) {
            wftm.setSelected(-1);
            status.setText(arg0.getNewValue().toString());
        } else if (WorkflowEvent.WF_RESUMED.equals(arg0.getPropertyName())) {
            status.setText(arg0.getNewValue().toString());            
        } else 
        	if (arg0.getNewValue()!= null)
        		status.setText(status.getText()+"\n"+arg0.getNewValue().toString());


        
    }
        public synchronized boolean isAnimate() {
            return animate;
        }
        public synchronized void setAnimate(boolean animate) {
            this.animate = animate;
        }        
        @Override
        public String toString() {
        	return title;
        }
}

 class ImageRenderer extends DefaultTableCellRenderer {

	  /**
	 * 
	 */
	private static final long serialVersionUID = -3804831435352110553L;

	/*
	   * @see TableCellRenderer#getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)
	   */
	  public Component getTableCellRendererComponent(JTable table, Object value,
	                                                 boolean isSelected, boolean hasFocus, 
	                                                 int row, int column) {
	    setIcon((ImageIcon)value);
	    return this;
	  }
	}
