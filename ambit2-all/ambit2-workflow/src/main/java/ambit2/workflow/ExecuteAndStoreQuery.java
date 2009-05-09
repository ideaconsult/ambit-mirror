package ambit2.workflow;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.ProcessorCreateQuery;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.IStoredQuery;


public class ExecuteAndStoreQuery extends ActivityPrimitive<IQueryObject<IStructureRecord>,IStoredQuery> {
	public ExecuteAndStoreQuery() {
		this(DBWorkflowContext.STOREDQUERY);
	}
	public ExecuteAndStoreQuery(String resultTag) {
		super(DBWorkflowContext.QUERY,
			  (resultTag==null)?DBWorkflowContext.STOREDQUERY:resultTag,
			  new ProcessorCreateQuery());
		setName("Query");
	}
}
