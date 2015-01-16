package ambit2.db.substance.processor;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.p.AbstractDBProcessor;
import net.idea.modbcum.p.UpdateExecutor;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.ProtocolApplicationAnnotated;
import ambit2.base.data.study.ValueAnnotated;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.update.bundle.matrix.DeleteMatrixValue;

public class DBMatrixValueMarker extends AbstractDBProcessor<IStructureRecord, IStructureRecord> {
    protected UpdateExecutor<IQueryUpdate> writer;
    protected DeleteMatrixValue query;

    /**
     * 
     */
    private static final long serialVersionUID = 1630263508460590694L;

    public DBMatrixValueMarker(SubstanceEndpointsBundle bundle) {
	super();
	writer = new UpdateExecutor<IQueryUpdate>();
	query = new DeleteMatrixValue();
	query.setGroup(bundle);
    }

    @Override
    public void setConnection(Connection connection) throws DbAmbitException {
	super.setConnection(connection);
	writer.setConnection(connection);
    }

    @Override
    public void close() throws Exception {
	try {
	    writer.close();
	} catch (Exception x) {
	}
	super.close();
    }

    @Override
    public IStructureRecord process(IStructureRecord record) throws Exception {
	for (ProtocolApplication pa : ((SubstanceRecord) record).getMeasurements())
	    if (pa instanceof ProtocolApplicationAnnotated) {
		ProtocolApplicationAnnotated paa = (ProtocolApplicationAnnotated) pa;
		if (paa.getRecords_to_delete() == null)
		    continue;
		List<ValueAnnotated> vaa = paa.getRecords_to_delete();
		for (ValueAnnotated va : vaa) {
		    query.setObject(va);
		    try {
			writer.process(query);
		    } catch (Exception x) {
			logger.log(Level.WARNING, x.getMessage());
		    }
		}
	    }
	return record;

    }

}
