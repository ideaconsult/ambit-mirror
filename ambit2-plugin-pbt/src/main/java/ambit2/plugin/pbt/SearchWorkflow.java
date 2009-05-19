package ambit2.plugin.pbt;

import ambit2.db.search.structure.QueryField;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;
import ambit2.workflow.library.LoginSequence;
import ambit2.workflow.library.QueryExecution;
import ambit2.workflow.library.QuerySelection;
import ambit2.workflow.library.SequenceAppendQueryResults;

import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class SearchWorkflow extends Workflow {
	public SearchWorkflow() {

        Sequence seq = new Sequence();
        seq.addStep(new QuerySelection());
        seq.addStep(new QueryExecution(new QueryField(),DBWorkflowContext.QUERY_POPUP,null));
        UserInteraction browse = new UserInteraction(
        		true,DBWorkflowContext.USERINTERACTION,DBWorkflowContext.QUERY_POPUP,"Browse results");
        seq.addStep(browse);        
        seq.addStep(new SequenceAppendQueryResults(DBWorkflowContext.QUERY_POPUP,false));
        
		setDefinition(new LoginSequence(seq));
	
		}
	@Override
	public String toString() {
		return "Search";
	}
}
