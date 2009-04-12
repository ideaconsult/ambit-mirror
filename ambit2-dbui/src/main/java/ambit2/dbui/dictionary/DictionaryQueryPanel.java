/* DictionaryQueryPanel.java
 * Author: nina
 * Date: Feb 6, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
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

package ambit2.dbui.dictionary;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.results.AmbitRows;
import ambit2.db.results.RowsModel;
import ambit2.db.search.DictionaryObjectQuery;
import ambit2.db.search.DictionaryQuery;
import ambit2.db.search.StringCondition;
import ambit2.db.search.TemplateQuery;
import ambit2.dbui.QueryEditor;
import ambit2.ui.Utils;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DictionaryQueryPanel  extends QueryEditor<String, String, StringCondition,Dictionary,DictionaryQuery>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7515359779806114282L;
	protected AmbitRows<Dictionary> parents;
	protected AmbitRows details;
	protected List<Dictionary> path;
	public DictionaryQueryPanel() {
		super();

	}

	protected void initRows() {
		path = new ArrayList<Dictionary>();
		path.add(null);//new Dictionary("Dataset",null));
	
		parents = new AmbitRows<Dictionary>() {
			@Override
			protected synchronized IQueryRetrieval<Dictionary> createNewQuery(
					Dictionary target) throws AmbitException {
				return new DictionaryObjectQuery(target.getTemplate()); 
				
			}			
		};
		parents.setPropertyname("parents");
		details = new AmbitRows();
	
	}
	public JComponent buildPanel() {
		initRows();
		FormLayout layout = new FormLayout(
	            "331dlu",
				"50dlu,50dlu");        
	     PanelBuilder panel = new PanelBuilder(layout);
	     panel.setDefaultDialogBorder();

	     CellConstraints cc = new CellConstraints();   
	     JComponent c = null;
	     
	     c = createNavigator(panel.getPanel(),path);
	     c.setBackground(panel.getPanel().getBackground());
	     c.setBorder(BorderFactory.createRaisedBevelBorder());
	     panel.add(new JScrollPane(c), cc.xywh(1,1,1,1));
	     panel.add(createValueComponent(), cc.xywh(1,2,1,1));	  	     
	     return panel.getPanel();
	}		
	protected JTable createNavigator(final JComponent parent,List<Dictionary> path) {
	
		final PathComboBoxEditor editor = new PathComboBoxEditor((JComboBox)createFieldnameComponent());
		final PathNavigationRenderer renderer = new PathNavigationRenderer();
		final TableCellRenderer textRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (value != null)
					((JLabel) c).setText(((Dictionary)value).getTemplate());
				else 
					((JLabel) c).setText("ALL");
				return c;
			}
		};
		JTable table = new JTable(new PathTableModel(path)) {
			
			@Override
			public Rectangle getCellRect(int row, int column,
					boolean includeSpacing) {
				if ((column %2) == 1) {
					Rectangle r1 = super.getCellRect(row, column-1, includeSpacing);
					Rectangle r2 = super.getCellRect(row, column, includeSpacing);
					Rectangle r = new Rectangle(r2.x,r2.y,r1.width+r2.width,r2.height);
					return r;
				} else
					return super.getCellRect(row, column, includeSpacing);
			}
			@Override
			public Component prepareEditor(TableCellEditor editor, int row,
					int column) {
				try {
					Object o = getValueAt(row, column-1);
					if (o == null)
						parents.setQuery(new DictionaryObjectQuery());
					else if (o instanceof Dictionary)
						parents.setQuery(new DictionaryObjectQuery((Dictionary)o));
					if (parents.size() == 0) return null;
				} catch (Exception x) {
					x.printStackTrace();
				}
				return super.prepareEditor(editor, row, column);
			}
			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				if ((column %2) == 1) return renderer;
				else return textRenderer;
			}
			@Override
			public TableCellEditor getCellEditor(int row, int column) {
				if ((column %2) == 1) return editor;
				else return super.getCellEditor(row, column);
			}
			
			@Override
			public void createDefaultColumnsFromModel() {
				TableColumnModel cm = getColumnModel();
				
				while (cm.getColumnCount() > 0) {
					cm.removeColumn(cm.getColumn(0));
				}
				for (int i = 0; i < getModel().getColumnCount(); i++) {
					TableColumn newColumn = new TableColumn();
					newColumn.setModelIndex(i);
					addColumn(newColumn);
		        				    
				}

				FontMetrics fm = parent.getFontMetrics(parent.getFont());
				for (int i = 0; i < getModel().getColumnCount(); i++) {
					TableColumn newColumn = getColumnModel().getColumn(i);
					int w = 100;
					try {
						Object o = getValueAt(0,i);
						if (o!=null) {
							w = (int)Math.ceil(20+fm.stringWidth(((Dictionary)o).getTemplate().toString()));
						}
					} catch (Exception x) {
						x.printStackTrace();
					}					
					newColumn.setWidth(((i%2)==0)?w:18);
					newColumn.setMinWidth(((i%2)==0)?w:18);
					newColumn.setMaxWidth(((i%2)==0)?w:18);					
				}
			}
		};
		table.setCellSelectionEnabled(true);
		table.setRowHeight(24);
		table.setTableHeader(null);
	    return table;

	}
	@Override
	protected JComponent createConditionComponent() {
		return BasicComponentFactory.createComboBox(
                new SelectionInList<StringCondition>(
                		new StringCondition[] {
                			StringCondition.getInstance(StringCondition.C_EQ),
                			StringCondition.getInstance(StringCondition.C_LIKE),
                			StringCondition.getInstance(StringCondition.C_NOTLIKE),
                			StringCondition.getInstance(StringCondition.C_REGEXP),
                			StringCondition.getInstance(StringCondition.C_SOUNDSLIKE),
                		},
                		presentationModel.getModel("condition")));
	}
	@Override
	protected JComponent createValueComponent() {

		ListModel fieldnames = new RowsModel(details);		
        final JList jlist = new JList();
        Bindings.bind(jlist, new SelectionInList<Property>(fieldnames));		
		return new JScrollPane(jlist);			
	}
	@Override
	protected JComponent createFieldnameComponent() {
		ValueHolder vh = new ValueHolder();
		SelectionInList<Dictionary> fieldnames = new SelectionInList<Dictionary>(new RowsModel<Dictionary>(parents),vh);
		fieldnames.addPropertyChangeListener("value",new PropertyChangeListener() {
			final TemplateQuery tq = new TemplateQuery();
			public void propertyChange(PropertyChangeEvent evt) {
				try {

					if (evt.getNewValue()!= null) {
						tq.setCondition(StringCondition.getInstance(StringCondition.C_EQ));			
						System.out.println(evt.getNewValue());
						tq.setValue(((Dictionary)evt.getNewValue()).getTemplate());
						details.setQuery(tq);
						if (details.size()==0)
							details.close();
					}

				} catch (Exception x) {
					x.printStackTrace();
				}
				
			}
		});		
	
		return BasicComponentFactory.createComboBox(fieldnames);

	}
	public void open() throws DbAmbitException {
		try {
			parents.setQuery(new DictionaryObjectQuery());
		} catch (Exception x) {
			throw new DbAmbitException(this,x);
		}
		
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		parents.setConnection(connection);
		details.setConnection(connection);
	}
}

class PathTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2784257029846153380L;
	protected List<Dictionary> path;
	
	public PathTableModel() {
		this (new ArrayList<Dictionary>());
	}
	
	public PathTableModel(List<Dictionary> path) {
		super();
		this.path = path;

	}

	public int getColumnCount() {
		return path.size()*2;
	}

	public int getRowCount() {
		return 1;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if ((columnIndex%2)==0) {
			return path.get(columnIndex/2);
		}
		else return null;
		
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex%2)==1;
	}
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if ((columnIndex%2)==1) {
			int index = (columnIndex-1)/2;
			if (value != null) {
				for (int i=path.size()-1; i>index;i--)
					path.remove(i);				
				path.add((Dictionary)value);
				fireTableStructureChanged();
			}
		}
	}
}

class PathNavigationRenderer extends JLabel implements TableCellRenderer {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6927150394267845945L;

	public PathNavigationRenderer() {
        super();
		try {
			setIcon(Utils.createImageIcon("images/resultset_next.png"));
		} catch (Exception x) {
			x.printStackTrace();
		}        
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        return this;
    }
}
class PathComboBoxRenderer extends JComboBox implements TableCellRenderer {
    public PathComboBoxRenderer(String[] items) {
        super(items);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        setSelectedItem(value);
        return this;
    }
}

class PathComboBoxEditor extends DefaultCellEditor {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6710017686133430495L;

	public PathComboBoxEditor(JComboBox box) {
        super(box);
    }
}
