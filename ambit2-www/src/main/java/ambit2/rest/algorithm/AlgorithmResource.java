package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.core.data.model.Algorithm;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.AbstractUpdate;
import ambit2.db.update.model.CreateModel;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.StatusException;
import ambit2.rest.StringConvertor;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.query.QueryResource;

/**
 * 
 * @author nina
 *
 * @param <Q>
 */
public class AlgorithmResource<Q> extends QueryResource<IQueryRetrieval<ModelQueryResults>, ModelQueryResults> {
	protected AlgorithmCatalogResource<Algorithm> catalog;
	protected String[][] algorithms = new String[][] {
			//id,class,name
			{"pka","ambit2.descriptors.PKASmartsDescriptor","pKa-SMARTS","pKa Prediction of Monoprotic Small Molecules the SMARTS Way, Lee, Adam C., Yu, Jing-yu, and Crippen, Gordon M.,J. Chem. Inf. Model., 2008,  10.1021/ci8001815"},
			{"toxtree-cramer","toxTree.tree.cramer.CramerRules","Cramer rules","http://toxtree.sourceforge.net"}
	};
	public final static String idalgorithm = "idalgorithm";
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
	public AlgorithmResource(Context context, Request request, Response response) {
		super(context, request, response);
		catalog = new AlgorithmCatalogResource<Algorithm>(context,request,response) {
			@Override
			protected Iterator<Algorithm> createQuery(Context context,
					Request request, Response response) throws StatusException {
				ArrayList<Algorithm> q = new ArrayList<Algorithm>();
				Object key = getRequest().getAttributes().get(idalgorithm);
				if (key==null)
					for (String[] d : algorithms) q.add(new Algorithm(d[0]));
				else
					for (String[] d : algorithms)
						if (d[0].equals(key)) {
							q.add(new Algorithm(d[0]));
							break;
						}
				if (q.size()==0) {
					getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
					return null;
				} else	return q.iterator();
			}
			@Override
			public IProcessor<Iterator<Algorithm>, Representation> createConvertor(
					Variant variant) throws AmbitException, ResourceException {
				/*
				if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
					return new DocumentConvertor(new DatasetsXMLReporter(getRequest().getRootRef()));	
					*/
				if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
					return new StringConvertor(
							new AlgorithmHTMLReporter(getRequest().getRootRef(),
									getRequest().getAttributes().get(idalgorithm)==null
									),MediaType.TEXT_HTML);
				} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
					AlgorithmURIReporter r = new AlgorithmURIReporter(getRequest().getRootRef()) {
						@Override
						public void processItem(Algorithm item, Writer output) {
							super.processItem(item, output);
							try {output.write('\n');} catch (Exception x) {}
						}
					};
					return new StringConvertor(	r,MediaType.TEXT_URI_LIST);
				} else //html 	
					return new StringConvertor(
							new CatalogHTMLReporter(getRequest().getRootRef()),MediaType.TEXT_HTML);
				
			}
						
		};
		catalog.setCategory("rules");
	}
	@Override
	public Representation getRepresentation(Variant variant) {
		return catalog.getRepresentation(variant);
	}
	@Override
	public IProcessor<IQueryRetrieval<ModelQueryResults>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IQueryRetrieval<ModelQueryResults> createQuery(Context context,
			Request request, Response response) throws StatusException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean allowGet() {
		return true;
	}
	@Override
	public boolean allowPost() {
		return true;
	}
	@Override
	public void acceptRepresentation(Representation entity)
			throws ResourceException {
		if (getRequest().getAttributes().get(idalgorithm)!=null)
			createNewObject(entity);
		else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}
	@Override
	protected ModelQueryResults createObjectFromHeaders(Form requestHeaders)
			throws ResourceException {
		
		Object key = getRequest().getAttributes().get(idalgorithm);
		if (key==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Algorithm not defined");
		for (String[] keys : algorithms) 
			if (keys[0].equals(key)) { //ok, create object
				String dataset_uri = getParameter(requestHeaders,headers.dataset_id.toString(),headers.dataset_id.isMandatory());
				String params = getParameter(requestHeaders,headers.algorithm_parameters.toString(),headers.algorithm_parameters.isMandatory());  	
				ModelQueryResults mr = new ModelQueryResults();
				mr.setName(keys[2]);
				mr.setContent(keys[1]);
				mr.setPredictors(new Template("Empty"));
				
				Template dependent = new Template();
				dependent.setName(keys[1]);
				dependent.add(new Property(keys[1],new LiteratureEntry(keys[2],keys[3])));

				mr.setDependent(dependent);
				return mr;				
			}
		
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid id "+key);
		

/*
 * get properties from dataset
 * SELECT * FROM properties
where idproperty in
(
SELECT idproperty FROM property_values join
property_tuples using(id)
join tuples using(idtuple)
where id_srcdataset=19
);
 */
	}
	@Override
	protected AbstractUpdate createUpdateObject(ModelQueryResults entry)
			throws ResourceException {
		CreateModel update = new CreateModel(entry);
		return update;
	}
	@Override
	protected QueryURIReporter<ModelQueryResults, IQueryRetrieval<ModelQueryResults>> getURUReporter(
			Reference baseReference) throws ResourceException {
		return new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(baseReference);
	}
}
