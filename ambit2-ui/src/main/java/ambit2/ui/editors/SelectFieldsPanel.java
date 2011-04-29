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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import ambit2.base.data.Dictionary;
import ambit2.base.data.ProfileListModel;
import ambit2.base.data.Property;
import ambit2.base.data.TypedListModel;
import ambit2.base.interfaces.IAmbitEditor;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SelectFieldsPanel extends JPanel implements ActionListener, IAmbitEditor<TypedListModel<Property>> {
	public enum MOVE {
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
			@Override
			public boolean getTag() {
				return true;
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
			@Override
			public boolean getTag() {
				return false;
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
			@Override
			public boolean getTag() {
				return true;
			}
			/*
			public void move(ambit2.base.data.TypedListModel<Property> fields, JList[] table, ambit2.base.data.TypedListModel<Property>[] fieldsModel) {
				int[] rows = table[getTableIndex()].getSelectedIndices();//getSelectedRows();
				for (int row : rows) {
					if (row >= table[getTableIndex()].getModel().getSize()) continue;
					Object o = table[getTableIndex()].getModel().getElementAt(row);

				}
			};
			*/
			
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
			@Override
			public boolean getTag() {
				return false;
			}
			
			
		};
		public abstract int getTableIndex();
		public abstract String display();
		public abstract boolean moveAll();
		public abstract boolean getTag();
		
		public void move(TypedListModel<Property> fields,JList[] table, TypedListModel<Property>[] fieldsModel) {
			if (moveAll()) {
				for (int i=0; i < table[getTableIndex()].getModel().getSize();i++) {
					Object o = table[getTableIndex()].getModel().getElementAt(i);
					if (o instanceof Dictionary) ; 
					if (o instanceof Property) ((Property)o).setEnabled(getTag());
				}
			} else {
				int[] rows = table[getTableIndex()].getSelectedIndices();//getSelectedRows();
				for (int row : rows) {
					if (row >= table[getTableIndex()].getModel().getSize()) continue;
					Object o = table[getTableIndex()].getModel().getElementAt(row);
					if (o instanceof Dictionary) ;
					if (o instanceof Property) ((Property)o).setEnabled(!((Property)o).isEnabled());
				}
			}
			if (fields instanceof AbstractListModel) {
				ListDataEvent event = new ListDataEvent(fields,ListDataEvent.CONTENTS_CHANGED,0,fields.getSize()-1);
				for (ListDataListener listener :((AbstractListModel)fields).getListDataListeners()) {
					listener.contentsChanged(event);
				}
			}
			
		}
		};
			
	//protected JTextPane helpArea ;
	protected List<JButton> buttons;
	protected TypedListModel<Property> fields;
	protected TypedListModel<Property>[] fieldsModel;
	//protected Profile<Property> profile;
	protected JList[] tables;
	protected String help;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -256267185521731191L;
	
	public SelectFieldsPanel() {
		this(null,"");
	}		

	public SelectFieldsPanel(TypedListModel<Property> fields,String help) {
		this(fields,
			new ProfileListModel(fields,false),
			new ProfileListModel(fields,true),
			help);
	}	
	
	protected SelectFieldsPanel(TypedListModel<Property> fields,
			TypedListModel<Property> allFields, 
			TypedListModel<Property> selectedFields,String help) {
		super();
		this.help = help;
		setObject(fields);
	}
	protected void buildPanel(String help) {
		FormLayout layout = new FormLayout(
	            "3dlu,fill:40dlu:grow,fill:120dlu:grow,3dlu,32dlu, 3dlu, fill:40dlu:grow,fill:120dlu:grow,3dlu",  //columns
				"3dlu,12dlu,3dlu,top:[pref,36dlu], 24dlu,3dlu,24dlu,3dlu,24dlu,3dlu,24dlu,bottom:[pref,72dlu]:grow,1dlu,pref,pref,pref,pref,pref");  //rows
		setLayout(layout);
        CellConstraints cc = new CellConstraints();
	    
        add(createSeparator("Available"),cc.xywh(2,2,2,1));
        add(createSeparator("Selected"),cc.xywh(7,2,2,1));
        
        tables = new JList[fieldsModel.length];
        for (int i=0; i < fieldsModel.length;i++) {
        	tables[i] = new JList(fieldsModel[i]);
        	
        	SelectionInList<Property> selectionInList = new SelectionInList<Property>(fieldsModel[i]);
            Bindings.bind(tables[i], selectionInList);
        	JScrollPane p = new JScrollPane(tables[i]);
        	p.setBorder(BorderFactory.createEtchedBorder());
        	p.setPreferredSize(new Dimension(160,280));
        	int offset = 2+i*5;
        	add(p,cc.xywh(offset,4,2,9));
        	
	        BeanAdapter beanAdapter = new BeanAdapter(selectionInList);
	        
	        String[][] config = {
	        		{"name","Name"},
	        		{"label","Alias"},
	        		{"units","Units"},
	        		{"title","url"},
	        		{"url","WWW"}
	        };
	        for (int j=0; j < config.length;j++) {
	        	String[] c = config[j];
	        	ValueModel model = beanAdapter.getValueModel(c[0]);
	        	add(BasicComponentFactory.createLabel(new ValueHolder(c[1])),cc.xywh(offset,j+14,1,1));
	        	JTextField t = BasicComponentFactory.createTextField(model);
	        	t.addMouseListener(new MouseAdapter() {
	        		@Override
	        		public void mouseEntered(MouseEvent e) {
	        			super.mouseEntered(e);
	        			((JTextField)e.getSource()).setToolTipText(((JTextField)e.getSource()).getText());
	        		}
	        	});
	        	add(t,cc.xywh(offset+1,j+14,1,1));	   
	        }
	        /*
	        ValueModel nameModel = beanAdapter.getValueModel("name");
	        ValueModel commentsModel = beanAdapter.getValueModel("label");
	        ValueModel titleModel = beanAdapter.getValueModel("title");
	        ValueModel urlModel = beanAdapter.getValueModel("url");	        
			//p.add(BasicComponentFactory.createTextField(parentModel));	      
	        add(BasicComponentFactory.createLabel(new ValueHolder("Name")),cc.xywh(offset,14,1,1));
			add(BasicComponentFactory.createTextField(nameModel),cc.xywh(offset+1,14,1,1));
			add(BasicComponentFactory.createLabel(new ValueHolder("Alias")),cc.xywh(offset,15,1,1));
			add(BasicComponentFactory.createTextField(commentsModel),cc.xywh(offset+1,15,1,1));
			add(BasicComponentFactory.createLabel(new ValueHolder("Reference")),cc.xywh(offset,16,1,1));
			add(BasicComponentFactory.createTextField(titleModel),cc.xywh(offset+1,16,1,1));			
			add(BasicComponentFactory.createLabel(new ValueHolder("WWW")),cc.xywh(offset,17,1,1));
			add(BasicComponentFactory.createTextField(urlModel),cc.xywh(offset+1,17,1,1));	  
			*/      
        }
        
        
        
        buttons = new ArrayList<JButton>();
        int i=0;
        for (MOVE op : MOVE.values()) {
        	JButton button = new JButton(op.display());
        	button.setPreferredSize(new Dimension(24,24));
        	button.setMaximumSize(new Dimension(24,24));
        	add(button,cc.xywh(5,5+(i*2),1,1));
        	button.addActionListener(this);
        	button.setActionCommand(op.toString());
        	i++;
        }
        
        /*
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
        */
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
    		//Object o = table[1].getModel().getElementAt(row);
    	} catch (Exception x) {
    		x.printStackTrace();
    	}
        
    	
    }

	public JComponent getJComponent() {
		return this;
	}

	public TypedListModel<Property> getObject() {
		return fields;
	}

	public boolean isEditable() {
		return true;
	}

	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		
	}

	public void setObject(TypedListModel<Property> object) {
		this.fields = object;
		fieldsModel = new ProfileListModel[2];
		this.fieldsModel[0] = new ProfileListModel(object,false);
		this.fieldsModel[1] = new ProfileListModel(object,true);
		buildPanel(help);
		
		
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}
	public boolean confirm() {
		return true;
	}	
}


