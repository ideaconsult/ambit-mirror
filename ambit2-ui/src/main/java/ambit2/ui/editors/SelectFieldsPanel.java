/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.ui.editors;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import ambit2.core.data.Profile;
import ambit2.core.data.Property;
import ambit2.ui.PropertiesTableModel;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SelectFieldsPanel extends JPanel implements ActionListener, IAmbitEditor<Profile> {
	protected enum MOVE {
		MOVE_RIGHT {
			@Override
			public String display() {
				return ">";
			}
			@Override
			public boolean moveAll() {
				return false;
			}
			@Override
			public int getTableIndex() {
				return 0;
			}
		},
		MOVE_LEFT {
			@Override
			public String display() {
				return "<";
			}
			@Override
			public boolean moveAll() {
				return false;
			}
			@Override
			public int getTableIndex() {
				return 1;
			}
			
		},
		MOVE_ALL_RIGHT {
			@Override
			public String display() {
				return ">>";
			}
			@Override
			public boolean moveAll() {
				return true;
			}
			public int getTableIndex() {
				return 0;
			}
			
			
		},
		MOVE_ALL_LEFT {
			@Override
			public String display() {
				return "<<";
			}
			@Override
			public boolean moveAll() {
				return true;
			}
			public int getTableIndex() {
				return 1;
			}
			
			
		};
		public abstract int getTableIndex();
		public abstract String display();
		public abstract boolean moveAll();
		public void move(Profile fields, JTable[] table, PropertiesTableModel[] fieldsModel) {
			int[] rows = table[getTableIndex()].getSelectedRows();
			for (int row : rows) {
				Object o = table[getTableIndex()].getValueAt(row,0);
				if (o instanceof Property) {
					((Property)o).setEnabled(!((Property)o).isEnabled());
				}
				if (!moveAll()) break;
			}
			
			for (PropertiesTableModel model : fieldsModel)
				model.setFields(fields);

		}
		};
			
	//protected JTextPane helpArea ;
	protected List<JButton> buttons;
	protected Profile fields;
	protected PropertiesTableModel[] fieldsModel;
	protected JTable[] tables;
	protected String help;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -256267185521731191L;
	
	public SelectFieldsPanel() {
		this(null,"");
	}		

	public SelectFieldsPanel(Profile fields,String help) {
		this(fields,
			new PropertiesTableModel(fields,false,1),
			new PropertiesTableModel(fields,true,2),
			help);
	}	
	
	protected SelectFieldsPanel(Profile fields,
			PropertiesTableModel allFields, 
			PropertiesTableModel selectedFields,String help) {
		super();
		this.help = help;
		setObject(fields);
	}
	protected void buildPanel(String help) {
		FormLayout layout = new FormLayout(
	            "3dlu,fill:160dlu:grow,3dlu,32dlu, 3dlu, fill:160dlu:grow,3dlu",  //columns
				"3dlu,12dlu,3dlu,top:[pref,36dlu], 24dlu,3dlu,24dlu,3dlu,24dlu,3dlu,24dlu,bottom:[pref,72dlu]:grow,1dlu,24dlu,1dlu");  //rows
		setLayout(layout);
        CellConstraints cc = new CellConstraints();
	    
        add(createSeparator("Available"),cc.xywh(2,2,1,1));
        add(createSeparator("Selected"),cc.xywh(6,2,1,1));
        
        tables = new JTable[fieldsModel.length];
        for (int i=0; i < fieldsModel.length;i++) {
        	tables[i] = new JTable(fieldsModel[i]);
        	JScrollPane p = new JScrollPane(tables[i]);
        	p.setBorder(BorderFactory.createEtchedBorder());
        	p.setPreferredSize(new Dimension(160,280));
        	add(p,cc.xywh(2+i*4,4,1,9));
        }
        
        //p1.setPreferredSize(new Dimension(160,216));
//        table2.setFillsViewportHeight(true);

        
        buttons = new ArrayList<JButton>();
        int i=0;
        for (MOVE op : MOVE.values()) {
        	JButton button = new JButton(op.display());
        	button.setPreferredSize(new Dimension(24,24));
        	button.setMaximumSize(new Dimension(24,24));
        	add(button,cc.xywh(4,5+(i*2),1,1));
        	button.addActionListener(this);
        	button.setActionCommand(op.toString());
        	i++;
        }
        
        JToolBar toolBar[] = new JToolBar[2];
        toolBar[0] = new JToolBar();
        toolBar[0].add(new JButton("Find"));
        toolBar[0].add(new JToolBar.Separator());
        toolBar[0].add(new JButton("Select all"));
        toolBar[0].add(new JButton("Unselect all"));
        toolBar[0].setFloatable(false);
        add(toolBar[0],cc.xywh(2,14,1,1));
        
        toolBar[1] = new JToolBar();
        toolBar[1].add(new JButton("Move Up"));
        toolBar[1].add(new JButton("Move Down"));
        toolBar[1].add(new JToolBar.Separator());
        toolBar[1].add(new JButton("Select all"));
        toolBar[1].add(new JButton("Unselect all"));
        toolBar[1].setFloatable(false);
        add(toolBar[1],cc.xywh(6,14,1,1));
        /*
        helpArea = new JTextPane();
        helpArea.setText(help);
        helpArea.setEditable(false);
        helpArea.setBackground(new Color(255,255,225));
        helpArea.setPreferredSize(new Dimension(288,48));
        add(new JScrollPane(helpArea),cc.xywh(2,16,5,1));
        */
        
	}
    private Component createSeparator(String textWithMnemonic) {
        return DefaultComponentFactory.getInstance().createSeparator(
                textWithMnemonic);
    }
    public void actionPerformed(ActionEvent e) {
    	try {
    		MOVE op = MOVE.valueOf(e.getActionCommand());
    		op.move(fields, tables,fieldsModel);
    	} catch (Exception x) {
    		x.printStackTrace();
    	}
        
    	
    }

	public JComponent getJComponent() {
		return this;
	}

	public Profile getObject() {
		return fields;
	}

	public boolean isEditable() {
		return true;
	}

	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		
	}

	public void setObject(Profile object) {
		this.fields = object;
		/*
		Collections.sort(fields,new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		for (int i=0; i < fields.size();i++)
			fields.get(i).setOrder((i+1)*100);
			*/
		fieldsModel = new PropertiesTableModel[2];
		this.fieldsModel[0] = new PropertiesTableModel(fields,false,1);
		this.fieldsModel[1] = new PropertiesTableModel(fields,true,2);
		buildPanel(help);
		
		
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}
}


