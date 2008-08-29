package ambit2.workflow;

import ambit2.db.processors.ProcessorCreateQuery;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.IStoredQuery;


public class ExecuteAndStoreQuery extends ActivityPrimitive<IQueryObject,IStoredQuery> {
	public ExecuteAndStoreQuery() {
		super(DBWorkflowContext.QUERY,
			  DBWorkflowContext.STOREDQUERY,
			  new ProcessorCreateQuery());
		setName("Query");
	}
}
