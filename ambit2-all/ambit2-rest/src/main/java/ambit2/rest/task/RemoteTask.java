package ambit2.rest.task;

import java.io.BufferedReader;
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
	
	public RemoteTask(Reference url,MediaType media, Representation input,Method method,ChallengeResponse authentication) {
		super();
		
		this.url = url;
		Representation r=null;
		try {
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
	
	public boolean isCompletedOK() {
		return Status.SUCCESS_OK.equals(status);
	}
	public boolean isAccepted() {
		return Status.SUCCESS_ACCEPTED.equals(status);
	}	
	public boolean isRedirected() {
		return Status.REDIRECTION_SEE_OTHER.equals(status);
	}	
	public boolean isERROR() {
		return error != null;
	}		
	public boolean isDone() {
		return isCompletedOK() || isERROR();
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
	public boolean poll() {
		if (isDone()) return true;
		ClientResource client = new ClientResource(result);
		client.setFollowingRedirects(false);
		Representation r = null;
		try {
			r = client.get();
			result = handleOutput(client, r);
			status = client.getStatus();
		} catch (ResourceException x) {
			error = x;
			status = x.getStatus();
		} catch (Exception x) {
			error = x;
			status = null;
		} finally {
			try {r.release();} catch (Exception x) {}
		}
		return isDone();
	}
	protected Reference handleOutput(ClientResource client, Representation r) throws ResourceException {
		Reference ref = client.getReference();
		if (Status.SUCCESS_OK.equals(client.getStatus())) {
			return ref;
		} else if (Status.REDIRECTION_SEE_OTHER.equals(client.getStatus()) || Status.SUCCESS_ACCEPTED.equals(client.getStatus())) {
			int count=0;;
			if (client.getLocationRef()!= null) { count++; ref = client.getLocationRef(); }
			else {
				ref = null;
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(r.getStream()));
					String line = null;
					while ((line = reader.readLine())!=null) {
						ref = new Reference(line);
						count++;
					}
				} catch (Exception x) {
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
				} 
			}
			if (count == 0) 
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,"No status indications!");
			
			return ref;
						
		} else { //everything else considered an error
			throw new ResourceException(client.getStatus());
		}
	}	

}