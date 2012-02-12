package org.opentox.fastox.ambit.workflow;

import org.opentox.fastox.ambit.workflow.RDFWorkflowContext.WFC_KEY;
import org.restlet.data.Reference;

import com.hp.hpl.jena.rdf.model.Model;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

/**
 * Generic RDF reader
 * @author nina
 *
 */
public class RDFReaderWorkflow extends Workflow {
	protected WFC_KEY key;
	public RDFReaderWorkflow(WFC_KEY key) {
		super();
		this.key = key;
		Primitive<Reference,Model> p = new Primitive<Reference,Model>(
				key.toString(),
				RDFWorkflowContext.WFC_KEY.ONTMODEL.toString(),
				new RDFReaderPerformer());
		
		p.setName(key.getName());
		Sequence seq = new Sequence();
		seq.addStep(p);
		setDefinition(seq);
	}
	@Override
	public String toString() {
		return key.getName();
	}
	
}
