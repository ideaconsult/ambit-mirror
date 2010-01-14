package org.opentox.fastox.ambit.workflow;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.rest.rdf.OT;

import com.hp.hpl.jena.ontology.OntModel;
import com.microworkflow.execution.Performer;

/**
 * ReadsRDF from a {@link Reference}
 * @author nina
 *
 */
public class RDFReaderPerformer extends Performer<Reference, OntModel> {

	@Override
	public OntModel execute() throws Exception {
		return	OT.createModel(getTarget(),MediaType.APPLICATION_RDF_XML);
	}

}
