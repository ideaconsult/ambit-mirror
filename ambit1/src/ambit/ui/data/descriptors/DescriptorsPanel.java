/**
 * Created on 2005-1-24
 *
 */
package ambit.ui.data.descriptors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ambit.data.AmbitCellEditor;
import ambit.data.IAmbitEditor;
import ambit.data.descriptors.DescriptorsList;
import ambit.exceptions.AmbitException;
import ambit.ui.AmbitColors;
import ambit.ui.domain.ADCorePanel;


/**
 * a GUI panel for {@link ambit.data.descriptors.DescriptorsList} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DescriptorsPanel extends ADCorePanel implements IAmbitEditor {
	protected JTable table;
	protected JComboBox comboBox;
	DescriptorsTableModel model;

	/**
	 * 
	 * @param dlist List of descriptors  {@link DescriptorsList} to be displayed
	 */
	public DescriptorsPanel(DescriptorsList dlist) {
		super("Descriptors",Color.white,AmbitColors.DarkClr);
		setLayout(new BorderLayout());	
		add(createTable(dlist),BorderLayout.CENTER);
		setPreferredSize(new Dimension(400,300));

	}
	/* (non-Javadoc)
     * @see ambit.ui.CorePanel#addWidgets()
     */
    protected void addWidgets() {
		

    }
	protected JScrollPane createTable(DescriptorsList dlist) {
		model = new DescriptorsTableModel(dlist); 
		table = new JTable(model,createColumnModel(model));
        table.getTableHeader().setReorderingAllowed(false);
		table.setPreferredScrollableViewportSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
     	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
     	table.setSurrendersFocusOnKeystroke(true);
     	JScrollPane p = new JScrollPane(table);
     	p.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
     	p.setBackground(backClr);
     	p.setForeground(foreClr);
     	table.setBackground(backClr);
     	table.setForeground(foreClr);     	
		return p;
	}
	protected TableColumnModel createColumnModel(DescriptorsTableModel model) {
	    TableColumnModel m = new DefaultTableColumnModel();
	    m.addColumn(new TableColumn(0));
	    m.addColumn(new TableColumn(1));
	    m.addColumn(new TableColumn(2,32));
	    m.addColumn(new TableColumn(3));
	    m.addColumn(new TableColumn(4));
	    m.addColumn(new TableColumn(5));
	    m.getColumn(5).setCellEditor(new AmbitCellEditor());
	    
	    m.addColumn(new TableColumn(6));
	    m.getColumn(6).setCellEditor(new AmbitCellEditor());
	    
	    for (int i=0; i < 7; i++)
	        m.getColumn(i).setHeaderValue(model.getColumnName(i));
	    return m;
	}
	/*
	public void createIDsColumn(int no) {
		try {
		TableColumn idColumn = table.getColumnModel().getColumn(no);
		switch(no) {
		case(2): {
			comboBox = new JComboBox();
			for (int i =0; i < IColumnTypeSelection._columnTypesS.length; i++)
				comboBox.addItem(IColumnTypeSelection._columnTypesS[i]);
			comboBox.setEnabled(true);
				
			idColumn.setCellEditor(new DefaultCellEditor(comboBox));
			break;
		}
		case(3): {
			comboBox = new JComboBox();
			for (int i =0; i < DescriptorType._typename.length; i++)
				comboBox.addItem(DescriptorType._typename[i]);
			comboBox.setEnabled(true);
				
			idColumn.setCellEditor(new DefaultCellEditor(comboBox));
			break;
		}	
		
		}
		} catch (Exception e) {
			//TODO exception
		}

	}
	*/
	/* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#getJComponent()
     */
    public JComponent getJComponent() {
        return this;
    }
    /* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#view(javax.swing.JComponent, boolean)
     */
    public boolean view(Component parent, boolean editable, String title) throws AmbitException {
        return 
        JOptionPane.showConfirmDialog(this,getJComponent(),"Descriptors",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;

    }
    public void setEditable(boolean editable) {
    	// TODO Auto-generated method stub
    	
    }
    public boolean isEditable() {
    	return true;
    }
}
