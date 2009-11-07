package ambit2.rest.similarity;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.BitSet;

import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IMolecule;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.QuerySimilarityBitset;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.query.StructureQueryResource;

/**
 *  Retrieve similar compounds, given a smiles
 * /similarity?search="smiles"&threshold=0.9
 * @author nina
 *
 */
public class SimilarityResource extends StructureQueryResource<QuerySimilarityBitset> {
	protected enum QueryType  {smiles,url};
	public final static String resource =  "/similarity";
	public final static String smiles =  "smiles";
	public final static String resourceID =  String.format("/{%s}",smiles);
	protected double threshold = 0.9;
	protected String dataset_id;
	protected String query_smiles;
	protected QueryType query_type;
	protected IMolecule mol ;
	/*
	public final static String similarity = String.format("%s%s",QueryResource.query_resource ,"/similarity/method");		
	public final static String fp_dataset = String.format("%s%s",similarity,"/fp1024/distance/tanimoto/{threshold}");
	public final static String tanimoto = SimilarityResource.similarity + "/fp1024/distance/tanimoto";	
	public final static String fp =  tanimoto + "/{threshold}";
	
	
*/
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();

		        
	};

	@Override
	protected QuerySimilarityBitset createQuery(Context context,
			Request request, Response response) throws ResourceException {
		setTemplate(createTemplate(context, request, response));
		Form form = getRequest().getResourceRef().getQueryAsForm();

		try {
			query_type = QueryType.valueOf(form.getFirstValue("type"));

		} catch (Exception x) {
			query_type = QueryType.smiles;
		}		
		switch (query_type) {
		case smiles:
			try {
				query_smiles = Reference.decode(form.getFirstValue("search"));
				if (query_smiles==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty smiles");
				mol = MoleculeTools.getMolecule(query_smiles);			
				
			} catch (InvalidSmilesException x) {
				throw new ResourceException(
						Status.CLIENT_ERROR_BAD_REQUEST,
						String.format("Invalid smiles %s",getRequest().getAttributes().get("smiles")),
						x
						);

			} catch (Exception x) {
				throw new ResourceException(
						Status.SERVER_ERROR_INTERNAL,
						x.getMessage(),
						x
						);
			}			
			break;
		case url:
			Reader reader = null;
			try {
				query_smiles = Reference.decode(form.getFirstValue("search"));
				if (query_smiles==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty query");
				
				Request rq = new Request();
				Client client = new Client(Protocol.HTTP);
				rq.setResourceRef(query_smiles);
				rq.setMethod(Method.GET);
				rq.getClientInfo().getAcceptedMediaTypes().clear();
				rq.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(ChemicalMediaType.CHEMICAL_MDLSDF));
				Response resp = client.handle(rq);
				if (resp.getStatus().equals(Status.SUCCESS_OK)) {
					reader = new InputStreamReader(resp.getEntity().getStream());
					/*
					String line = null;
					int count=0;
					reader = new BufferedReader(new InputStreamReader(resp.getEntity().getStream()));
					while ((line = ((BufferedReader)reader).readLine())!=null) {
						System.out.println(line);
						count++;
					}					
					*/
					mol = MoleculeTools.readMolfile(reader);
				}				
			} catch (InvalidSmilesException x) {
				throw new ResourceException(
						Status.CLIENT_ERROR_BAD_REQUEST,
						String.format("Invalid smiles %s",getRequest().getAttributes().get("smiles")),
						x
						);

			} catch (Exception x) {
				throw new ResourceException(
						Status.SERVER_ERROR_INTERNAL,x.getMessage(),x
						);
			} finally {
				try {reader.close();} catch (Exception x){};
			}
			break;
		default:
			break;
		}

		
		try {
			this.dataset_id = Reference.decode(getRequest().getAttributes().get("dataset_id").toString());
		} catch (Exception x) {
			this.dataset_id = null;
		}		
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
		try {
			q.setValue(getBitset(mol));
			return q;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
		

	}

	public BitSet getBitset(IMolecule molecule) throws AmbitException {
		FingerprintGenerator gen = new FingerprintGenerator();
		return gen.process(molecule);
	}

		
}
