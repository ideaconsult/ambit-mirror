package org.opentox.fastox.ambit.workflow;

import org.opentox.rdf.OT;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import com.hp.hpl.jena.rdf.model.Model;
import com.microworkflow.execution.Performer;

/**
 * ReadsRDF from a {@link Reference}
 * @author nina
 *
 */
public class RDFReaderPerformer extends Performer<Reference, Model> {

	@Override
	public Model execute() throws Exception {
		return	OT.createModel(null,getTarget(),MediaType.APPLICATION_RDF_XML);
	}

}
