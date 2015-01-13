package ambit2.db.substance.processor;

import java.sql.Connection;
import java.sql.ResultSet;

import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.QueryExecutor;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.ReadSubstance;
import ambit2.db.substance.study.UpdateEffectRecords;
import ambit2.db.substance.study.UpdateEffectRecordsBundle;
import ambit2.db.substance.study.UpdateSubstanceStudy;
import ambit2.db.substance.study.UpdateSubstanceStudyBundle;

public class DBBundleStudyWriter extends DBSubstanceWriter {
	protected ReadSubstance rq;
	protected QueryExecutor qx;
	protected SubstanceEndpointsBundle bundle;
	public SubstanceEndpointsBundle getBundle() {
		return bundle;
	}
	public void setBundle(SubstanceEndpointsBundle bundle) {
		this.bundle = bundle;
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7474604799923342029L;

	public DBBundleStudyWriter(SubstanceEndpointsBundle bundle,SourceDataset dataset, SubstanceRecord importedRecord) {
		super(dataset, importedRecord, false, false);
		setBundle(bundle);
		rq = new ReadSubstance();
		qx = new QueryExecutor();
		qx.setCache(true);
		qx.setCloseConnection(false);
	}

	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		qx.setConnection(connection);
		super.setConnection(connection);
	}
	@Override
	public void close() throws Exception {
		try {qx.close();} catch (Exception x) {}
		super.close();
	}
	@Override
	protected UpdateSubstanceStudy createSubstanceStudyUpdateQuery(ProtocolApplication papp) throws Exception  {
		return new UpdateSubstanceStudyBundle(bundle,importedRecord.getCompanyUUID(), papp);
	}
	@Override
	protected UpdateEffectRecords createEffectRecordUpdateQuery(ProtocolApplication papp, EffectRecord effect) throws Exception  {
		return new UpdateEffectRecordsBundle(bundle,importedRecord.getCompanyUUID(),papp,effect);
	}
	@Override
	protected void importSubstanceRecord(SubstanceRecord substance) throws Exception {
		SubstanceRecord q = new SubstanceRecord(substance.getCompanyUUID());
		SubstanceRecord found = null;
		rq.setValue(q);
		ResultSet rs = null;
		try {
			rs = qx.process(rq);
			while (rs.next()) {
				found = rq.getObject(rs);
			}
		} catch (Exception x) {
			x.printStackTrace();
			if (rs != null) rs.close();
		}
		if (found==null) {
	     	throw new Exception("Not found");
		} else {
			substance.setIdsubstance(found.getIdsubstance());
			importedRecord.setCompanyUUID(found.getCompanyUUID());
			importedRecord.setIdsubstance(found.getIdsubstance());
		}
	}
}
