package ambit2.rest.model;

import ambit2.db.model.ModelQueryResults;
import ambit2.rest.OT;
import ambit2.rest.RDFBatchParser;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Reads Models from RDF
 * @author nina
 *
 */
public class RDFModelParser extends RDFBatchParser<ModelQueryResults> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4169047876241761152L;

	public RDFModelParser(String baseReference) {
		super(baseReference, OT.OTClass.Model);
	}

	@Override
	protected ModelQueryResults createRecord() {
		return new ModelQueryResults();
	}

	@Override
	protected void parseRecord(Resource newEntry, ModelQueryResults record) {
		// TODO Auto-generated method stub
		
	}

}
