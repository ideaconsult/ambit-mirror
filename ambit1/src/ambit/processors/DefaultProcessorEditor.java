package ambit.processors;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import ambit.data.IAmbitEditor;
import ambit.exceptions.AmbitException;

/**
 * 
 * Default visualisation for {@link ambit.processors.IAmbitProcessor}. Just shows toString() result in a text field. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class DefaultProcessorEditor extends DefaultCellEditor implements IAmbitEditor {
    protected Component textField;
	protected IAmbitProcessor processor;
	protected boolean editable=false;
	
	public DefaultProcessorEditor(IAmbitProcessor processor) {
		super(new JTextField());
		textField = getComponent();
		this.processor = processor;
	}
	public boolean view(Component parent, boolean editable, String title) throws AmbitException {
        textField.setEnabled(editable);
		return JOptionPane.showConfirmDialog(parent,getComponent(),processor.toString(),
				JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;

	}
	
	public JComponent getJComponent() {
		if (processor != null) {
		    ((JTextField) textField).setText(processor.toString());
		}    
		return ((JTextField) textField);
	}
	
	/* (non-Javadoc)
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        if (value instanceof IAmbitProcessor) 
            this.processor = (IAmbitProcessor) processor;
        return getComponent();
    }
    /* (non-Javadoc)
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    public boolean isCellEditable(EventObject anEvent) {
        return false;
    }
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
