package org.opentox.fastox.ambit.workflow;

import org.opentox.fastox.ambit.workflow.RDFWorkflowContext.WFC_KEY;

import com.hp.hpl.jena.ontology.OntModel;

/**
 *  Reads models and adds them to the {@link OntModel}
 * @author nina
 *
 */
public class ModelsReaderWorkflow extends RDFReaderWorkflow {
	public ModelsReaderWorkflow() {
		super(WFC_KEY.MODEL_REFERENCE);
	}

}
