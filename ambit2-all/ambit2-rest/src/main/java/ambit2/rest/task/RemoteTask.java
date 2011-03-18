package ambit2.rest.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import org.opentox.dsl.task.ClientResourceWrapper;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

/**
 * Convenience class to launch and poll remote POST jobs
 * @author nina
 *
 */
public class RemoteTask implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final Reference url;
	protected Status status = null;
	protected Reference result = null;
	protected Exception error = null;
	
	protected void setError(Exception error) {
		this.error = error;
	}
	public Exception getError() {
		return error;
	}
	public RemoteTask(Reference url,MediaType media, 
			  Representation input,
			  Method method) throws ResourceException {
		super();
		this.url = url;
		Representation r=null;
		ClientResourceWrapper client = null;
		
		try {
			client = new ClientResourceWrapper(url);
			client.setFollowingRedirects(true);
			client.setRetryAttempts(1);
			client.setRetryOnError(false);
			
			
			if (method.equals(Method.POST)) 
				r = client.post(input,media);
			else if (method.equals(Method.PUT))
				r = client.put(input,media);
			else if (method.equals(Method.DELETE))
				r = client.delete(media);
			else if (method.equals(Method.GET))
				r = client.get(media);
			else throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
			this.status = client.getStatus();
			
			if (!r.isAvailable()) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
						String.format("[%s] Representation not available %s",this.status,url));
			}
			
			result = handleOutput(r.getStream(),status,null);
		} catch (ResourceException x) {
			status = x.getStatus();
			try { 
				error = new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
						String.format("URL=%s [%s] %s",url,
								x.getStatus(),
								client.getResponseEntity()==null?"":client.getResponseEntity().getText()),
						x); 
			}	catch (Exception xx) { error = x; }
		} catch (Exception x) {
			setError(x);
			status = null;
		} finally {
			try { if (r != null) r.release(); } catch (Exception x) { x.printStackTrace();}
			try { client.release(); } catch (Exception x) { x.printStackTrace();}
		}
	}	
	
	
	public boolean isCompletedOK() {
		return Status.SUCCESS_OK.equals(status);
	}
	public boolean isCancelled() {
		return Status.SERVER_ERROR_SERVICE_UNAVAILABLE.equals(status);
	}
	public boolean isAccepted() {
		return Status.SUCCESS_ACCEPTED.equals(status);
	}	

	public boolean isERROR() {
		return error != null;
	}		
	public boolean isDone() {
		return isCompletedOK() || isERROR() || isCancelled();
	}		
	public Reference getUrl() {
		return url;
	}

	public Status getStatus() {
		return status;
	}

	public Reference getResult() {
		return result;
	}

	@Override
	public String toString() {
		return String.format("URL: %s\tResult: %s\tStatus: %s\t%s", url,result,status,error==null?"":error.getMessage());
	}
	/**
	 * returns true if ready
	 * @return
	 */
	public boolean poll() {

			
		if (isDone()) return true;

		ClientResourceWrapper client = null;
		Representation r = null;
		
		try {
			
			client = new ClientResourceWrapper(result.toString());
			client.setRetryOnError(false);
			client.setRetryAttempts(1);
			client.setFollowingRedirects(true);
	        r = client.get(MediaType.TEXT_URI_LIST);
			status = client.getStatus();
			if (Status.SERVER_ERROR_SERVICE_UNAVAILABLE.equals(status)) {
				return true;
			}
			
//			if (!r.getEntity().isAvailable()) throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,String.format("Representation not available %s",result));
			
			result = handleOutput(r.getStream(),status,result);
//			System.out.println(String.format("poll %s %s",status,result));
		} catch (ResourceException x) {
			setError(x);
			status = x.getStatus();
		} catch (Exception x) {
			setError(x);
			status = null;
		} finally {
			try {r.release();} catch (Exception x) {}
			try {
				client.release(); client = null;
			} catch (Exception x) {}

		}
		return isDone();
	}
	/**
	 * 
	 * @param in
	 * @param status
	 * @param url  the url contacted - for returning proper error only
	 * @return
	 * @throws ResourceException
	 */
	protected Reference handleOutput(InputStream in,Status status,Reference url) throws ResourceException {
		Reference ref = null;
		if (Status.SUCCESS_OK.equals(status) 
						|| Status.SUCCESS_ACCEPTED.equals(status) 
						|| Status.SUCCESS_CREATED.equals(status) 
						//|| Status.REDIRECTION_SEE_OTHER.equals(status)
						|| Status.SERVER_ERROR_SERVICE_UNAVAILABLE.equals(status)
						) {

			if (in==null) {
				String msg = String.format("Error reading response from %s: %s. Status was %s", url==null?getUrl():url, "Empty content",status);
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,msg);
			}
			
			int count=0;
			try {

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = reader.readLine())!=null) {
					ref = new Reference(line.trim());
					count++;
				}
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
						String.format("Error reading response from %s: %s", url==null?getUrl():url, x.getMessage()),x);
			} finally {
				try { in.close(); } catch (Exception x) {} ;
			}
			if (count == 0) 
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
							String.format("No task status indications from %s",url==null?getUrl():url));
			
			return ref;
						
		} else { //everything else considered an error
			throw new ResourceException(status);
		}
	}


}