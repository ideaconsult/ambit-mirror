package ambit2.rest.similarity;

import java.awt.Dimension;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRelation;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.reporters.QueryAbstractReporter;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.property.QueryPairwiseTanimoto;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.DisplayMode;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.query.QueryResource;

/**
 * Returns similarity matrix of a dataset
 * @author nina
 *
 * @param <Q>
 */
public class SimilarityMatrixResource<Q extends IQueryRetrieval<IStructureRelation<Double>>> extends QueryResource<Q,IStructureRelation<Double>> { 
	public final static String resource =  "/matrix";
	protected Integer datasetID;
	protected Integer queryResultsID;
	
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		if ((queryObject == null) && !(variant.getMediaType().equals(MediaType.TEXT_HTML))) 
			throw new NotFoundException();
		
		//setTemplate(template);
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) {
			variant.setMediaType(new MediaType(media));
		}
		String filenamePrefix = getRequest().getResourceRef().getPath();
		
		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			Dimension d = new Dimension(150,150);
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			try {
				d.width = Integer.parseInt(form.getFirstValue("w").toString());
			} catch (Exception x) {}
			try {
				d.height = Integer.parseInt(form.getFirstValue("h").toString());
			} catch (Exception x) {}			
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					createHTMLReporter(d),MediaType.TEXT_HTML);
	
		} 
	 /*
		else if (variant.getMediaType().equals(ChemicalMediaType.WEKA_ARFF)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new ARFFResourceReporter(getTemplate(),getGroupProperties(),getRequest(),getDocumentation(),
								String.format("%s%s",getRequest().getRootRef(),getCompoundInDatasetPrefix())
							),
					ChemicalMediaType.WEKA_ARFF,filenamePrefix);			
		} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					createCSVReporter()
					,MediaType.TEXT_CSV,filenamePrefix);
		} 
		//else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
		//otherwise return json
		 * */
			return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
					new PairwiseSimilarityJSONReporter(
							//getTemplate(),getGroupProperties(),getRequest(),
							getRequest()),
					MediaType.APPLICATION_JSON,filenamePrefix);	
	}
	
	protected QueryAbstractReporter createHTMLReporter(Dimension d) {
		return new PairwiseSimilarityHTMLReporter(getRequest(),DisplayMode.table,null);
	}

	public String getCompoundInDatasetPrefix() {
		if (dataset_prefixed_compound_uri)
		return
			datasetID!=null?String.format("%s/%d", OpenTox.URI.dataset.getURI(),datasetID):
			queryResultsID!=null?String.format("%s/R%d", OpenTox.URI.dataset.getURI(),queryResultsID):"";
		else return "";
	}
	
	protected CSVReporter createCSVReporter() {
		CSVReporter csvReporter = new CSVReporter(null,null,
				String.format("%s%s",getRequest().getRootRef(),getCompoundInDatasetPrefix())
				);
		csvReporter.setSimilarityColumn(Property.getInstance("metric",queryObject==null?"":queryObject.toString(),"http://ambit.sourceforge.net"));
		return csvReporter;
	}
	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Object id = request.getAttributes().get(DatasetStructuresResource.datasetKey);
		if (id != null)  try {
			
			id = Reference.decode(id.toString());
			return getQueryById(new Integer(id.toString()));

		} catch (NumberFormatException x) {
			return getQueryById(id.toString());
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
		}
		return null;
	}
	
	protected Q getQueryById(Integer key) throws ResourceException {
		Q query = null;
		datasetID = key;
		QueryPairwiseTanimoto q = new QueryPairwiseTanimoto();
		SourceDataset dataset = new SourceDataset();
		dataset.setID(key);
		q.setFieldname(dataset);
		q.setValue(dataset);
		Form form = getResourceRef(getRequest()).getQueryAsForm();
		setPaging(form, q);
		return (Q)q;
	}
	

	protected Q getQueryById(String key) throws ResourceException {
		if (key.startsWith(DatasetStructuresResource.QR_PREFIX)) {
			key = key.substring(DatasetStructuresResource.QR_PREFIX.length());
			try {
				queryResultsID = Integer.parseInt(key.toString());
			} catch (NumberFormatException x) {
				throw new InvalidResourceIDException(key);
			}
			QueryPairwiseTanimoto q = new QueryPairwiseTanimoto();
			StoredQuery dataset = new StoredQuery();
			dataset.setID(queryResultsID);
			q.setFieldname(dataset);
			q.setValue(dataset);
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			setPaging(form, q);
			return (Q)q;
		} //else return getDatasetByName(key);
		throw new InvalidResourceIDException(key);
	}
}
