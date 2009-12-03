package ambit2.rest.algorithm;

import java.util.List;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.core.data.model.Algorithm;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.AbstractUpdate;
import ambit2.db.update.model.CreateModel;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.query.QueryResource;


/**
 * 
 * @author nina
 *
 * @param <Q>
 */
public class AlgorithmResource<Q> extends QueryResource<IQueryRetrieval<ModelQueryResults>, ModelQueryResults> {
	protected AllAlgorithmsResource catalog;
	public final static String algorithm = "/algorithm";	
	public final static String algorithmKey =  "idalgorithm";
	public final static String resourceID =  String.format("%s/{%s}",algorithm,algorithmKey);
	public enum headers  {
		dataset_id {
			@Override
			public boolean isMandatory() {
				return false;
			}
		},
		algorithm_parameters; 
		public boolean isMandatory() {
			return false;
		}
	};	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		catalog = new AllAlgorithmsResource();
		catalog.init(getContext(),getRequest(),getResponse());
	}
	@Override
	public Representation get(Variant variant) {
		return catalog.get(variant);
	}
	@Override
	public IProcessor<IQueryRetrieval<ModelQueryResults>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		return null;
	}

	@Override
	protected IQueryRetrieval<ModelQueryResults> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		return null;
	}

	@Override
	protected Representation post(Representation entity)
			throws ResourceException {
		if (getRequest().getAttributes().get(algorithmKey)!=null) {
			createNewObject(entity);
			getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
			getResponse().setEntity(null);
			return getResponse().getEntity();
		} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}
	@Override
	protected ModelQueryResults createObjectFromHeaders(Form requestHeaders, Representation entity)
			throws ResourceException {
		
		Object key = getRequest().getAttributes().get(algorithmKey);
		if (key==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Algorithm not defined");
		key = Reference.decode(key.toString());
		Algorithm a = catalog.find(key); //ok, create object
		if (a!= null)
			try {
					List<Property> p = DescriptorsFactory.createDescriptor2Properties(a.getContent().toString());
					if ((p == null)||(p.size()==0)) throw new ResourceException(Status.SERVER_ERROR_INTERNAL,"Can't create "+key);

					String dataset_uri = getParameter(requestHeaders,headers.dataset_id.toString(),headers.dataset_id.isMandatory());
					String params = getParameter(requestHeaders,headers.algorithm_parameters.toString(),headers.algorithm_parameters.isMandatory());  	
					ModelQueryResults mr = new ModelQueryResults();
					mr.setName(a.getName());
					mr.setContent(a.getContent().toString());
					
					mr.setPredictors(a.getInput());

					Template dependent = new Template();
					dependent.setName(String.format("Model-%s",a.getName()));		
					mr.setDependent(dependent);
					
					for (Property property:p) dependent.add(property);

					return mr;			
				
			} catch (Exception x) {
					 throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
			}
		
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid id "+key);
		

	}
	@Override
	protected AbstractUpdate createUpdateObject(ModelQueryResults entry)
			throws ResourceException {
		CreateModel update = new CreateModel(entry);
		return update;
	}
	@Override
	protected QueryURIReporter<ModelQueryResults, IQueryRetrieval<ModelQueryResults>> getURUReporter(
			Request baseReference) throws ResourceException {
		return new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(baseReference);
	}
}
