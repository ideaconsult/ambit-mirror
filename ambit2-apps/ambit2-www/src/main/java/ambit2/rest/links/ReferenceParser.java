package ambit2.rest.links;

import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.restlet.Application;
import org.restlet.data.Reference;

/**
 * Abstract class to parse into domain object, given a reference
 * @author nina
 *
 * @param <T>
 */
public abstract class ReferenceParser<T> extends DefaultAmbitProcessor<Reference, T>{
	protected Application application;
	public Application getApplication() {
		return application;
	}
	public void setApplication(Application application) {
		this.application = application;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2867425886814531544L;


}
