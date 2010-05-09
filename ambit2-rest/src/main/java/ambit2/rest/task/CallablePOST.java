package ambit2.rest.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.rest.OpenTox;
import ambit2.rest.rdf.OT;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Wrapper for remote tasks to be used as super service
 * @author nina
 *
 */
public class CallablePOST implements Callable<Reference>{
	protected String[] algorithmsURI;
	protected String modelURI;
	protected MediaType media; 
	protected Representation input;
	protected ChallengeResponse authentication;
	protected ReferenceList results;
	protected Status status;
	protected long pollInterval = 1500;
	protected long pollTimeout = 1000L*60L*5L; //5 min
	protected Reference applicationRootReference;
	
	public long getPollTimeout() {
		return pollTimeout;
	}
	public void setPollTimeout(long pollTimeout) {
		this.pollTimeout = pollTimeout;
	}
	public long getPollInterval() {
		return pollInterval;
	}
	public void setPollInterval(long pollInterval) {
		this.pollInterval = pollInterval;
	}
	
	public CallablePOST(Form form,Reference root) throws Exception {
		this(MediaType.TEXT_URI_LIST,form.getWebRepresentation(),root);
	}	
	
	public CallablePOST(MediaType media, 
			  Representation input,Reference root) {
		this(media,input,null,root);
	}		
	public CallablePOST(MediaType media, 
			  Representation input,
			  ChallengeResponse authentication,
			  Reference root) {
		this(media,input,authentication,1500,root);
	}
	public CallablePOST(MediaType media, 
			  Representation input,
			  ChallengeResponse authentication, long pollInterval,
			  Reference root) {

		this.media = media;
		this.input = input;
		this.pollInterval = pollInterval;
		this.applicationRootReference = root;
	}
	public Reference call() throws Exception {
		long now = System.currentTimeMillis();
		Form form = new Form(input);
		String dataset_service = form.getFirstValue(OpenTox.params.dataset_service.toString());
		
		Reference newdataset = new Reference(form.getFirstValue(OpenTox.params.dataset_uri.toString()));
		if (newdataset==null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,OpenTox.params.dataset_uri + " not defined!");
		try {
			algorithmsURI = form.getValuesArray(OpenTox.params.algorithm_uri.toString());
			int nonnullalg = 0;
			for (int i=0; i < algorithmsURI.length;i++) nonnullalg += algorithmsURI[i]==null?0:1;
			if (nonnullalg==0) algorithmsURI = null;
			else {
				String[] newalg = new String[nonnullalg]; int j=0;
				for (int i=0; i < algorithmsURI.length;i++)	{
					if (algorithmsURI[i]==null) continue;
					newalg[j] = algorithmsURI[i]; j++;
				}
				algorithmsURI = newalg;
			}
			modelURI = form.getFirstValue(OpenTox.params.model_uri.toString());
			if (modelURI != null) {
				//now we retrieve descriptors from the algorithm
				Form frm = CallablePOST.getFeatures(modelURI,new Form(),OpenTox.params.algorithm_uri.toString());
				String[] model_descriptors = frm==null?null:frm.getValuesArray(OpenTox.params.algorithm_uri.toString());
				if (model_descriptors!=null) 
					if (algorithmsURI==null) algorithmsURI = model_descriptors;
					else {
						String[] newalg = new String[algorithmsURI.length+model_descriptors.length];
						for (int i=0; i < algorithmsURI.length;i++)	newalg[i] = algorithmsURI[i];
						for (int i=0; i < model_descriptors.length;i++)	newalg[i+algorithmsURI.length] = model_descriptors[i];
						algorithmsURI = newalg;
					}
				
			}
			
			if ((algorithmsURI==null) || (algorithmsURI.length<1)) {
				if (modelURI == null)
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,OpenTox.params.algorithm_uri + " not defined!");
			} else if(algorithmsURI.length>1) {
				if (dataset_service==null)
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No dataset service");

				RemoteTask createDataTask = new RemoteTask(new Reference(dataset_service),media,new Form(input).getWebRepresentation(),Method.POST,authentication);
				createDataTask = wait(createDataTask,now);
				if (createDataTask.error!=null) throw createDataTask.error;
				Reference dataset = createDataTask.getResult();
				
				RemoteTaskPool pool = new RemoteTaskPool();
				for (String url:algorithmsURI) {
					Form params = new Form();
					params.add(OpenTox.params.dataset_uri.toString(),dataset.toString());
					params.add(OpenTox.params.dataset_service.toString(),dataset_service);
					RemoteTask task = new RemoteTask(new Reference(url),media,params.getWebRepresentation(),Method.POST,authentication);
					pool.add(task);
				}

				while (pool.poll()>0) {

					Thread.sleep(pollInterval);
					Thread.yield();
					if ((System.currentTimeMillis()-now) > pollTimeout)
						break;
				}


				Form params = new Form();
				for (RemoteTask task : pool.pool) {
					if (task.error!=null) 
						throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
								String.format("%s %s",task.getUrl(),task.error.getMessage()));
					params.add(OpenTox.params.dataset_uri.toString(),task.getResult().toString());
				}
				params.add(OpenTox.params.dataset_service.toString(),dataset_service);
				RemoteTask task = new RemoteTask(dataset,media,params.getWebRepresentation(),Method.PUT,authentication);
				task = wait(task,now);
				newdataset = task.getResult();
				
			} else {
				RemoteTask task = new RemoteTask(new Reference(algorithmsURI[0]),media,input,Method.POST,authentication);
				task = wait(task,now);
				Reference result = task.getResult();
				newdataset = result;
			}
			//modeluri
			if (modelURI!=null) {
				Form params = new Form();
				params.add(OpenTox.params.dataset_service.toString(),dataset_service);
				params.add(OpenTox.params.dataset_uri.toString(),newdataset.toString());
				RemoteTask task = new RemoteTask(new Reference(modelURI),media,params.getWebRepresentation(),Method.POST,authentication);
				task = wait(task,now);
				return task.getResult();
			} else return newdataset;
		} catch (Exception x) {
			//x.printStackTrace();
			throw x;
		} finally {
			System.out.println(String.format("Elapsed %s", System.currentTimeMillis()-now));
		}
	}

	protected RemoteTask wait(RemoteTask task, long now) throws Exception {
		String result = task.getResult().toString();
		while (!task.poll()) {
			if (task.error!=null) throw task.error;
			Thread.sleep(pollInterval);
			Thread.yield();
			if ((System.currentTimeMillis()-now) > pollTimeout) 
				throw new ResourceException(Status.SERVER_ERROR_GATEWAY_TIMEOUT,
						String.format("%s %s ms > %s ms",result==null?algorithmsURI[0].toString():result,System.currentTimeMillis()-now,pollTimeout));
		}
		
		if (task.error!=null) 
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
					String.format("%s %s",result==null?algorithmsURI[0].toString():result,task.error.getMessage()),
					task.error);
		return task;
	}
	public static String getSparql(String name)  throws Exception {
		InputStream in  = CallablePOST.class.getClassLoader().getResourceAsStream(name);
        StringBuilder sparqlQuery = new StringBuilder();
        String line;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while ((line = reader.readLine()) != null) {
            	sparqlQuery.append(line).append("\n");
            }
        } finally {
            in.close();
        }
        return sparqlQuery.toString();
	}
	public static Form getFeatures(String modeluri, Form form, String varname) throws Exception {
		List<String> features = new ArrayList<String>();
        QueryExecution qe = null;
        OntModel model = null;
        try {
        	model = OT.createModel(null, new Reference(modeluri),MediaType.APPLICATION_RDF_XML);
			Query query = QueryFactory.create(String.format(getSparql("sparql/ModelIndependentVariables.sparql").toString(),modeluri,modeluri));
			qe = QueryExecutionFactory.create(query,model);
			ResultSet results = qe.execSelect();
			
			
			while (results.hasNext()) {
				QuerySolution solution = results.next();
				Resource var = solution.getResource("vars");
				features.add(var.getURI());
			}
			if (features.size()==0) return null;
	
        } catch (Exception x) {
        	throw x;
        } finally {
        	try {qe.close();} catch (Exception x) {}
        	try {model.close();} catch (Exception x) {}
        }
		
        //get features & algorithm; read each feature separately, to use less memory
        for (String feature:features) 
	        try {
	        	model = OT.createModel(null, new Reference(feature),MediaType.APPLICATION_RDF_XML);	
				
				Query query = QueryFactory.create(getSparql("sparql/FeatureAlgorithm.sparql"));
				qe = QueryExecutionFactory.create(query,model);
				ResultSet results = qe.execSelect();
				while (results.hasNext()) {
					QuerySolution solution = results.next();
					Resource var = solution.getResource("algorithm");
					form.add(varname,var.getURI());
				}
				
	        }  catch (Exception x) {
	        	x.printStackTrace();
	        	throw x;
	        } finally {
	        	try {qe.close();} catch (Exception x) {}
	        	try {model.close();} catch (Exception x) {}
	        }
	       return form;
	}
}
