package ambit2.plugin.pbt;

import ambit2.db.search.property.TemplateQuery;
import ambit2.db.search.structure.QueryField;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;
import ambit2.workflow.library.LoginSequence;
import ambit2.workflow.library.QueryExecution;
import ambit2.workflow.library.QuerySelection;
import ambit2.workflow.library.SequenceAppendQueryResults;

import com.microworkflow.process.Conditional;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.Workflow;

public class SearchWorkflow extends Workflow {
	public SearchWorkflow() {

        Sequence seq = new Sequence();
		TestCondition modified = new TestCondition() {
			@Override
			public boolean evaluate() {
				Object o = getContext().get(PBTWorkBook.PBT_WORKBOOK);
				if (o==null) return false;
				if (o instanceof PBTWorkBook) {
					return ((PBTWorkBook)o).isModified();
				}
				return false;
			}
		};
		
        seq.addStep(new QuerySelection());
        seq.addStep(new QueryExecution(new QueryField(),DBWorkflowContext.QUERY_POPUP,null));
        UserInteraction browse = new UserInteraction(
        		true,DBWorkflowContext.USERINTERACTION,DBWorkflowContext.QUERY_POPUP,"Browse results");
        seq.addStep(browse);        
        
		TemplateQuery templateQuery = new TemplateQuery();
		templateQuery.setValue(PBTWorkBook.PBT_TITLE);
        seq.addStep(new SequenceAppendQueryResults(DBWorkflowContext.QUERY_POPUP,false,templateQuery));
        
		Conditional c1 = new Conditional(modified,null,new LoginSequence(seq));
		c1.setName("PBT modified?");
		
		setDefinition(c1);
	
		}
	@Override
	public String toString() {
		return "Search";
	}
}
