package ambit2.dbui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListModel;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.results.AmbitRows;
import ambit2.db.results.RowsModel;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.PropertyStatsString;
import ambit2.db.search.property.RetrieveFieldNamesByType;
import ambit2.db.search.structure.QueryField;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.ComboBoxAdapter;
import com.jgoodies.binding.list.SelectionInList;

public class QueryFieldEditor extends QueryEditor<Property,String, StringCondition,IStructureRecord,QueryField>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4483885648170842874L;
	protected AmbitRows<Property> properties;
	protected AmbitRows<String> stats;
	protected PropertyStatsString queryStringStartsWith;
	@Override
	public JComponent buildPanel() {
		properties = new AmbitRows<Property>() {
			@Override
			protected synchronized IQueryRetrieval createNewQuery(
					Property target) throws AmbitException {
				queryStringStartsWith.setFieldname(target);
				queryStringStartsWith.setValue(presentationModel.getValue("value").toString());
				return queryStringStartsWith;
			}
		};
		properties.setPropertyname("property");
		stats = new AmbitRows<String>();
		properties.addPropertyChangeListener(properties.getPropertyname(),stats);
		queryStringStartsWith = new PropertyStatsString();
		queryStringStartsWith.setCondition(
					StringCondition.getInstance(StringCondition.STRING_CONDITION.S_STARTS_WITH.getName()));
		return super.buildPanel();
	}
	@Override
	protected JComponent createFieldnameComponent() {
		
		ListModel fieldnames = new RowsModel<Property>(properties) {
			@Override
			public int getSize() {
				return super.getSize()+1;
			}
			@Override
			public Property getElementAt(int index) {
				if (index == 0) return null;
				else
					return super.getElementAt(index-1);
			}
		};
		SelectionInList<Object> list = new SelectionInList<Object>(fieldnames, presentationModel.getModel("fieldname"));
		list.addPropertyChangeListener("value",new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				try {
				properties.process((Property) evt.getNewValue());
				} catch (Exception x) {
					
				}
			}
		});
		JComboBox box = BasicComponentFactory.createComboBox(list);
		AutoCompleteDecorator.decorate(box);
		return box;

	}
	@Override
	protected JComponent createValueComponent() {
		RowsModel rows = new RowsModel<String>(stats);

		ComboBoxAdapter<String> adapter = new ComboBoxAdapter<String>(rows,
					presentationModel.getModel("value"));
		JComboBox box = new JComboBox(adapter);

		box.setEditable(true);
		box.getEditor().getEditorComponent().addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			public void keyPressed(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
				if (KeyEvent.VK_ENTER == e.getKeyChar()) try {
					queryStringStartsWith.setValue(presentationModel.getValue("value").toString());
					stats.setQuery(queryStringStartsWith);
				} catch (Exception x) {
					
				}
			}
		});

		return box;
	}	
	@Override
	protected JComponent createConditionComponent() {
		JComboBox box = BasicComponentFactory.createComboBox(
                new SelectionInList<StringCondition>(
                		new StringCondition[] {
                			StringCondition.getInstance(StringCondition.C_EQ),
                			StringCondition.getInstance(StringCondition.C_LIKE),
                			StringCondition.getInstance(StringCondition.C_NOTLIKE),
                			StringCondition.getInstance(StringCondition.C_REGEXP),
                			StringCondition.getInstance(StringCondition.C_SOUNDSLIKE),
                			StringCondition.getInstance(StringCondition.C_STARTS_WITH)
                		},
                		presentationModel.getModel("condition")));
		AutoCompleteDecorator.decorate(box);
		return box;
	}

	public QueryField process(IQueryRetrieval<Property> target)
			throws AmbitException {
		return getObject();
	}
	public void open() throws DbAmbitException {
		try {
			properties.setQuery(new RetrieveFieldNamesByType(true));
		} catch (Exception x) {
			throw new DbAmbitException(this,x);
		}
		
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		properties.setConnection(connection);
		stats.setConnection(connection);
	}
}
