package ambit2.plugin.search;


import ambit2.db.search.QueryField;
import ambit2.workflow.ExecuteAndStoreQuery;
import ambit2.workflow.QueryInteraction;
import ambit2.workflow.library.DefineProfile;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class SearchWorkflow extends Workflow {
	public SearchWorkflow() {
		super();
		
    	ExecuteAndStoreQuery p1 = new ExecuteAndStoreQuery();
        p1.setName("Search");    
        Sequence seq=new Sequence();
        seq.setName("Basic search");
        seq.addStep(new QueryInteraction(new QueryField()));
		seq.addStep(p1);
		setDefinition(new LoginSequence(seq));
				
	}
	@Override
	public String toString() {
		return "Simple search";
	}
}
