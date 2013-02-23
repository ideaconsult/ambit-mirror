package ambit2.i5rest.convertors;

import java.io.IOException;
import java.io.Writer;
import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.PropertiesUtil;
import org.ideaconsult.iuclidws.session.SessionEngine;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.types.Types.Session;
import org.restlet.Request;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.Reporter;
import ambit2.i5rest.resource.SubstanceResource;

public abstract class I5Reporter<Content> implements Reporter<Content, Writer> {
	/**
	 * 
	 */
	private String licenseURI = "www.iuclid5.eu";
	private static final long serialVersionUID = -1309296455188885375L;
	protected final static Logger LOGGER = Logger.getLogger(I5Reporter.class);
	protected String target = "http://ambit.uni-plovdiv.bg:8081/i5wsruntime/services";
	//protected String target = "http://192.168.1.11:8080/i5wsruntime/services";
	protected  Request request;
	
	public I5Reporter( Request request,Session session) {
		super();
		this.request = request;
		setTarget(SubstanceResource.getServiceURI(request));
		setUser(SubstanceResource.getUser(request));
		setPass(SubstanceResource.getPass(request));
	}	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		if (target==null) target = "http://192.168.1.11:8080/i5wsruntime/services";
		else this.target = target;
	}
	protected String user = "webservice";
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		if (user==null) user = "webservice";
		else this.user = user;
	}
	protected String pass = "webservice";
	
	
	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		if (pass==null) pass = "webservice";
		else this.pass = pass;
	}
	protected Writer output;
	protected Session session;
	protected boolean existingSession = false;
	
	public I5Reporter(Session session) {
		super();
		setSession(session);
	}
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
		existingSession = session != null;
	}
	
	public void close() throws Exception {
	}

	public Writer getOutput() throws AmbitException {
		return output;
	}

	public long getTimeout() {
		return 0;
	}

	public void setOutput(Writer output) throws AmbitException {
		this.output = output;
	}

	public void setTimeout(long timeout) {
	}

	public long getID() {
		return 0;
	}

	public boolean isEnabled() {
		return true;
	}

	public void setEnabled(boolean value) {
		
	}

	
	public void login() throws SessionEngineFault,SessionEngineNotAvailableFault,AxisFault,RemoteException {
		if (session == null) {
			LOGGER.info("execute login for " + PropertiesUtil.getUsername());
			final Session session = SessionEngine.login(user,pass,getTarget());
			LOGGER.info("received session with id " + session.getId());
			setSession(session);
		} else {
			LOGGER.info("existing session with id " + session.getId());
		}

	}
	public void header() throws IOException {
		output.write("<html><head><title>AMBIT-IUCLID5 interaction demo</title></head><body>");
		
	}
	public void footer() throws IOException {
		output.write("</body></html>");
	}	
	public void logout() throws SessionEngineNotAvailableFault
												,RemoteException,
												SessionEngineNotAvailableFault,
												SessionEngineFault,
												ClientServiceException{
		LOGGER.info("execute logout for " + session.getId());
		if (!existingSession) {
			SessionEngine.logout(session,getTarget());
			setSession(null);
		}
	}		

	@Override
	public void setLicenseURI(String uri) {
		this.licenseURI = uri;
	}
	@Override
	public String getLicenseURI() {
		return licenseURI;
	}
	@Override
	public String getFileExtension() {
		return ".i5";
	}
}
