package ambit2.fastox.steps;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.fastox.users.IToxPredictSession;

public class StepProcessor extends DefaultAmbitProcessor<Representation, Form> {

	protected Reference baseReference = null;
	public Reference getBaseReference() {
		return baseReference;
	}

	public void setBaseReference(Reference baseReference) {
		this.baseReference = baseReference;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1318642062244171641L;
	
	public Form process(Representation entity) throws AmbitException {
		return process(entity,null);
	}
	
	public Form process(Representation entity, IToxPredictSession session) throws AmbitException {
		return new Form(entity);
	}
	public static String readUriList(InputStream in) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) { return line; }
			
		} catch (Exception x) {
			
		} finally {
			try {in.close(); } catch (Exception x) {}
		}
		return null;
	}
	
	public Representation post(Reference uri, Representation input) throws Exception {
		Representation r = null;
		Reference ref = null;
		try {
			ClientResource resource = new ClientResource(uri);
			r = resource.post(input);
			try { r.release(); } catch (Exception x) {}
			
			ref = resource.getResponse().getLocationRef();
			Status status = resource.getStatus();
			while (status.equals(Status.REDIRECTION_SEE_OTHER) || status.equals(Status.SUCCESS_ACCEPTED)) {
				//System.out.println(status);
				//System.out.println(ref);
				resource.setReference(ref);
				Response response = resource.getResponse();

				status = response.getStatus();
				if (Status.REDIRECTION_SEE_OTHER.equals(status)) {
					ref = response.getLocationRef();
				} 
				try { response.release(); } catch (Exception x) {}

			}
			return r;
		} catch (Exception x) {
			try { r.release(); } catch (Exception xx) {}
			throw x;
		} finally {
			
			
		}
	}
}
