package ambit2.workflow;

import ambit2.db.search.IQueryObject;



public class QueryInteraction extends UserInteraction<IQueryObject> {
	public QueryInteraction(IQueryObject query) {
		super(query,DBWorkflowContext.QUERY,"Define query");
	}
}
