package ambit2.rest.legacy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;

import net.idea.restnet.c.task.ClientResourceWrapper;
import net.idea.restnet.rdf.ns.OT;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.rest.rdf.BibTex;
import ambit2.rest.rdf.OTA;
import ambit2.rest.rdf.OTEE;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * Convenience class to launch and poll remote POST jobs
 * @author nina
 *
 */
@Deprecated
public class OTRemoteTask implements Serializable {
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
	public OTRemoteTask(Reference url,MediaType media, 
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

		HttpURLConnection client = null;
		InputStream in = null;
		
		try {
			client = ClientResourceWrapper.getHttpURLConnection(result.toString(),"GET",MediaType.TEXT_URI_LIST.toString());
			client.setFollowRedirects(true);

			status = new Status(client.getResponseCode());
			if (Status.SERVER_ERROR_SERVICE_UNAVAILABLE.equals(status)) {
				return true;
			}
	        in = client.getInputStream();
			result = handleOutput(in,status,result);
		} catch (IOException x) {
			setError(x);

		} catch (ResourceException x) {
			setError(x);
			status = x.getStatus();
		} catch (Exception x) {
			setError(x);
			status = null;
		} finally {
			try {in.close();} catch (Exception x) {}
			try {
				client.disconnect(); client = null;
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
				if (Status.SUCCESS_ACCEPTED.equals(status) && (url != null)) return url;
				String msg = String.format("Error reading response from %s: %s. Status was %s", url==null?getUrl():url, "Empty content",status);
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,msg);
			}
			
			int count=0;
			try {

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = reader.readLine())!=null) {
					if ("".equals(line.trim())) 
						ref = null;
					else {
						ref = new Reference(line.trim());
						count++;
					}
				}
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
						String.format("Error reading response from %s: %s", url==null?getUrl():url, x.getMessage()),x);
			} finally {
				try { in.close(); } catch (Exception x) {} ;
			}
			if (count == 0) {
				if (getStatus().equals(Status.SUCCESS_OK)) return null;
				else return url==null?getUrl():url;
			}
			/* A hack for the validation service returning empty responses on 200 OK ...
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
							String.format("No task status indications from %s",url==null?getUrl():url));
			*/
			return ref;
						
		} else { //everything else considered an error
			throw new ResourceException(status);
		}
	}

	public boolean pollRDF() {
		if (isDone()) return true;

		HttpURLConnection uc = null;
		InputStream in = null;
		
		try {
			uc = ClientResourceWrapper.getHttpURLConnection(result.toString(),"GET", MediaType.APPLICATION_RDF_XML.toString(),getClass().getName());	
			status = new Status(uc.getResponseCode());
//			if (!r.getEntity().isAvailable()) throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,String.format("Representation not available %s",result));
			in = uc.getInputStream();
			result = handleOutputRDF(in,status);
		} catch (IOException x) {
			
			error = new ResourceException(status);
			
		} catch (ResourceException x) {
			error = x;
			status = x.getStatus();
		} catch (Exception x) {
			error = x;
			status = null;
		} finally {
			try {in.close();} catch (Exception x) {}
			try {uc.disconnect();} catch (Exception x) {}


		}
		return isDone();
	}	
	protected Reference handleOutputRDF(InputStream in,Status status) throws ResourceException {
		Reference ref = result;
		
		if (Status.SUCCESS_OK.equals(status) || Status.SUCCESS_ACCEPTED.equals(status) || Status.SUCCESS_CREATED.equals(status) || Status.REDIRECTION_SEE_OTHER.equals(status)) {
			Model jenaModel = null;
			try {
				jenaModel = ModelFactory
								.createDefaultModel();

						jenaModel.setNsPrefix("ot", OT.NS);
						jenaModel.setNsPrefix("ota", OTA.NS);
						jenaModel.setNsPrefix("otee", OTEE.NS);
						jenaModel.setNsPrefix("owl", OWL.NS);
						jenaModel.setNsPrefix("dc", DC.NS);
						jenaModel.setNsPrefix("bx", BibTex.NS);
						jenaModel.setNsPrefix("xsd", XSDDatatype.XSD + "#");
						
				jenaModel = OT.createModel(jenaModel, in,MediaType.APPLICATION_RDF_XML);
				Resource theTask  = jenaModel.createResource(result.toString());
				//Property hasStatus = jenaModel.createProperty("http://www.opentox.org/api/1.1#hasStatus");

				Property resultURI = jenaModel.createProperty("http://www.opentox.org/api/1.1#resultURI");
				StmtIterator i  =  jenaModel.listStatements(new SimpleSelector(theTask,resultURI,(RDFNode) null));
				Statement st = null;
				while (i.hasNext()) {
					st = i.next();
					if (st.getObject().isLiteral()) {
						ref = new Reference(((Literal)st.getObject()).getString());
					} else if (st.getObject().isURIResource()) {
						ref = new Reference(((Resource)st.getObject()).getURI());
					} else ref = new Reference(st.getObject().toString());
					
				}	
				i.close();
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x.getMessage(),x);
			} finally {
				try { jenaModel.close(); } catch (Exception x) {} ;
				try { in.close(); } catch (Exception x) {} ;
			}
			//if (count == 0) 
				//throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,"No status indications!");
			
			return ref;
						
		} else { //everything else considered an error
			throw new ResourceException(status);
		}
	}	
}