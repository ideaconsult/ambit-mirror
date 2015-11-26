package ambit2.db.search.property;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;

/**
 * Given {@link IStructureRecord} , find if its properties are already in the properties table.
 * @author nina
 *
 */
public class RetieveFeatures extends AbstractPropertyRetrieval<String, IStructureRecord, StringCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6723083165416521349L;
	static String name_title_search = "\n(name = ? and title = ?) ";
	
	@Override
	public String getSQL() throws AmbitException {
		Iterable<Property> properties = getValue().getRecordProperties();
		StringBuilder b = new StringBuilder();
		int count = 0;
		for (Property property : properties) 
			if (property.isEnabled()) {
				b.append(count==0?"":"or");
				b.append(name_title_search);
				count++;
			}
		if (count==0) throw new AmbitException("No parameters!");
		return String.format("%s where %s",base_sql,b.toString());
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		Iterable<Property> properties = getValue().getRecordProperties();
		for (Property property : properties) 
			if (property.isEnabled()) {
				params.add(new QueryParam<String>(String.class, property.getName()));				
				params.add(new QueryParam<String>(String.class, property.getReference().getTitle()));	
			}
		return params;
	}


}
