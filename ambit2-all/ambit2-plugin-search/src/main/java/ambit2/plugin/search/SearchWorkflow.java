package ambit2.plugin.search;


import ambit2.db.search.structure.QueryField;
import ambit2.workflow.library.LoginSequence;
import ambit2.workflow.library.QueryExecution;
import ambit2.workflow.library.QuerySelection;

import com.microworkflow.process.Workflow;

public class SearchWorkflow extends Workflow {
	
	public SearchWorkflow() {
		super();
		setDefinition(new LoginSequence(new QuerySelection().addStep(new QueryExecution(new QueryField()))));
	}
	@Override
	public String toString() {
		return "Simple search";
	}
}
