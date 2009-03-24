package ambit2.rest.similarity;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QuerySimilarityBitset;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.query.StructureQueryResource;

public class SimilarityResource extends StructureQueryResource<QuerySimilarityBitset> {
	protected String dataset_id;
	
	public SimilarityResource(Context context, Request request, Response response) {
		super(context,request,response);
		
		try {
			this.dataset_id = Reference.decode(request.getAttributes().get("dataset_id").toString());
		} catch (Exception x) {
			this.dataset_id = null;
		}


	}
	@Override
	protected QuerySimilarityBitset createQuery(Context context,
			Request request, Response response) throws AmbitException {
		Double threshold = 0.0;
        try {
        	threshold = new Double(Reference.decode(request.getAttributes().get("threshold").toString()));
        } catch (Exception x) {
        	threshold = 0.5;
        }		
        
		QuerySimilarityBitset q = new QuerySimilarityBitset();
		q.setThreshold(threshold);
		q.setCondition(NumberCondition.getInstance(">"));		
		
		FingerprintGenerator gen = new FingerprintGenerator();
		SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		
		try {
	        String smiles = Reference.decode(request.getAttributes().get("smiles").toString());
			q.setBitset(gen.process(parser.parseSmiles(smiles)));			
			return q;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		
	}

		
}
