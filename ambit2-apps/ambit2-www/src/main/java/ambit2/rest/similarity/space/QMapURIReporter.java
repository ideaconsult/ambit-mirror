package ambit2.rest.similarity.space;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.db.simiparity.space.QMap;

public class QMapURIReporter extends QueryURIReporter<QMap, IQueryRetrieval<QMap>> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -5366595019468858610L;

    public QMapURIReporter(Reference baseRef) {
	super(baseRef);
    }

    public QMapURIReporter(Request ref) {
	super(ref);
    }

    public QMapURIReporter() {
	this((Request) null);
    }

    @Override
    public String getURI(String ref, QMap record) {
	if (record.getId() > 0)
	    return String.format("%s%s/%d", ref, QMapResource.qmap, record.getId());
	else
	    return String.format("%s%s", ref, QMapResource.qmap);

    }

}