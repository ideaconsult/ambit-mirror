package ambit2.rest.structure;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.db.DbReader;
import ambit2.db.DbReaderStructure;

/**
 * {@link MediaType.TEXT_URI_LIST}
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class CompoundURIReporter<Q extends IQueryRetrieval<IStructureRecord>> extends
	QueryURIReporter<IStructureRecord, Q> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 3648376868814044783L;
    protected boolean readStructure = false;
    protected String prefix = "";

    public String getPrefix() {
	return prefix;
    }

    public void setPrefix(String prefix) {
	this.prefix = prefix;
    }

    public CompoundURIReporter(String prefix, Reference baseRef) {
	super(baseRef);
	setPrefix(prefix);
    }

    public CompoundURIReporter(Reference baseRef) {
	super(baseRef);
	setPrefix("");
    }

    public CompoundURIReporter(Request request) {
	this("", request, false);
    }

    public CompoundURIReporter(String prefix, Request request, boolean readStructure) {
	super(request);
	setPrefix(prefix);
	this.readStructure = readStructure;

    }

    public CompoundURIReporter(String prefix) {
	this(prefix, null, false);

    }

    public void open() throws DbAmbitException {

    }

    @Override
    public String getURI(String ref, IStructureRecord item) {
	if ((item.getIdstructure() == -1) || (item.getType().equals(STRUC_TYPE.NA)))
	    return String.format("%s%s%s/%d", ref, prefix, CompoundResource.compound, item.getIdchemical());
	else
	    return String.format("%s%s%s/%d%s/%d", ref, prefix, CompoundResource.compound, item.getIdchemical(),
		    ConformerResource.conformerKey, item.getIdstructure());

    }

    @Override
    protected AbstractBatchProcessor<IQueryRetrieval<IStructureRecord>, IStructureRecord> createBatch(Q query) {
	if (readStructure) {
	    DbReader<IStructureRecord> reader = new DbReaderStructure();
	    reader.setHandlePrescreen(true);
	    return reader;
	} else
	    return super.createBatch(query);
    }
}