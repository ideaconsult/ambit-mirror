package ambit2.workflow.ui;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.microworkflow.events.WorkflowEvent;
import com.microworkflow.process.Activity;
import com.microworkflow.process.Workflow;
import com.microworkflow.ui.IWorkflowListenerUI;
import com.microworkflow.ui.WorkflowTableModel;

public class WorkflowViewPanel extends JPanel implements IWorkflowListenerUI {
    protected Workflow workflow;
    protected WorkflowTableModel wftm;
    protected JTable table;
    protected boolean animate = true;
    protected JTextArea status;
    protected String title="Workflow";
    /**
     * 
     */
    private static final long serialVersionUID = -9177580035692770353L;
    public WorkflowViewPanel(Workflow wf,Action action) {
        super();
		FormLayout layout = new FormLayout(
            "pref:grow",
			"top:pref,top:pref:grow,pref,pref,bottom:pref");
		setLayout(layout);
        wftm = new WorkflowTableModel(null);
        setWorkflow(wf);
        

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
					final int columnSize[] = { 32, 32, 0};
					for (int i = 0; i < m.getColumnCount(); i++) {
						TableColumn newColumn = new TableColumn(i);
						if (columnSize[i]>0) {
							newColumn.setMaxWidth(columnSize[i]);
						}
						addColumn(newColumn);
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
        
        CellConstraints cc = new CellConstraints();        
        
        add(DefaultComponentFactory.getInstance ().createSeparator( "Workflow steps" ),cc.xy(1,1)) ;
        add(pane, cc.xy(1,2));        
        add(ButtonBarFactory.buildRightAlignedBar(
        		new JButton[] {new JButton(action),cancel}),
        		cc.xy(1,3)
        		);
        add(DefaultComponentFactory.getInstance ().createSeparator( "Messages" ),cc.xy(1,4)) ;
        add(new JScrollPane(status),cc.xy(1,5));
	    
	    
    }
    public void setWorkflow(Workflow wf) {
       if (this.workflow != null)
           this.workflow.removePropertyChangeListener(this);
       wftm.setActivity(null);
       this.workflow = wf;
       if (wf != null) {
           wftm.setActivity(wf.getDefinition());
           workflow.addPropertyChangeListener(this);
      	   title = wf.toString();
       } else title="No workflow defined!";
    }
    public JComponent getUIComponent() {
        return this;
    }
    public void propertyChange(PropertyChangeEvent arg0) {
        if (WorkflowEvent.WF_DEFINITION.equals(arg0.getPropertyName()))
            if (arg0.getNewValue() instanceof Activity) {
            	title = arg0.toString();
                wftm.setActivity((Activity )arg0.getNewValue());
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
