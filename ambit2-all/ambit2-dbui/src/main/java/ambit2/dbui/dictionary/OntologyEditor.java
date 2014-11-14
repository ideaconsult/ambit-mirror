package ambit2.dbui.dictionary;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IAmbitEditor;
import ambit2.db.processors.ProcessorOntology;
import ambit2.db.processors.ProcessorOntology.OP;
import ambit2.db.processors.ProcessorOntology.SIDE;
import ambit2.db.results.AmbitRows;
import ambit2.db.results.RowsModel;
import ambit2.db.search.property.QueryOntology;
import ambit2.ui.EditorPreferences;
import ambit2.ui.Utils;
import ambit2.ui.editors.ListEditor;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class OntologyEditor extends AbstractDBProcessor<Object, Dictionary> implements IAmbitEditor<QueryOntology> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6900524455040663939L;
	protected List<JButton> buttons;
	protected ProcessorOntology processor = new ProcessorOntology();
	protected QueryOntology[] query = new QueryOntology[SIDE.values().length];
	protected ListEditor[] listEditor = new ListEditor[SIDE.values().length];
	protected AmbitRows<Property>[] rows = new AmbitRows[SIDE.values().length];
	protected ImageIcon folderIcon = null;
	
	protected JComponent component;
	
	public OntologyEditor() {
		try {folderIcon = Utils.createImageIcon("images/folder.png");} catch (Exception x) {}
        for (SIDE side : SIDE.values()) {
        	query[side.ordinal()] = new QueryOntology();
        	query[side.ordinal()].setValue(side.equals(SIDE.LEFT)?new Dictionary("Endpoints",null):new Dictionary("Dataset",null));
        	rows[side.ordinal()] = new AmbitRows<Property>();
			listEditor[side.ordinal()] = new ListEditor(new RowsModel(rows[side.ordinal()])) {
				@Override
				protected IAmbitEditor getEditor(Object object) {
					try {
						return EditorPreferences.getEditor(object);
					} catch (Exception x) {
						x.printStackTrace();
						return null;
					}
				}
				@Override
				protected void handleDoubleClick(Object selection) {
					if (selection instanceof Dictionary) 
						for (SIDE side : SIDE.values()) 
						if (listEditor[side.ordinal()]==this) try {
							String value = query[side.ordinal()].getFieldname()?query[side.ordinal()].getValue().getTemplate():query[side.ordinal()].getValue().getParentTemplate();
							System.out.println(value);
							boolean parent =  ((Dictionary)selection).getTemplate().equals(value);
							query[side.ordinal()].setValue((Dictionary)selection);							
							query[side.ordinal()].setFieldname(!parent);
							rows[side.ordinal()].setQuery(query[side.ordinal()]);						
						} catch (AmbitException x) {
							x.printStackTrace();
						}
				}
				
			};	
			listEditor[side.ordinal()].getList().setCellRenderer(new DefaultListCellRenderer() {
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if (index == 0)
						((JLabel)c).setForeground(Color.blue);
					if (value instanceof Dictionary) {
						((JLabel)c).setIcon(folderIcon);
					}
					return c;
				}
			});
			rows[side.ordinal()].addPropertyChangeListener(side.toString(), new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					System.out.println(evt.getPropertyName());
					
				}
			});
			
        }
        component  = buildPanel("");
	}
	/**
						 query[side.ordinal()].setValue(((Dictionary)selected).getTemplate());									 
										 rows[side.ordinal()].setQuery(query[side.ordinal()]);
	 */
	/*
	 * (non-Javadoc)
	 * @see ambit2.db.AbstractDBProcessor#setConnection(java.sql.Connection)
	 */
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		for (SIDE side : SIDE.values()) {
			rows[side.ordinal()].setConnection(connection);
			try {
			rows[side.ordinal()].setQuery(query[side.ordinal()]);
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		processor.setConnection(connection);
	}
	public boolean confirm() {
		try {
	
		processor.close();
		for (SIDE side : SIDE.values()) 
			rows[side.ordinal()].close();
		} catch (Exception x) {}
		return false;
	}
	public JComponent getJComponent() {

		return component;
	}
	protected JComponent buildPanel(String help) {
		FormLayout layout = new FormLayout(
	            "3dlu,fill:60dlu:grow,fill:80dlu:grow,3dlu,32dlu, 3dlu, fill:60dlu:grow,fill:80dlu:grow,3dlu",  //columns
				"3dlu,12dlu,3dlu,top:[pref,36dlu], 24dlu,3dlu,24dlu,3dlu,24dlu,3dlu,24dlu,bottom:[pref,72dlu]:grow,1dlu,pref,pref,pref,pref,pref");  //rows
	    PanelBuilder builder = new PanelBuilder(layout);
	   // builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
	    
        builder.add(createSeparator("Template navigator"),cc.xywh(2,2,8,1));

 
        for (SIDE side : SIDE.values())
        	builder.add(listEditor[side.ordinal()].getJComponent(),cc.xywh(2+side.ordinal()*5,4,2,9));
   	
        buttons = new ArrayList<JButton>();
        int i=0;
        
        for (SIDE op: SIDE.values()) {
        	JButton button = new JButton(op.display());
        	button.setPreferredSize(new Dimension(24,24));
        	button.setMaximumSize(new Dimension(24,24));
        	builder.add(button,cc.xywh(5,5+(i*2),1,1));
        	button.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			SIDE side = (SIDE.valueOf(e.getActionCommand()));
        				try { 
	        				for (SIDE s : SIDE.values()) 
	        					processor.setTemplate(query[s.ordinal()].getValue(),s);
	        				processor.setCurrentSide(side);
	        				processor.setOperation(OP.MOVE);
	        				processor.process(listEditor[side.ordinal()].getSelected());  
	        				for (SIDE s : SIDE.values()) {
	        					rows[s.ordinal()].setQuery(query[s.ordinal()]);
	        				}
        				} catch (Exception x) {x.printStackTrace();}        					
        		
        			
        			
        		}
        	});
        	button.setActionCommand(op.toString());
        	i++;
        }
        return builder.getPanel();
       
	}	
    private Component createSeparator(String textWithMnemonic) {
        return DefaultComponentFactory.getInstance().createSeparator(
                textWithMnemonic);
    }	
    public Dictionary process(Object target) throws AmbitException {
    	return processor.process(target);
    }

	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}
	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		
	}
	public void setObject(QueryOntology object) {
		/*
		query[SIDE.LEFT.ordinal()] = object;
		try {
		rows[SIDE.LEFT.ordinal()].setQuery(query[SIDE.LEFT.ordinal()]);
		} catch (Exception x) {
			
		}
		*/
		
	}
	public QueryOntology getObject() {
		return query[SIDE.LEFT.ordinal()];
	}
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
    
    
    
}
