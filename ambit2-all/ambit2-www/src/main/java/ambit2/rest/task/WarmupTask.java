package ambit2.rest.task;

import java.util.logging.Level;
import java.util.logging.Logger;



import org.opentox.aa.opensso.OpenSSOToken;
import org.opentox.dsl.aa.IAuthToken;
import org.opentox.dsl.task.ClientResourceWrapper;
import org.opentox.dsl.task.RemoteTask;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

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
		String warmup = "http://localhost:8080/ambit2/model?max=25&media=text/uri-list";
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
			return ssoToken;
		} else {
			warmupLogger.info("Login failed "+ssoToken.getAuthService());
			return null;
		}
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
			x.printStackTrace();
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
