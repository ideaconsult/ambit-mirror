package ambit2.workflow.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ambit2.core.data.SelectionBean;

/**
 * An editor for {@link SelectionBean}
 * @author nina
 *
 * @param <T>
 */
public class SelectionEditor<T> extends AbstractEditor<SelectionBean<T>> {
	protected SelectionBeanModel<T> model;
	protected JLabel title;
	/**
	 * 
	 */
	private static final long serialVersionUID = -6847653828855903389L;
	
	public SelectionEditor() {
		setLayout(new BorderLayout());
		setWfcfactory(wfcfactory);
		setAnimate(true);
		model = new SelectionBeanModel<T>();
		final JTable table = new JTable(model) {
			@Override
			public void createDefaultColumnsFromModel() {
				TableColumnModel cm = getColumnModel();
				
				while (cm.getColumnCount() > 0) {
					cm.removeColumn(cm.getColumn(0));
				}
				TableColumn c = new TableColumn(0,32);
				c.setMaxWidth(32);
				cm.addColumn(c);
				cm.addColumn(new TableColumn(1,128));
			}
		};
		table.setShowGrid(false);
		table.setBackground(getBackground());
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(false);
		table.setTableHeader(null);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane pane = new JScrollPane(table);
		pane.setBackground(getBackground());
		add(pane,BorderLayout.CENTER);	
		title = new JLabel();
		add(title,BorderLayout.NORTH);
	}

	@Override
	protected void animate(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		getObject().clear();
		
	}

	public boolean isEditable() {
		return true;
	}

	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setObject(SelectionBean<T> object) {
		super.setObject(object);
		model.setOptions(object);
		title.setText(object.getTitle());
	}

}

class SelectionBeanModel<T> extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1701333992811435992L;
	protected SelectionBean<T> options;
	
	public SelectionBean<T> getOptions() {
		return options;
	}

	public void setOptions(SelectionBean<T> options) {
		this.options = options;
		fireTableStructureChanged();
	}

	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		if (options == null) return 0;
		return options.getOptions().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (options == null) return null;
		switch (columnIndex) {
		case 0: { return options.getSelected() == options.getOptions().get(rowIndex);}
		default: return options.getOptions().get(rowIndex);
		}
	}
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:  { options.setSelected(options.getOptions().get(rowIndex));
			fireTableDataChanged();
		}
		default: return;
		}
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		return columnIndex==0;
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0: return Boolean.class;
		default: return super.getColumnClass(columnIndex);
		}	}
	
}