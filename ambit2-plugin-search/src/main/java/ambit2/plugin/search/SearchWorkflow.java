package ambit2.plugin.search;


import ambit2.db.search.structure.QueryField;
import ambit2.workflow.library.LoginSequence;
import ambit2.workflow.library.QueryExecution;

import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class SearchWorkflow extends Workflow {
	
	public SearchWorkflow() {
		super();
		Sequence seq = new Sequence();
		seq.addStep(new QueryExecution(new QueryField()));
		setDefinition(new LoginSequence(seq));
	}
	@Override
	public String toString() {
		return "Search";
	}
}
