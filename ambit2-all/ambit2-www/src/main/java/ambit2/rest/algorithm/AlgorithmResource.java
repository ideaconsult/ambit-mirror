package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import ambit2.descriptors.processors.DescriptorsFactory;
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

	protected Object[][] algorithms = new Object[][] {
			//id,class,name
			{"1","pKa","ambit2.descriptors.PKASmartsDescriptor",null},
			{"2","ToxTree: Cramer rules","toxTree.tree.cramer.CramerRules",null},
			{"3","ToxTree: Extended Cramer rules","cramer2.CramerRulesWithExtensions",null},
			{"4","ToxTree: Eye irritation","eye.EyeIrritationRules",null},
			{"5","ToxTree: Skin irritation","sicret.SicretRules",null},
			{"6","ToxTree: Structure Alerts for the in vivo micronucleus assay in rodents","mic.MICRules",null},
			{"7","ToxTree: Michael acceptors","michaelacceptors.MichaelAcceptorRules",null},
			{"8","ToxTree: Benigni/Bossa rules for carcinogenicity and mutagenicity","mutant.BB_CarcMutRules",null},
			//{"ToxTree: START biodegradation and persistence plug-in","mutant.BB_CarcMutRules",null},
			{"9","ToxTree: ILSI/Kroes decision tree for TTC","toxtree.plugins.kroes.Kroes1Tree",
				new Property("DailyIntake","\u00B5g/day", new LiteratureEntry("User input","http://toxtree.sourceforge.net"))},
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
				try {
					ArrayList<Algorithm> q = new ArrayList<Algorithm>();
					Object key = getRequest().getAttributes().get(idalgorithm);
					
					if (key==null)
						for (Object[] d : algorithms) {
							Algorithm alg = new Algorithm(d[1].toString());
							alg.setId(Integer.parseInt(d[0].toString()));
							q.add(alg);
						}
					else { 
						key = Reference.decode(key.toString());
						
						for (Object[] d : algorithms)
							if (d[0].equals(key)) {
								Algorithm alg = new Algorithm(d[1].toString());
								alg.setId(Integer.parseInt(d[0].toString()));
								q.add(alg);
								break;
							}
					}
					if (q.size()==0) {
						getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
						return null;
					} else	return q.iterator();
				} catch (Exception x) {
					throw new StatusException(new Status(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage()));
				}
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
		if (getRequest().getAttributes().get(idalgorithm)!=null) {
			createNewObject(entity);
			getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
			getResponse().setEntity(null);
		} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}
	@Override
	protected ModelQueryResults createObjectFromHeaders(Form requestHeaders)
			throws ResourceException {
		
		Object key = getRequest().getAttributes().get(idalgorithm);
		if (key==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Algorithm not defined");
		key = Reference.decode(key.toString());
		for (Object[] keys : algorithms) if (keys[0].equals(key)) //ok, create object
			try {
					List<Property> p = DescriptorsFactory.createDescriptor2Properties(keys[2].toString());
					if ((p == null)||(p.size()==0)) throw new ResourceException(Status.SERVER_ERROR_INTERNAL,"Can't create "+key);

					String dataset_uri = getParameter(requestHeaders,headers.dataset_id.toString(),headers.dataset_id.isMandatory());
					String params = getParameter(requestHeaders,headers.algorithm_parameters.toString(),headers.algorithm_parameters.isMandatory());  	
					ModelQueryResults mr = new ModelQueryResults();
					mr.setName(keys[1].toString());
					mr.setContent(keys[2].toString());
					
					if (keys[3]==null)
						mr.setPredictors(new Template("Empty"));
					else {
						Template predictors = new Template(String.format("Predictors-%s",keys[1]));
						predictors.add((Property)keys[3]);
						mr.setPredictors(predictors);
					}

					Template dependent = new Template();
					dependent.setName(String.format("Model-%s",keys[1]));		
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
			Reference baseReference) throws ResourceException {
		return new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(baseReference);
	}
}
