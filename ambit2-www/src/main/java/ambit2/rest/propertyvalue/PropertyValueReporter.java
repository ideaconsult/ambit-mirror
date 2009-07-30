package ambit2.rest.propertyvalue;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.RetrieveField;
import ambit2.db.reporters.QueryReporter;

/**
 * Reports property value for acompound/conformer
 * @author nina
 *
 * @param <Q>
 */
public class PropertyValueReporter extends QueryReporter<Object,RetrieveField, Writer> {
	protected List results = new ArrayList();
	@Override
	public void footer(Writer output, RetrieveField query) {
		try {
			for (Object o : results) {
				output.write(o.toString());
				output.write(' ');
			}
		output.flush();
		} catch (Exception x) {}
		
	}

	@Override
	public void header(Writer output, RetrieveField query) {
		results.clear();
		
	}

	@Override
	public void processItem(Object item, Writer output) {
		try {

			if (results.indexOf(item)==-1) results.add(item);
			
		} catch (Exception x) {
			
		}
		
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
