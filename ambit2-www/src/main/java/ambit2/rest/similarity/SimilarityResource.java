package ambit2.rest.similarity;

import java.util.BitSet;

import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IMolecule;
import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.QuerySimilarityBitset;
import ambit2.rest.StatusException;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.StructureQueryResource;

public class SimilarityResource extends StructureQueryResource<QuerySimilarityBitset> {
	public final static String similarity = String.format("%s%s",QueryResource.query_resource ,"/similarity/method");		
	public final static String fp_dataset = String.format("%s%s%s",similarity,"/fp1024/distance/tanimoto/{threshold}",DatasetsResource.datasetID);
	public final static String tanimoto = SimilarityResource.similarity + "/fp1024/distance/tanimoto";	
	public final static String fp =  tanimoto + "/{threshold}";
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
			Request request, Response response) throws StatusException {
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
			q.setValue(getBitset(MoleculeTools.getMolecule(smiles)));			
			return q;
		} catch (InvalidSmilesException x) {
			throw new StatusException(
					new Status(Status.CLIENT_ERROR_BAD_REQUEST,x,String.format("Invalid smiles %s",request.getAttributes().get("smiles")))
					);				

		} catch (Exception x) {
			throw new StatusException(
					new Status(Status.SERVER_ERROR_INTERNAL,x,x.getMessage())
					);
		}
		
	}

	public BitSet getBitset(IMolecule molecule) throws AmbitException {
		FingerprintGenerator gen = new FingerprintGenerator();
		return gen.process(molecule);
	}

		
}
