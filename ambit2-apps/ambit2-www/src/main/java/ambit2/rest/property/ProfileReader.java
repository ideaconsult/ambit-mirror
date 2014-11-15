package ambit2.rest.property;

import java.sql.Connection;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.BatchProcessingException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.modbcum.p.AbstractDBProcessor;

import org.restlet.Context;
import org.restlet.data.Cookie;
import org.restlet.data.Reference;
import org.restlet.util.Series;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.db.reporters.QueryTemplateReporter;
import ambit2.db.search.property.AbstractPropertyRetrieval;
import ambit2.rest.task.CallableQueryProcessor;

public class ProfileReader extends AbstractDBProcessor<Reference, Template> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1956891586130018936L;
	protected Template profile;
	protected Reference applicationReference;
	protected QueryTemplateReporter<IQueryRetrieval<Property>> reporter;
	protected Context context;
	protected StringBuilder cookies;
	protected String agent;
	
	public ProfileReader(Reference applicationReference, Template profile,Context context,
				String token,
				Series<Cookie> cookies,
				String agent) throws Exception {
		super();
		setApplicationReference(applicationReference);
		setProfile(profile==null?new Template():profile);
		reporter = new QueryTemplateReporter<IQueryRetrieval<Property>>(getProfile());
		reporter.setCloseConnection(false);
		this.context = context;
		if (cookies!=null) for (Cookie cookie : cookies) {
			if (this.cookies==null) this.cookies=new StringBuilder();
			else this.cookies.append(";");
			this.cookies.append(cookie.getName());
			this.cookies.append("=");
			this.cookies.append(cookie.getValue());
		}
		this.agent=agent;
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		reporter.setConnection(connection);
	}
	@Override
	public void close() throws Exception {
		reporter.close();
		super.close();
	}
	public Reference getApplicationReference() {
		return applicationReference;
	}

	public void setApplicationReference(Reference applicationReference) {
		this.applicationReference = applicationReference;
	}

	public Template getProfile() {
		return profile;
	}

	public void setProfile(Template profile) {
		this.profile = profile;
	}
/*
 * (non-Javadoc)
 * @see ambit2.base.interfaces.IProcessor#process(java.lang.Object)
 */
	/*
ambit2.base.exceptions.AmbitException: Communication Error (1001) - sun.security.validator.ValidatorException: PKIX path building failed: sun.
security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
        at ambit2.rest.property.ProfileReader.process(ProfileReader.java:82)
        at ambit2.rest.query.StructureQueryResource.createTemplate(StructureQueryResource.java:147)
        at ambit2.rest.query.StructureQueryResource.createTemplate(StructureQueryResource.java:119)
        at ambit2.rest.query.StructureQueryResource.createTemplate(StructureQueryResource.java:115)
        at ambit2.rest.dataset.DatasetStructuresResource.createQuery(DatasetStructuresResource.java:39)
        at ambit2.rest.dataset.DatasetResource.createQuery(DatasetResource.java:188)
        at ambit2.rest.dataset.DatasetResource.createQuery(DatasetResource.java:55)
        at
	 */
	public Template process(Reference uri) throws AmbitException {
		if (profile == null) setProfile(new Template());
		if (uri==null) return profile;
		Object q;
		try {
			q = CallableQueryProcessor.getQueryObject(uri, 
					applicationReference,context,cookies==null?null:cookies.toString(),agent);
			if ((q!=null) && (q instanceof AbstractPropertyRetrieval)) {
				
				try {
					reporter.setConnection(getConnection());
					reporter.process((AbstractPropertyRetrieval)q);
				} catch (NotFoundException x) {
					//this is ok
				} catch (BatchProcessingException x) {
					if (x.getCause() instanceof NotFoundException) { //ok
					} else
						logger.log(Level.WARNING,x.getMessage(),x);
				} catch(Exception x) {
					logger.log(Level.WARNING,x.getMessage(),x);
				} finally {
					//the reporter closes the connection as well
					try {
						//reporter.setCloseConnection(true); //the caller should decide!
						reporter.close();
					} 
					catch (Exception x) {}
				}
			}
			return profile;
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			q = null;
		}
	}

	@Override
	public void setCloseConnection(boolean closeConnection) {
		super.setCloseConnection(closeConnection);
		reporter.setCloseConnection(closeConnection);
	}
	public void open() throws DbAmbitException {
		
	}

}
