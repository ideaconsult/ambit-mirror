package org.opentox.fastox.ambit.workflow;

import com.hp.hpl.jena.ontology.OntModel;
import com.microworkflow.events.WorkflowContextEvent;
import com.microworkflow.process.WorkflowContext;

/**
 * Encapsulates {@link OntModel} as a workflow context {@link WorkflowContext}.
 * @author nina
 *
 */
public class RDFWorkflowContext extends WorkflowContext {
	public enum WFC_KEY {
		ONTMODEL,
		DATASET_REFERENCE {
			@Override
			public String getName() {
				return "Structures";
			}
		},
		MODEL_REFERENCE {
			@Override
			public String getName() {
				return "Models";
			}
		};
		public String getName() {
			return toString();
		}
	}
	
    public OntModel getRDFModel() {
        Object model = get(WFC_KEY.ONTMODEL.toString());
        if (model instanceof OntModel) return(OntModel) model;
        else return null;
    }

    public Object put(String key, Object value) {
    	if  (WFC_KEY.ONTMODEL.toString().equals(key)) {
    		if (value != null) {
	    		OntModel model = getRDFModel();
	        	if (model == null) super.put(WFC_KEY.ONTMODEL.toString(),value);
	        	else {
	        		model.add((OntModel)value);
	        		firePropertyChange(new WorkflowContextEvent(this,key, null, model));
	        	}
	        	
    		}
    		return getRDFModel();
    	}
    	else return super.put(key, value);
    }    
}
