package ambit2.rest.task;

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.idea.restnet.c.task.ClientResourceWrapper;
import net.idea.restnet.i.aa.IAuthToken;

import org.opentox.aa.IOpenToxUser;
import org.opentox.aa.OpenToxUser;
import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;
import org.opentox.dsl.task.RemoteTask;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.rest.aa.opensso.OpenSSOServicesConfig;

/**
 * 
 * @author nina
 *
 * @param <USERID>
 */
public class WarmupTask<USERID> extends CallableProtectedTask<USERID> implements IAuthToken {
	protected OpenSSOToken ssoToken;
	protected Logger warmupLogger;
	protected long timeout = 50000;
	protected boolean aaEnabled ;
	public WarmupTask(USERID token, boolean aaEnabled) {
		super(token);
		this.aaEnabled = aaEnabled;
		warmupLogger = Logger.getLogger(this.getClass().getName());
	}

	@Override
	public TaskResult doCall() throws Exception {
		Thread.sleep(1000);
		Thread.yield();
		long time = System.currentTimeMillis();
		try {
			 ssoToken = login();
			 warmUpOntology(ssoToken);
			 String compoundURI = getCompoundAndWarmUpInChI(ssoToken);
			 warmupLogger.info(compoundURI);
			 String[] models = getModelsWarmup(ssoToken);
			 if (models!=null) {
				 for (String model:models)  warmupLogger.info(model);
				 if (compoundURI!=null) {
					 ClientResourceWrapper.setTokenFactory(this);
					 runModelsWarmUp(compoundURI.trim(),models);
				 }	 
			 }	 
			 warmupLogger.info(compoundURI);
		} finally {
			if (aaEnabled && (ssoToken!=null)) {
				ssoToken.logout();
				warmupLogger.info("Logout ");
			}
			ClientResourceWrapper.setTokenFactory(null);
			warmupLogger.info("Warmup completed at "+(System.currentTimeMillis()-time) + " ms.");
		}
		/*
		try {
			//ping the policy server
			String warmup = "http://localhost:8080/ambit2/admin/policy?search=http%3A%2F%2Flocalhost%3A8080%2Fambit2%2Fdataset";
			client = new ClientResource(warmup);
			r = client.get();
			logger.info("Policy service "+client.getStatus().toString());
			if (r!=null) {
				String text = r.getText();
			} ;
		} catch (Exception x) {
			throw x;
		} finally {
			try {r.release();} catch (Exception x) {client = null;}
			try {client.release();} catch (Exception x) {client = null;}
		}				
		
		return new TaskResult("http://localhost");
		*/
		return new TaskResult("http://localhost:8080/ambit2");
	}

	protected String warmUpOntology(OpenSSOToken token) throws Exception {
		warmupLogger.info("Ontology server warmup ...");
		ClientResourceWrapper client = null;
		Representation r = null;
		String warmup = "http://localhost:8080/ambit2/sparqlendpoint";
		try {
			ClientResourceWrapper.setTokenFactory(this);
			client = new ClientResourceWrapper(warmup);
			r = client.get();
			warmupLogger.info(client.getStatus().toString());
			if (Status.SUCCESS_OK.equals(client.getStatus()) && (r!=null)) {
				warmup = r.getText().trim();
				warmupLogger.info("Ontology service retrieved "+warmup);
				try {r.release();} catch (Exception x) {r = null;}
				
				client.setReference(warmup);
				r = client.get();
				if (Status.SUCCESS_OK.equals(client.getStatus()) && (r!=null)) {
					String results = r.getText();
					warmupLogger.info(results);
				}

			} 
		} catch (Exception x) {
			warmupLogger.log(Level.WARNING,warmup,x);
		} finally {
			try {if (r!=null)r.release();} catch (Exception x) {r = null;}
			try {client.release();} catch (Exception x) {client = null;}
			ClientResourceWrapper.setTokenFactory(null);
		}		
		return null;
	}
	
	protected String getCompoundAndWarmUpInChI(OpenSSOToken token) throws Exception {
		warmupLogger.info("InChI & DB lookup warmup ...");
		ClientResourceWrapper client = null;
		Representation r = null;
		String warmup = "http://localhost:8080/ambit2/query/compound/search/all?type=smiles&property=&search=c1ccccc1&max=1&media=text/uri-list";
		try {
			ClientResourceWrapper.setTokenFactory(this);
			client = new ClientResourceWrapper(warmup);
			r = client.get();
			warmupLogger.info(client.getStatus().toString());
			if (Status.SUCCESS_OK.equals(client.getStatus()) && (r!=null)) {
				return r.getText();
			} 
		} catch (Exception x) {
			warmupLogger.log(Level.WARNING,warmup,x);
			throw x;
		} finally {
			try {r.release();} catch (Exception x) {r = null;}
			try {client.release();} catch (Exception x) {client = null;}
			ClientResourceWrapper.setTokenFactory(null);
		}		
		return null;
	}
	
	protected String[] getModelsWarmup(OpenSSOToken token) throws Exception {
		warmupLogger.info("Models warmup ...");
		ClientResourceWrapper client = null;
		Representation r = null;
		String warmup = "http://localhost:8080/ambit2/model?max=50&media=text/uri-list";
		try {
			ClientResourceWrapper.setTokenFactory(this);
			client = new ClientResourceWrapper(warmup);
			r = client.get();
			warmupLogger.info(client.getStatus().toString());
			if (Status.SUCCESS_OK.equals(client.getStatus()) && (r!=null)) {
				String models = r.getText();
				return models.split("\n");
			} 
		} catch (Exception x) {
			warmupLogger.log(Level.WARNING,warmup,x);
			throw x;
		} finally {
			try {r.release();} catch (Exception x) {r = null;}
			try {client.release();} catch (Exception x) {client = null;}
			ClientResourceWrapper.setTokenFactory(null);
		}		
		return null;
	}
	
	public void runModelsWarmUp(String dataset,String[] models) throws Exception {
		if ((models==null) || (models.length==0)) {
			logger.log(Level.WARNING,"No models, exiting warmup...");
			return;
		}
		MyThread[] threads = new MyThread[models.length];
		for (int i= 0; i < models.length; i++) try {
			threads[i] = new MyThread(dataset,models[i],1000,warmupLogger,50000,this);
			threads[i].start();
		} catch (Exception x) {
			logger.log(Level.SEVERE,"models thread start loop",x);
		}
		logger.log(Level.INFO,"Waiting threads to join");
		for (int i= 0; i < threads.length; i++) try {
			 try {
			       threads[i].join();
			       logger.log(Level.INFO,threads[i].toString());
			    } catch (InterruptedException x) {
			    	logger.log(Level.SEVERE,"interrupted",x);    	
			    }
		} catch (Exception x) {
			logger.log(Level.SEVERE,"join loop",x);
		}
		logger.log(Level.INFO,"completed");
		long time = 0;
		String s = "";
		for (int i= 0; i < models.length; i++) {
				time += threads[i].time;
				s += "\t" + threads[i].time;
		}
	    logger.log(Level.INFO,s);
		logger.log(Level.INFO,"Average time per model prediction thread "+Long.toString(time/models.length)+ " ms.");
	}	
	


	protected OpenSSOToken login() throws Exception {
		if (!aaEnabled) return new OpenSSOToken(null);
		OpenSSOServicesConfig config = OpenSSOServicesConfig.getInstance();
		OpenSSOToken ssoToken = new OpenSSOToken(config.getOpenSSOService());
		String username = config.getTestUser();
		String pass = config.getTestUserPass();
		if (ssoToken.login(username,pass)) {
			warmupLogger.info("Login to "+ ssoToken.getAuthService() + " successfull "+ssoToken.getToken());
			
			warmUpPolicy(ssoToken,config);
			return ssoToken;
		} else {
			warmupLogger.info("Login failed "+ssoToken.getAuthService());
			return null;
		}
		
		
	}
	
	protected void warmUpPolicy(OpenSSOToken ssoToken,OpenSSOServicesConfig config) throws Exception {
		warmupLogger.info("Policy server "+config.getPolicyService()+"warmup ...");
		OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
		String uri = "http://example.org";
		try {
			policy.createUserPolicy("guest", ssoToken, uri, new String[]{"GET"});
			warmupLogger.warning("Policy created");	
		} catch (Exception x) {
			warmupLogger.warning(x.getMessage());	
		}
		try {
			deleteAllPolicies(policy,ssoToken, new URL(uri));
			warmupLogger.warning("Policies deleted");
		} catch (Exception x) {
			warmupLogger.warning(x.getMessage());	
		}
	}
	

	protected void deletePolicy(URL url,String token) throws Exception {
		if (token==null) return;
		try {
			OpenSSOServicesConfig config = OpenSSOServicesConfig.getInstance();
			OpenSSOPolicy policyTools = new OpenSSOPolicy(config.getPolicyService());
			OpenSSOToken ssoToken = new OpenSSOToken(config.getOpenSSOService());
			ssoToken.setToken(token);
			deleteAllPolicies(policyTools,ssoToken,url);
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,String.format("Error deleting policies for %s",url),x);
		}
	}
	
	public void deleteAllPolicies(OpenSSOPolicy policyTools,OpenSSOToken ssoToken,URL url) throws Exception {
		IOpenToxUser user = new OpenToxUser();
		warmupLogger.fine("Deleting policies for "+url.toString());
		Hashtable<String, String> policies = new Hashtable<String, String>();
		int status = policyTools.getURIOwner(ssoToken, url.toExternalForm(), user, policies);
		if (200 == status) {
			Enumeration<String> e = policies.keys();
			while (e.hasMoreElements()) {
				String policyID = e.nextElement();
				policyTools.deletePolicy(ssoToken,policyID);
				warmupLogger.fine("Policy "+policyID + "deleted");
			}
		} //else throw new RestException(status);
	}
	
	@Override
	public String getToken() {
		if (ssoToken==null) return null;
		else return ssoToken.getToken();
	}
	


	


}

class MyThread extends Thread {
	public long sleep;
	public long time;
	long timeout;
	public String dataset;
	public Reference calculated;
	public String modelUri; 
	public Exception error = null;
	protected Logger threadLogger;
	IAuthToken tokenFactory;
	public MyThread(String dataset,String modelUri, long sleep, Logger threadLogger, long timeout, IAuthToken tokenFactory) {
		super(modelUri);
		this.sleep = sleep;
		this.dataset = dataset;
		this.modelUri = modelUri;
		this.timeout = timeout;
		this.threadLogger = threadLogger;
		this.tokenFactory = tokenFactory;
	}
	
	@Override
	public void run() {
		time = System.currentTimeMillis();
		try {
			Form form = new Form();
			form.add("dataset_uri", dataset);
			ClientResourceWrapper.setTokenFactory(tokenFactory);
			RemoteTask task = new RemoteTask(
					new Reference(modelUri),MediaType.TEXT_URI_LIST, form.getWebRepresentation(),Method.POST);
			threadLogger.info("Model " + task.getUrl());
			while (!task.poll()) {
				Thread.yield();
				Thread.sleep(sleep);
				if ((System.currentTimeMillis() - time)>timeout) {
					threadLogger.log(Level.INFO,timeout + "ms timeout expired",task);
					calculated = null;
					return;
				} else
					threadLogger.log(Level.INFO,"poll model task",task);
			}
			if (task.isERROR()) throw task.getError();
			else calculated = task.getResult();
			
		} catch (Exception x) {
			error = x;
			threadLogger.log(Level.SEVERE,this.getName(),x);
		} finally {
			ClientResourceWrapper.setTokenFactory(null);
			time = System.currentTimeMillis() - time;
		}
	}
	
	public String toString() {
		return String.format("%s\t%d ms\t%s\t%s",getName(),time,dataset,calculated);
	};

	
}
