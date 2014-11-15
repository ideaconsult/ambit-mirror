package ambit2.rest.reference;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;

import ambit2.base.data.ILiteratureEntry;

/**
 * Generates URI for {@link ReferenceResource}
 * @author nina
 *
 * @param <Q>
 */
public class ReferenceURIReporter <Q extends IQueryRetrieval<ILiteratureEntry>> extends QueryURIReporter<ILiteratureEntry, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868430033131766579L;
	public ReferenceURIReporter(Request baseRef) {
		super(baseRef,null);
	}
	public ReferenceURIReporter() {
		this(null);
	}	

	@Override
	public String getURI(String ref, ILiteratureEntry item) {
		return String.format("%s%s/%d",ref,ReferenceResource.reference,item.getId());
	}

}
