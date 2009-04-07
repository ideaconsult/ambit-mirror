package ambit2.dbui;

import java.sql.Connection;

import javax.swing.JComponent;
import javax.swing.ListModel;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.results.AmbitRows;
import ambit2.db.results.RowsModel;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.RetrieveFieldNamesByType;
import ambit2.db.search.structure.QueryField;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;

public class QueryFieldEditor extends QueryEditor<Property,String, StringCondition,IStructureRecord,QueryField>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4483885648170842874L;
	protected AmbitRows<Property> properties;

	@Override
	public JComponent buildPanel() {
		properties = new AmbitRows<Property>();
		return super.buildPanel();
	}
	@Override
	protected JComponent createFieldnameComponent() {
		
		ListModel fieldnames = new RowsModel<Property>(properties);		
		return BasicComponentFactory.createComboBox(
				new SelectionInList<Object>(fieldnames, presentationModel.getModel("fieldname")));
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
	}
}
