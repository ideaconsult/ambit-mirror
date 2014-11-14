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
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import ambit2.base.data.Dictionary;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.db.results.AmbitRows;
import ambit2.db.results.MemoryRowsModel;
import ambit2.db.results.RowsModel;
import ambit2.db.search.DictionaryObjectQuery;
import ambit2.db.search.DictionaryQuery;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.TemplateQuery;
import ambit2.dbui.QueryEditor;
import ambit2.ui.Utils;
import ambit2.ui.editors.SelectFieldsPanel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DictionaryQueryPanel  extends QueryEditor<String, String, StringCondition,Dictionary,DictionaryQuery<Dictionary>>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7515359779806114282L;
	protected AmbitRows<Dictionary> parents;
	protected AmbitRows<Property> details;
	protected List<Dictionary> path;
	protected Profile<Property> profile = new Profile<Property>();
	
	public Profile<Property> getProfile() {
		profile.setChanged();
		return profile;
	}
	@Override
	public boolean confirm() {
		profile.setChanged();
		return super.confirm();
	}
	public void setProfile(Profile<Property> profile) {
		this.profile = profile;
	}

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
		details = new AmbitRows<Property>();

	}
	public JComponent buildPanel() {
		initRows();
		FormLayout layout = new FormLayout(
	            "331dlu",
				"pref,48dlu,pref:grow");        
	     PanelBuilder panel = new PanelBuilder(layout);
	     panel.setDefaultDialogBorder();

	     CellConstraints cc = new CellConstraints();   
	     JComponent c = null;
	     
	     c = createNavigator(panel.getPanel(),path);
	    // c.setBackground(panel.getPanel().getBackground());
	    // c.setBorder(BorderFactory.createRaisedBevelBorder());
	     panel.addSeparator("Filter by templates", cc.xywh(1,1,1,1));
	     panel.add(c, cc.xywh(1,2,1,1));
	     panel.add(createValueComponent(), cc.xywh(1,3,1,1));	  	     
	     return panel.getPanel();
	}
	protected JComponent createNavigator(final JComponent parent,List<Dictionary> path) {
		
		final PathComboBoxEditor editor = new PathComboBoxEditor((JComboBox)createFieldnameComponent());
		final PathNavigationRenderer renderer = new PathNavigationRenderer();
		final TableCellRenderer textRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				if (column==0) return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (value != null)
					((JLabel) c).setText(((Dictionary)value).getTemplate());
				else 
					((JLabel) c).setText("All");
				return c;
			}
		};
		JTable table = new JTable(new SimplePathTableModel(path,true)) {
			/*
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
			*/
			@Override
			public Component prepareEditor(TableCellEditor editor, int row,
					int column) {
				try {
					Object o = getValueAt(row, column);
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
				return textRenderer;
			}
			@Override
			public TableCellEditor getCellEditor(int row, int column) {
				return editor;
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
					if (i==0) {
						newColumn.setWidth(18);
						newColumn.setMinWidth(18);
						newColumn.setMaxWidth(48);
					}
					addColumn(newColumn);
		        				    
				}
			}
		};
		table.setCellSelectionEnabled(true);
		table.setTableHeader(null);
		table.setShowVerticalLines(false);
	    return new JScrollPane(table);

	}
	/*
	protected JComponent createNavigator(final JComponent parent,List<Dictionary> path) {
	
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
		JTable table = new JTable(new PathTableModel(path,true)) {
			
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
	    return new JScrollPane(table);

	}
	*/
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
		final MemoryRowsModel<Property> properties = new MemoryRowsModel<Property>(details) {
			@Override
			public void prepareList() {
				for (int i=content.size()-1; i >=0; i--)
					if (!content.get(i).isEnabled())
						content.remove(i);

			}
			@Override
			public void updateProfile(Profile profile) {
				profile.clear();
				for (int i=content.size()-1; i >=0; i--)
					if (content.get(i).isEnabled())
						profile.add(content.get(i));

			}
			
	
		};
		SelectFieldsPanel selectFields = new SelectFieldsPanel() {
			@Override
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				properties.updateProfile(profile);			
			}
		};
		selectFields.setObject(properties);
		return selectFields;
	}
	/*
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
						tq.setValue(((Dictionary)evt.getNewValue()).getTemplate());
						details.setQuery(tq);
						if (details.size()==0)
							details.close();
						else {
							details.beforeFirst();
							while (details.next()) {
								profile.add(details.getObject());
							}
						}
					}

				} catch (Exception x) {
					x.printStackTrace();
				}
				
			}
		});		
	
		return BasicComponentFactory.createComboBox(fieldnames);

	}
	*/
	protected JComponent createFieldnameComponent() {
		ValueHolder vh = new ValueHolder();
		SelectionInList<Dictionary> fieldnames = new SelectionInList<Dictionary>(
					new RowsModel<Dictionary>(parents),vh);
		fieldnames.addPropertyChangeListener("value",new PropertyChangeListener() {
			final TemplateQuery tq = new TemplateQuery();
			public void propertyChange(PropertyChangeEvent evt) {
				try {

					if (evt.getNewValue()!= null) {
						tq.setCondition(StringCondition.getInstance(StringCondition.C_EQ));			
						tq.setValue(((Dictionary)evt.getNewValue()).getTemplate());
						details.setQuery(tq);
						if (details.size()==0)
							details.close();
						else {
							details.beforeFirst();
							while (details.next()) {
								profile.add(details.getObject());
							}
						}
					}

				} catch (Exception x) {
					x.printStackTrace();
				}
				
			}
		});		
	
	
		JComboBox box = BasicComponentFactory.createComboBox(fieldnames);
		box.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
				if ((value != null) &&(c instanceof JLabel)) 
					((JLabel)c).setText(((Dictionary)value).getTemplate());
				return c;
			}
		});
		return box;

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

class SimplePathTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2784257029846153380L;
	protected String[] name = {"#","Templates (hierarchical)"};
	protected List<Dictionary> path;
	public SimplePathTableModel() {
		this (new ArrayList<Dictionary>(),true);
	}
	
	public SimplePathTableModel(List<Dictionary> path, boolean columns) {
		super();
		this.path = path;

	}

	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return path.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 1: return path.get(rowIndex);
		default: {
			StringBuilder b = new StringBuilder();
			for (int i=0; i < rowIndex;i++) b.append(">");
			//return Integer.toString(rowIndex+1)+"."; 
			return b.toString();
		}
		}
		
		
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex==1;
	}
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (value==null) return;
		if (columnIndex == 1) {
			for (int i=path.size()-1; i>rowIndex;i--)
					path.remove(i);		
				path.add((Dictionary)value);
			fireTableStructureChanged();
		}
	}
	@Override
	public String getColumnName(int column) {
		return name[column];
	}
}

class PathTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2784257029846153380L;
	protected List<Dictionary> path;
	protected boolean columns = true;
	public PathTableModel() {
		this (new ArrayList<Dictionary>(),true);
	}
	
	public PathTableModel(List<Dictionary> path, boolean columns) {
		super();
		this.path = path;
		this.columns = columns;

	}

	public int getColumnCount() {
		if (columns) return path.size()*2;
		else return 1;
	}

	public int getRowCount() {
		if (columns) return 1;
		else return path.size()*2;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		int index = (columns)?(columnIndex%2):(rowIndex%2);
		if (index==0) {
			return path.get((columns)?(columnIndex/2):(rowIndex/2));
		}
		else return null;
		
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columns)?(columnIndex%2)==1:(rowIndex%2)==1;
	}
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columns) {
			if ((columnIndex%2)==1) {
				int index = (columnIndex-1)/2;
				if (value != null) {
					for (int i=path.size()-1; i>index;i--)
						path.remove(i);				
					path.add((Dictionary)value);
					fireTableStructureChanged();
				}
			}
		} else {
			if ((rowIndex%2)==1) {
				int index = (rowIndex-1)/2;
				if (value != null) {
					for (int i=path.size()-1; i>index;i--)
						path.remove(i);				
					path.add((Dictionary)value);
					fireTableStructureChanged();
				}
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
