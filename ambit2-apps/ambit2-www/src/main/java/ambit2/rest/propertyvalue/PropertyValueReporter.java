package ambit2.rest.propertyvalue;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.PropertyValue;

/**
 * Reports property value for acompound/conformer
 * @author nina
 *
 * @param <Q>
 */
public class PropertyValueReporter<T> extends QueryReporter<T,IQueryRetrieval<T>, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5846954016565174817L;
	protected List results = new ArrayList();
	@Override
	public void footer(Writer output, IQueryRetrieval<T> query) {
		try {
			for (Object o : results) {
				output.write(o.toString());
				output.write(' ');
			}
			output.flush();
		} catch (Exception x) {}
		
	}

	@Override
	public void header(Writer output, IQueryRetrieval<T> query) {
		results.clear();
		
	}

	@Override
	public Object processItem(T item) throws AmbitException {
		try {
			if (item instanceof IStructureRecord) {
				IStructureRecord v = (IStructureRecord) item;
				for (Property p : v.getRecordProperties())
					output.write(String.format("%s = %s\n",p,v.getRecordProperty(p)));
			} else if (item instanceof PropertyValue) {
				PropertyValue v = (PropertyValue) item;
				output.write(String.format("%s = %s",v.getProperty().getLabel(),v.getValue()));
			} else	if (results.indexOf(item)==-1) results.add(item);
			
		} catch (Exception x) {
			
		}
		return null;
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
