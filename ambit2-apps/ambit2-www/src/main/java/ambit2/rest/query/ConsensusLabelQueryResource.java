package ambit2.rest.query;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ConsensusLabel;
import ambit2.base.data.ConsensusLabel.CONSENSUS_LABELS;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryStructureByQuality;
import ambit2.db.search.structure.QueryStructureByQualityPairLabel;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetResource;

/**
 * Retrieves Dataset ofcompounds, given a quality label. 
 * Resource wrapper for {@link QueryStructureByQuality}
 * @author nina
 *
 */
public class ConsensusLabelQueryResource   extends DatasetResource<IQueryRetrieval<IStructureRecord>> {
	public static String resource = "/consensuslabel";
	protected Integer datasetID;
	protected Integer queryResultsID;
	@Override
	public String getCompoundInDatasetPrefix() {
		if (dataset_prefixed_compound_uri)
		return
			datasetID!=null?String.format("%s/%d", OpenTox.URI.dataset.getURI(),datasetID):
				queryResultsID!=null?String.format("%s/R%d", OpenTox.URI.dataset.getURI(),queryResultsID):"";
		else return "";
	}	
	


	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		//collapsed = true;
		setGroupProperties(context, request, response);
		setTemplate(createTemplate(context, request, response));
		QueryStructureByQualityPairLabel q = new QueryStructureByQualityPairLabel();
		Form form = request.getResourceRef().getQueryAsForm();
		Object key = form.getFirstValue(QueryResource.search_param);
		ConsensusLabel label = new ConsensusLabel();
		if (key != null) {
	        try {
	        	label.setLabel(CONSENSUS_LABELS.valueOf(Reference.decode(key.toString())));
	        } catch (Exception x) {
	        	StringBuilder b = new StringBuilder();
	        	b.append("Valid values are ");
	        	for(CONSENSUS_LABELS v : CONSENSUS_LABELS.values()) { b.append(v); b.append('\t');}
				throw new ResourceException(
						Status.CLIENT_ERROR_BAD_REQUEST,
						b.toString(),
						x
						);
	        }			
		} else {
			label.setLabel(CONSENSUS_LABELS.Consensus);
		}
		q.setValue(label);
		
		key = form.getFirstValue("text");
		if (key!=null) label.setText(key.toString());
		
		Object id = request.getAttributes().get(OpenTox.URI.dataset.getKey());
		
		
		if (id != null) try {

			datasetID = Integer.parseInt(id.toString());
			SourceDataset dataset = new SourceDataset();
			dataset.setId(datasetID);
			q.setFieldname(dataset);
			return q;
		} catch (Exception x) {
			if (id.toString().startsWith("R")) {
				queryResultsID = Integer.parseInt(id.toString().substring(1));
				StoredQuery dataset = new StoredQuery();
				dataset.setId(queryResultsID);
				q.setFieldname(dataset);
				
			} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid dataset id");
		} 
		return q;
	//	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No dataset !");
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	@Override
	protected Representation put(Representation representation, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
 
}
