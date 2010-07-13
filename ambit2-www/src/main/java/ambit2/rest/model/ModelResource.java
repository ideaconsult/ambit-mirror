package ambit2.rest.model;

import java.awt.Dimension;
import java.sql.Connection;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryTemplateReporter;
import ambit2.db.search.property.ModelTemplates;
import ambit2.db.update.model.ReadModel;
import ambit2.rest.DBConnection;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.model.predictor.DescriptorPredictor;
import ambit2.rest.model.predictor.FingerprintsPredictor;
import ambit2.rest.model.predictor.ModelPredictor;
import ambit2.rest.model.predictor.NumericADPredictor;
import ambit2.rest.query.ProcessingResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.task.CallableDescriptorCalculator;
import ambit2.rest.task.CallableModelPredictor;
import ambit2.rest.task.CallableQueryProcessor;
import ambit2.rest.task.CallableWekaPredictor;

/**
 * Model as in http://opentox.org/development/wiki/Model
 * Supported REST operation:
 * GET 	 /model<br>
 * GET 	 /model/{id}
 * @author nina
 *
 */
public class ModelResource extends ProcessingResource<IQueryRetrieval<ModelQueryResults>, ModelQueryResults> {

	
	public final static String resource = OpenTox.URI.model.getURI();
	public final static String resourceKey =  OpenTox.URI.model.getKey();
	public final static String resourceID = OpenTox.URI.model.getResourceID();
	protected boolean collapsed = true;
	
	public enum modeltypes  {
		pka,toxtree
	};
	
	protected String category = "";

	public ModelResource() {
		super();
		getVariants().add(new Variant(MediaType.IMAGE_PNG));
	}
	protected Object getModelID(Object id) throws ResourceException {
		
		if (id != null) try {
			id = Reference.decode(id.toString());
			collapsed = false;
			return new Integer(id.toString());
			
		} catch (NumberFormatException x) {
			return id;
		} catch (Exception x) {
			return null;
		} else return null;

	}
	@Override
	protected IQueryRetrieval<ModelQueryResults> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		ReadModel query = getModelQuery(getModelID(getRequest().getAttributes().get(resourceKey)));
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String name = form.getFirstValue(QueryResource.search_param);
		if (name!=null) query.setFieldname(name);
		collapsed = query.getValue()!=null;
		return query;
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		
	if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
		return new OutputWriterConvertor(
				new ModelHTMLReporter(getRequest(),collapsed),MediaType.TEXT_HTML);
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		return new StringConvertor(	new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()) {
			@Override
			public Object processItem(ModelQueryResults dataset) throws AmbitException  {
				super.processItem(dataset);
				try {
				output.write('\n');
				} catch (Exception x) {}
				return null;
			}
		},MediaType.TEXT_URI_LIST);
	} else if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
		return new StringConvertor(	new ModelTextReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()),
				MediaType.TEXT_PLAIN);	
		
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
			variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
			variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
			variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
			variant.getMediaType().equals(MediaType.APPLICATION_JSON)
			) {
		return new RDFJenaConvertor<ModelQueryResults,IQueryRetrieval<ModelQueryResults>>(
				new ModelRDFReporter<IQueryRetrieval<ModelQueryResults>>(getRequest(),variant.getMediaType())
				,variant.getMediaType());	
	} else if (variant.getMediaType().equals(MediaType.IMAGE_PNG) ||
			variant.getMediaType().equals(MediaType.IMAGE_BMP) ||
			variant.getMediaType().equals(MediaType.IMAGE_JPEG) ||
			variant.getMediaType().equals(MediaType.IMAGE_TIFF) ||
			variant.getMediaType().equals(MediaType.IMAGE_GIF) 
			) {
		Dimension d = new Dimension(250,250);
		Form form = getRequest().getResourceRef().getQueryAsForm();
		try {
			
			d.width = Integer.parseInt(form.getFirstValue("w").toString());
		} catch (Exception x) {}
		try {
			d.height = Integer.parseInt(form.getFirstValue("h").toString());
		} catch (Exception x) {}		
		
		return new ImageConvertor<ModelQueryResults, IQueryRetrieval<ModelQueryResults>>(
				new ModelImageReporter(getRequest(), getRequest().getResourceRef().getQueryAsForm(), d),variant.getMediaType());			
	} else//html
		return new OutputWriterConvertor(
				new ModelHTMLReporter(getRequest(),collapsed),MediaType.TEXT_HTML);
	}
	
	@Override
	protected QueryURIReporter<ModelQueryResults, IQueryRetrieval<ModelQueryResults>> getURUReporter(
			Request baseReference) throws ResourceException {
		return new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest());
	}
	
	protected void readVariables(ModelQueryResults model) throws ResourceException {
		
		ModelTemplates query = new ModelTemplates();
		query.setFieldname(model);

		QueryTemplateReporter<ModelTemplates> reporter = null;
		Connection connection = null;
		try {
			DBConnection dbc = new DBConnection(getContext());
			connection = dbc.getConnection(getRequest());
			
			query.setValue("dependent");
			reporter = new QueryTemplateReporter<ModelTemplates>(model.getDependent());
			reporter.setCloseConnection(false);
			reporter.setConnection(connection);
			//dependent
			try {
				reporter.process(query);
			} catch (NotFoundException x) {
				//this is ok
			}
		

			query.setValue("independent");
			reporter.setConnection(connection);
			reporter.setOutput(model.getPredictors());
			try {
				reporter.process(query);
			} catch (NotFoundException x) {
				//ok
			}

			query.setValue("predicted");
			reporter.setConnection(connection);
			reporter.setOutput(model.getPredicted());
			reporter.process(query);
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		try {
			reporter.setCloseConnection(true);
			reporter.close();
		} catch (Exception x) {
			x.printStackTrace();
		}
		try {	connection.close(); 	} catch (Exception x) {}


	}
	

	@Override
	protected CallableQueryProcessor createCallable(Form form,	ModelQueryResults model) throws ResourceException {

		try {
			readVariables(model);
			
			ModelPredictor predictor = ModelPredictor.getPredictor(model,getRequest());
			
			if (model.getContentMediaType().equals(AlgorithmFormat.WEKA.getMediaType())) {
				return //reads Instances, instead of IStructureRecord
				new CallableWekaPredictor<Object>(
						form,
						getRequest().getRootRef(),
						getContext(),
						predictor)
						;
			} else if (model.getContentMediaType().equals(AlgorithmFormat.COVERAGE_SERIALIZED.getMediaType())) {

				if (model.getPredictors().size()== 0) { //hack for structure based AD
					if (predictor instanceof FingerprintsPredictor)
						return 
						new CallableModelPredictor<IStructureRecord,FingerprintsPredictor>(
								form,
								getRequest().getRootRef(),
								getContext(),
								(FingerprintsPredictor)predictor) {
							
						}	;
					else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Not supported %s",predictor.getClass().getName()));
				} else {
					return
						new CallableModelPredictor(
								form,
								getRequest().getRootRef(),
								getContext(),
								predictor) {
							
						}	;
						/*
					return new CallableWekaPredictor<DataCoverage>( //reads Instances, instead of IStructureRecord
							form,
							getRequest().getRootRef(),
							getContext(),
							predictor)
							;
							*/
				}
			} else if (model.getContentMediaType().equals(AlgorithmFormat.JAVA_CLASS.getMediaType())) {
				return
				new CallableDescriptorCalculator(
						form,
						getRequest().getRootRef(),
						getContext(),
						(DescriptorPredictor) predictor
						);
		} else throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE,model.getContentMediaType());
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
		} finally {

		}
	}

	protected ReadModel getModelQuery(Object idmodel) throws ResourceException {
		if (idmodel == null) return new ReadModel();
		else if (idmodel instanceof Integer)
			return new ReadModel((Integer)idmodel);
		else {
			ReadModel query = new ReadModel(null);
			query.setFieldname(idmodel.toString());
			return query;
		}
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {

		return super.post(entity, variant);
	}
	
	
}

