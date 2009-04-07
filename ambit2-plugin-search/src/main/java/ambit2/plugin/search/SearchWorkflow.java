package ambit2.plugin.search;


import nplugins.core.Introspection;
import ambit2.base.data.ClassHolder;
import ambit2.base.data.SelectionBean;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.QueryField;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.ExecuteAndStoreQuery;
import ambit2.workflow.QueryInteraction;
import ambit2.workflow.UserInteraction;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class SearchWorkflow extends Workflow {
	public SearchWorkflow() {
		super();

		SelectionBean<ClassHolder> selection = new SelectionBean<ClassHolder>(
				new ClassHolder[] {
						new ClassHolder("ambit2.db.search.structure.QueryField","Text properties (CAS, Name, etc.)","",""),
						new ClassHolder("ambit2.db.search.structure.QueryFieldNumeric","Numeric properties (Descriptors, measured data, IDs)","",""),
						new ClassHolder("ambit2.db.search.structure.QueryDataset","Dataset","Retrieve all the compounds from a dataset",""),
						new ClassHolder("ambit2.db.search.structure.QueryStructure","Structure","Search by SMILES, Inchi, Formula",""),
						new ClassHolder("ambit2.db.search.structure.QuerySimilarityStructure","Similarity","Search for similar structures",""),						
				},"Search by"
				);

        UserInteraction<SelectionBean<ClassHolder>> selectQuery = new UserInteraction<SelectionBean<ClassHolder>>(
        		selection,
        		"SELECTION","??????");
        selectQuery.setName("Select type of query");
       
        Performer<SelectionBean<ClassHolder>,AbstractStructureQuery> performer = new Performer<SelectionBean<ClassHolder>,AbstractStructureQuery>() {
    		public AbstractStructureQuery execute() throws Exception {
    			
    			ClassHolder ch = getTarget().getSelected();
    			Object o = Introspection.loadCreateObject(ch.getClazz());
    			if (o instanceof AbstractStructureQuery)
    				return (AbstractStructureQuery)o;
    			else throw new Exception(o.getClass().getName() + " not expected");
    		}        	
        };
    	Primitive<SelectionBean<ClassHolder>,AbstractStructureQuery> p = 
    		new Primitive<SelectionBean<ClassHolder>,AbstractStructureQuery>( 
    			"SELECTION",
    			DBWorkflowContext.QUERY,performer);
    	
    	ExecuteAndStoreQuery p1 = new ExecuteAndStoreQuery();
        p1.setName("Search");    
        Sequence seq=new Sequence();
        seq.setName("Basic search");
        seq.addStep(selectQuery);
        seq.addStep(p);
        seq.addStep(new QueryInteraction(new QueryField()));
		seq.addStep(p1);
		setDefinition(new LoginSequence(seq));
				
	}
	@Override
	public String toString() {
		return "Simple search";
	}
}
