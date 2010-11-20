package ambit2.rest.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
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

	
	public Exception getError() {
		return error;
	}
	public RemoteTask(Reference url,MediaType media, 
			  Representation input,
			  Method method,
			  ChallengeResponse authentication) throws ResourceException {
		super();
		this.url = url;
		Representation r=null;
		ClientResource client = null;
		try {
			client = new ClientResource(url);
			client.setChallengeResponse(authentication);
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
			
			if (!r.isAvailable()) throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,String.format("Representation not available %s",url));
			
			result = handleOutput(r.getStream(),status);
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
			error = x;
			status = null;
		} finally {
			try { if (r != null) r.release(); } catch (Exception x) { x.printStackTrace();}
			try { client.release(); } catch (Exception x) { x.printStackTrace();}
		}
	}	
	/*
	public RemoteTask(Reference url,MediaType media, Representation input,Method method,ChallengeResponse authentication) {
		super();
		
		this.url = url;
		Representation r=null;
		try {
			System.out.println(url);
			System.out.println(input.getText());
			ClientResource client = new ClientResource(url);
			client.setChallengeResponse(authentication);
			r = client.post(input,media);
			result = handleOutput(client, r);
			this.status = client.getStatus();
		} catch (ResourceException x) {
			error = x;
			status = x.getStatus();
		} catch (Exception x) {
			error = x;
			status = null;
		} finally {
			try { r.release(); } catch (Exception x) {}
			System.out.println(toString());
		}
	}		
	*/
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

		ClientResource client = null;
		Representation r = null;
		
		try {
			
			client = new ClientResource(result.toString());
			client.setRetryOnError(false);
			client.setRetryAttempts(1);
			client.setFollowingRedirects(true);
	        r = client.get(MediaType.TEXT_URI_LIST);
			status = client.getStatus();
			System.out.println(status);
			if (Status.SERVER_ERROR_SERVICE_UNAVAILABLE.equals(status)) {
				return true;
			}
			
//			if (!r.getEntity().isAvailable()) throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,String.format("Representation not available %s",result));
			
			result = handleOutput(r.getStream(),status);
//			System.out.println(String.format("poll %s %s",status,result));
		} catch (ResourceException x) {
			error = x;
			status = x.getStatus();
		} catch (Exception x) {
			error = x;
			status = null;
		} finally {
			try {r.release();} catch (Exception x) {}
			try {
				client.release(); client = null;
			} catch (Exception x) {}

		}
		return isDone();
	}
	/*
public boolean poll() {
		if (isDone()) return true;

		InputStream in = null;
		URLConnection c = null;
		try {
			c = new URL(result.toString()).openConnection();
	        HttpURLConnection hc = ((HttpURLConnection)c);
	        hc.setRequestMethod("GET");
	        hc.setUseCaches(false);
	        hc.setFollowRedirects(false);
	        hc.setRequestProperty("Accept",MediaType.TEXT_URI_LIST.toString());
	       // hc.setRequestProperty("Accept",MediaType.TEXT_HTML.toString());
			in = hc.getInputStream();
			status = new Status(hc.getResponseCode());
//			if (!r.getEntity().isAvailable()) throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,String.format("Representation not available %s",result));
			result = handleOutput(in,status);
			
		} catch (ResourceException x) {
			error = x;
			status = x.getStatus();
		} catch (Exception x) {
			error = x;
			status = null;
		} finally {
			try {in.close();} catch (Exception x) {}


		}
		return isDone();
	}
	 */
	protected Reference handleOutput(InputStream in,Status status) throws ResourceException {
		Reference ref = null;
		System.out.println("handleOutput "+status );
		if (Status.SUCCESS_OK.equals(status) 
						|| Status.SUCCESS_ACCEPTED.equals(status) 
						|| Status.SUCCESS_CREATED.equals(status) 
						//|| Status.REDIRECTION_SEE_OTHER.equals(status)
						|| Status.SERVER_ERROR_SERVICE_UNAVAILABLE.equals(status)
						) {
			int count=0;
			try {
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = reader.readLine())!=null) {
					ref = new Reference(line.trim());
					System.out.println(ref);
					count++;
				}
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x.getMessage(),x);
			} finally {
				try { in.close(); } catch (Exception x) {} ;
			}
			if (count == 0) 
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,"No status indications!");
			
			return ref;
						
		} else { //everything else considered an error
			throw new ResourceException(status);
		}
	}	

}