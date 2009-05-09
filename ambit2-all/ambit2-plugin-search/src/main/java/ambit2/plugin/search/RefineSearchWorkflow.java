package ambit2.plugin.search;

import ambit2.db.search.structure.QueryField;
import ambit2.db.search.structure.SCOPE;
import ambit2.workflow.ExecuteAndStoreQuery;
import ambit2.workflow.QueryInteraction;
import ambit2.workflow.library.LoginSequence;
import ambit2.workflow.library.QuerySelection;
import ambit2.workflow.library.ScopeSelection;

import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class RefineSearchWorkflow extends Workflow {
	public RefineSearchWorkflow() {
		Sequence seq = new Sequence();
        seq.addStep(new QuerySelection());
        seq.addStep(new QueryInteraction(new QueryField()));
        seq.addStep(new ScopeSelection(SCOPE.scope_last_results));
    	ExecuteAndStoreQuery p1 = new ExecuteAndStoreQuery(null);
        p1.setName("Search");           
		seq.addStep(p1);
		

		setDefinition(new LoginSequence(seq));
	}
}
