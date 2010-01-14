package org.opentox.fastox.ambit.workflow;

import org.opentox.fastox.ambit.workflow.RDFWorkflowContext.WFC_KEY;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * Reads a dataset and adds it to the {@link OntModel}
 * @author nina
 *
 */
public class StructureReaderWorkflow extends RDFReaderWorkflow {

	public StructureReaderWorkflow() {
		super(WFC_KEY.DATASET_REFERENCE);
	}

}
