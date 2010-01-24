package ambit2.rest.similarity;

import java.util.BitSet;

import org.openscience.cdk.interfaces.IMolecule;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QuerySimilarityBitset;
import ambit2.db.update.structure.ChemicalByDataset;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.StructureQueryResource;

/**
 *  Retrieve similar compounds, given a smiles
 * /similarity?search="smiles"&threshold=0.9
 * @author nina
 *
 */
public class SimilarityResource<Q extends IQueryRetrieval<IStructureRecord>> extends StructureQueryResource<Q> {
	
	public final static String resource =  "/similarity";
	public final static String smiles =  "smiles";
	public final static String resourceID =  String.format("/{%s}",smiles);
	protected double threshold = 0.9;
	protected String dataset_id;

	protected IMolecule mol ;

	protected String getDefaultTemplateURI(Context context, Request request,Response response) {
		return (dataset_id == null)?null:
			String.format("%s%s/%s%s",
					getRequest().getRootRef(),OpenTox.URI.dataset.getURI(),dataset_id,PropertyResource.featuredef);				
			
//			String.format("riap://application/dataset/%s%s",dataset_id,PropertyResource.featuredef);
	}

	@Override
	protected Q createQuery(Context context,
			Request request, Response response) throws ResourceException {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		mol = getMolecule(form);
		
		threshold = 0.0;
        try {
        	threshold = new Double(Reference.decode(form.getFirstValue("threshold")));
        } catch (Exception x) {
        	threshold = 0.9;
        }			
        
		QuerySimilarityBitset q = new QuerySimilarityBitset();
		q.setChemicalsOnly(true);
		q.setThreshold(threshold);
		q.setCondition(NumberCondition.getInstance(">"));		
		q.setName("Similarity");
		try {
			q.setValue(getBitset(mol));
			
			QueryCombinedStructure qc= null;
			try {
				qc = new QueryCombinedStructure();
				qc.setChemicalsOnly(true);
				this.dataset_id = Reference.decode(getRequest().getAttributes().get(DatasetResource.datasetKey).toString());
				ChemicalByDataset  cd = new ChemicalByDataset(new Integer(dataset_id));
				qc.setScope(cd);
				setTemplate(createTemplate(context, request, response));
				return (Q)qc;
			} catch (Exception x) {
				setTemplate(createTemplate(context, request, response));
				return (Q)q;
			}	
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}

	}

	public BitSet getBitset(IMolecule molecule) throws AmbitException {
		FingerprintGenerator gen = new FingerprintGenerator();
		return gen.process(molecule);
	}

		
}
