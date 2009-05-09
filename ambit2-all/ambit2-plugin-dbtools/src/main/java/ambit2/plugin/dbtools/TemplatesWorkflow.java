package ambit2.plugin.dbtools;

import ambit2.db.search.property.QueryOntology;
import ambit2.workflow.UserInteraction;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class TemplatesWorkflow extends Workflow {
	public TemplatesWorkflow() {
        Sequence seq=new Sequence();
        seq.setName("[Organize templates and properties]");    	
        seq.addStep(new UserInteraction<QueryOntology>(
        		new QueryOntology(),"ONTOLOGY","Organize templates and properties"));
        setDefinition(new LoginSequence(seq));
	}
}
