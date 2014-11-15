package ambit2.rest.task;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import net.idea.restnet.rdf.ns.OT;

import org.opentox.dsl.task.RemoteTask;
import org.opentox.dsl.task.RemoteTaskPool;
import org.restlet.Context;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.rest.OpenTox;
import ambit2.rest.task.Task.TaskProperty;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;



/**
 * Creates a compatible dataset, given a model and a dataset of compounds
 * 1)Retrieve model representation
 * 2)Retrieve independent features representation. 
 * 2a)create a new dataset/Rx with independent features relevant to model
 * 3)Get ot:hasSource for each independent feature
 * 4)launch descriptor calculation for the dataset and feature source - only for compounds without feature values
 * 5)/dataset/Rx should be ready
 * 6)submit the new dataset to prediction
 * @author nina
 *
 */
public class CallableDatasetCreator  implements Callable<Reference>  {
	static Logger logger = Logger.getLogger(CallableDatasetCreator.class.getName());
	protected PropertyChangeSupport support;
	protected Reference applicationRoot;
	protected String modelURI;
	protected String datasetURI;
	protected Reference datasetService;
	protected Form input;
	protected RemoteTaskPool jobs = new RemoteTaskPool();
	protected Hashtable<Reference,RemoteTask> algorithms = new Hashtable<Reference, RemoteTask>(); 
	protected Form featuresQuery = new Form();
	/*
	 * dataset_service=http://ambit.uni-plovdiv.bg:8080/ambit2/dataset
	 * curl -X POST -d "dataset_uri=http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/R4666" http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/algorithm/CDKPhysChem/BCUTDescriptor 
	curl -X POST -d "dataset_uri=http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/194" -d "prediction_feature=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/12236" http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/model/TUMOpenToxModel_kNN_6 -v
	 */
	public CallableDatasetCreator(Form form, Reference applicationRoot, Reference datasetService,
			Context context) throws ResourceException {
		super();
		this.applicationRoot = applicationRoot; 
		//datasetService = new Reference(String.format("%s%s",applicationRoot.toString(),OpenTox.URI.dataset.getURI()));
		this.datasetService=datasetService;
		if (OpenTox.params.dataset_uri.getFirstValue(form)==null) 
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty dataset_uri!");
		this.datasetURI = OpenTox.params.dataset_uri.getFirstValue(form).toString();
		
		if (OpenTox.params.model_uri.getFirstValue(form)==null) 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty model_uri!");
		this.modelURI = OpenTox.params.model_uri.getFirstValue(form).toString();
		input = new Form();
		input.add(OpenTox.params.dataset_uri.toString(),this.datasetURI);
		input.add(OpenTox.params.dataset_service.toString(),this.datasetService.toString());
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;  
 
	}
	
	public Reference call() throws Exception {
		try {
			Reference result =  callInternal();
			logger.fine(result.toString());
			return result;
		} catch (Exception x) {
			throw x;
		} finally {
			//clean up listeners
	    	if (support!=null) {
	    		PropertyChangeListener[] listeners = support.getPropertyChangeListeners();
	    		for (PropertyChangeListener l: listeners)
	    			support.removePropertyChangeListener(l);
	    	}
		}
	}
	
	protected Reference callInternal() throws Exception {
		RemoteTask currentJob = null;
		//create subsets with missing descriptors
		run(new Reference(modelURI));
		jobs.run();
		jobs.clear();
		//run descriptor calculations
		Enumeration<Reference> algs = algorithms.keys();
		while (algs.hasMoreElements()) {
			Reference alg = algs.nextElement();
			RemoteTask task = algorithms.get(alg);
			if (Status.SUCCESS_OK.equals(task.getStatus())) {
				Form input = new Form();
				input.add(OpenTox.params.dataset_uri.toString(),task.getResult().toString());
				input.add(OpenTox.params.dataset_service.toString(),datasetService.toString());
				jobs.add(new RemoteTask(alg,MediaType.TEXT_URI_LIST,input.getWebRepresentation(),Method.POST));
			}
			
		}
		
		jobs.run();
 		
		//here we should have everything calculated 
		jobs.clear();
		algorithms.clear();
		
		//now create dataset with a nice name to send to model service
		firePropertyChange(TaskProperty.PROPERTY_NAME.toString(),null,String.format("Create dataset with a nice name to send to model service %s",datasetURI));
		input.clear();
		Reference dataset = new Reference(datasetURI);
		Form query = dataset.getQueryAsForm();
		Iterator<Parameter> features = featuresQuery.iterator();
		while (features.hasNext()) query.add(features.next());
		
		dataset.setQuery(query.getQueryString());
		input.add(OpenTox.params.dataset_uri.toString(),dataset.toString());
		input.add(OpenTox.params.dataset_service.toString(),datasetService.toString());
		currentJob = new RemoteTask(datasetService,MediaType.TEXT_URI_LIST,input.getWebRepresentation(),Method.POST);
		jobs.add(currentJob);
		jobs.run();
		jobs.clear();
		
		
		
		if (Status.SUCCESS_OK.equals(currentJob.getStatus())) {
			if (currentJob.getResult() == null) 
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,currentJob.toString());
			//and finally run the model
			input.clear();
			input.add(OpenTox.params.dataset_uri.toString(),currentJob.getResult().toString());
			input.add(OpenTox.params.dataset_service.toString(),datasetService.toString());
			
			firePropertyChange(TaskProperty.PROPERTY_NAME.toString(),null,String.format("Start model (finally) %s",modelURI));
			currentJob = new RemoteTask(new Reference(modelURI),MediaType.TEXT_URI_LIST,input.getWebRepresentation(),Method.POST);
			jobs.add(currentJob);
			jobs.run();		
			
			jobs.clear();
			if (Status.SUCCESS_OK.equals(currentJob.getStatus()))
					return currentJob.getResult();

		} 
		if (currentJob.getError()!= null) 
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,currentJob.toString(),currentJob.getError());
		else throw new ResourceException(currentJob.getStatus());		

	}
	
	protected void run(Reference modelURI) throws Exception {
		OntModel jenaModel = null;
		StmtIterator features = null;
		try {
			firePropertyChange(TaskProperty.PROPERTY_NAME.toString(),null,String.format("Retrieving model %s",modelURI));
			jenaModel = OT.createModel(OntModelSpec.OWL_DL_MEM);
			jenaModel = (OntModel) OT.createModel(jenaModel, modelURI,MediaType.APPLICATION_RDF_XML);
			features =  jenaModel.listStatements(
					new SimpleSelector(null,OT.OTProperty.independentVariables.createProperty(jenaModel),(RDFNode)null));
			int count = 0;
			while (features.hasNext()) {
				Statement st = features.next();
				RDFNode feature = st.getObject();
				if (feature.isURIResource()) {
					//read feature
					jenaModel =  (OntModel)OT.createModel(jenaModel,new Reference(((Resource)feature).getURI()),MediaType.APPLICATION_RDF_XML);
					count++;
				
				}
			}
			if (count==0) {
				firePropertyChange(TaskProperty.PROPERTY_NAME.toString(),null,String.format("No descriptors to calcualte for model %s",modelURI));
				return;
			} else
			firePropertyChange(TaskProperty.PROPERTY_NAME.toString(),null,String.format("Prepare dataset for model %s",modelURI));
			//everything in, do some querying
			try {
				launchFeaturesCalculations(jenaModel,modelURI.toString());
			} catch (ResourceException x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,modelURI.toString(),x);
			} catch (Exception x) {
				throw x;
			}
		} catch (ResourceException x) {
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
					String.format("[%s] %s",modelURI.toString(),x.getMessage(),x));
		} catch (Exception x) {
			throw x;
		} finally {
			try { features.close(); } catch (Exception x) {}
			try { jenaModel.close(); } catch (Exception x) {}
		}
	}
	
	protected void launchFeaturesCalculations(Model jenaModel, String model) {
		String queryString =
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
		"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
		"\n"+
		"	select DISTINCT ?vars ?algorithm\n"+
		"	where {\n"+
		"		%s rdf:type ot:Model.\n"+
		"	    %s ot:independentVariables  ?vars.\n"+
		"	    ?vars rdf:type ot:Feature.\n"+
		"	    ?vars ot:hasSource ?algorithm.\n"+
		"       OPTIONAL { ?src rdf:type ot:Algorithm. } \n"+ 
		"	}\n"+
		"		ORDER by ?algorithm";		

		String param = (model==null)?"?Model":String.format("<%s>", model);
		Query query = QueryFactory.create(String.format(queryString,param,param));
		// Execute the query and obtain results
		
		Reference algorithm = null;
		List<String> features = new ArrayList<String>();
		QueryExecution qe = QueryExecutionFactory.create(query,jenaModel);
		ResultSet results = qe.execSelect();
		while (results.hasNext()) {
			QuerySolution s = results.next();
			RDFNode var = s.get("vars");
			RDFNode node = s.get("algorithm");
			//algorithm
			Reference ref = null;
			if (node.isLiteral())
					ref = new Reference(((Literal)node).getString());
			else if (node.isURIResource()) {
					ref = new Reference(((Resource)node).getURI());
			} else continue;
			
			//feature
			String feature = null;
			if (var.isLiteral())
				feature = ((Literal)var).getString();
			else if (var.isURIResource()) {
				feature = ((Resource)var).getURI();
			} else  continue;
			
			featuresQuery.add(OpenTox.params.feature_uris.toString(),feature);
			
			if (!ref.equals(algorithm)) {
				//send for calculations only structures without values
				if (algorithm!=null) addJob(algorithm,features);
				features.clear();
				algorithm = ref;
			} 
			features.add(feature);
			
		}
		if (algorithm!=null) addJob(algorithm,features);
		qe.close();
	}
	
	protected void addJob(Reference algorithm, List<String> features) {
		Reference filter = createFilterReference(
				applicationRoot.toString(), 
				datasetURI, 
				features.toArray(new String[] {}));
		input.removeAll(OpenTox.params.dataset_uri.toString());
		input.add(OpenTox.params.dataset_uri.toString(),filter.toString());
		input.removeAll(OpenTox.params.dataset_service.toString());
		firePropertyChange(TaskProperty.PROPERTY_NAME.toString(),null,String.format("Missing values"));		
		RemoteTask job = new RemoteTask(datasetService,MediaType.TEXT_URI_LIST,input.getWebRepresentation(),Method.POST);
		algorithms.put(algorithm,job);
		jobs.add(job);
		firePropertyChange(TaskProperty.PROPERTY_NAME.toString(),null,String.format("Start descriptor calculation %s",algorithm));
	}
	
	public static Reference createFilterReference(String root,String dataset, String[] features) {
		Form form = new Form();
		for (String feature:features)
		form.add(OpenTox.params.feature_uris.toString(),feature);
		form.add(OpenTox.params.dataset_uri.toString(),dataset);

		Reference ref = new Reference(String.format("%s%s%s",root,
				OpenTox.URI.query.getURI(),
				OpenTox.URI.missingValues.getURI()
				));
		ref.setQuery(form.getQueryString());
		return ref;
	}
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
    	if (support ==null) support = new PropertyChangeSupport(this);
    	support.addPropertyChangeListener(listener);
    }
    public synchronized void removePropertyChangeListener(
			PropertyChangeListener listener) {
    	if (support != null) support.removePropertyChangeListener(listener);
    }
    public void firePropertyChange(String propertyName, 
			Object oldValue, Object newValue) {
    	if (support!=null) {
    	    support.firePropertyChange(propertyName,oldValue, newValue);

    	}
    }
    @Override
    protected void finalize() throws Throwable {
    	if (support!=null) {
    		PropertyChangeListener[] listeners = support.getPropertyChangeListeners();
    		for (PropertyChangeListener l: listeners)
    			support.removePropertyChangeListener(l);
    	}
    	super.finalize();
    }
}



