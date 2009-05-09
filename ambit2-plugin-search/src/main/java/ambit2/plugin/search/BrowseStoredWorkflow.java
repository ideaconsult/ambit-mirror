package ambit2.plugin.search;

import ambit2.db.search.IQueryObject;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.db.search.structure.SCOPE;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.ExecuteAndStoreQuery;
import ambit2.workflow.QueryInteraction;
import ambit2.workflow.library.LoginSequence;
import ambit2.workflow.library.ScopeSelection;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class BrowseStoredWorkflow extends Workflow {
	public BrowseStoredWorkflow() {
		
		Sequence seq = new Sequence();
        seq.addStep(
		new Primitive<IQueryObject,IQueryObject>(DBWorkflowContext.QUERY,DBWorkflowContext.QUERY,
				new Performer<IQueryObject, IQueryObject>() {
			@Override
			public IQueryObject execute() throws Exception {
				return new QueryStoredResults();
			}
		})
		);
        
        seq.addStep(new QueryInteraction(new QueryStoredResults()));
    	ExecuteAndStoreQuery p1 = new ExecuteAndStoreQuery(null);
        p1.setName("Search");           
		seq.addStep(p1);		
		setDefinition(new LoginSequence(seq));
	}
}
