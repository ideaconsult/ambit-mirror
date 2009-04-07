package ambit2.plugin.pbt;

import ambit2.db.search.structure.QueryField;
import ambit2.workflow.ExecuteAndStoreQuery;
import ambit2.workflow.QueryInteraction;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class SearchWorkflow extends Workflow {
	public SearchWorkflow() {
		
    	ExecuteAndStoreQuery p1 = new ExecuteAndStoreQuery();
        p1.setName("Search");    
        Sequence seq=new Sequence();
        seq.setName("Substance search");
        seq.addStep(new QueryInteraction(new QueryField()));
		seq.addStep(p1);
				
        setDefinition(seq);        	
        


		setDefinition(new LoginSequence(seq));
	
		}
	@Override
	public String toString() {
		return "Search";
	}
}
