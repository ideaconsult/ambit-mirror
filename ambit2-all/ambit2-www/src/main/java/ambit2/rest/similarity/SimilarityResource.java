package ambit2.rest.similarity;

import java.util.BitSet;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesParser;
import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QuerySimilarityBitset;
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
		
		
		
		try {
	        String smiles = Reference.decode(request.getAttributes().get("smiles").toString());
			q.setBitset(getBitset(getMolecule(smiles)));			
			return q;
		} catch (InvalidSmilesException x) {
			throw new AmbitException(x);
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		
	}
	public IMolecule getMolecule(String smiles) throws InvalidSmilesException {
		SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		String test = smiles;
		//This is a workaround for a bug in CDK smiles parser
		for (int i=1; i < 9; i++) {
			int pos=0;
			int count = 0;			
			while ((pos =test.indexOf(Integer.toString(i)))>0) {
				test = test.substring(pos+1);
				count++;
			}
			if ((count % 2)==1) throw new InvalidSmilesException(smiles);	
		}
			
		return parser.parseSmiles(smiles);
	}
	public BitSet getBitset(IMolecule molecule) throws AmbitException {
		FingerprintGenerator gen = new FingerprintGenerator();
		return gen.process(molecule);
	}

		
}
