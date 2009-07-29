package ambit2.rest.property;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

/**
 * Reports property value for acompound/conformer
 * @author nina
 *
 * @param <Q>
 */
public class PropertyReporter<Q extends IQueryRetrieval<Object>> extends QueryReporter<Object,Q, Writer> {
	protected List results = new ArrayList();
	@Override
	public void footer(Writer output, Q query) {
		try {
			for (Object o : results) {
				output.write(o.toString());
				output.write(' ');
			}
		output.flush();
		} catch (Exception x) {}
		
	}

	@Override
	public void header(Writer output, Q query) {
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
