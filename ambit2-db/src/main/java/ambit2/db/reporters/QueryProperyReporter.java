package ambit2.db.reporters;

import ambit2.base.data.Property;
import ambit2.db.DbReader;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.readers.IQueryRetrieval;

public abstract class QueryProperyReporter<Q extends IQueryRetrieval<Property>,Output> extends QueryReporter<Property, Q, Output> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2662159144067243671L;

	@Override
	public void footer(Output output, Q query) {
		
	}

	@Override
	public void header(Output output, Q query) {
	}

	public void open() throws DbAmbitException {
	}

	protected AbstractBatchProcessor<IQueryRetrieval<Property>, Property> createBatch(Q query) {
		DbReader<Property> reader = new DbReader<Property>();
		reader.setHandlePrescreen(false);
		return reader;
	}
}
