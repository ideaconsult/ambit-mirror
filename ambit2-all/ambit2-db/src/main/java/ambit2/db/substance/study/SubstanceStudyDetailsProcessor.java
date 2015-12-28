package ambit2.db.substance.study;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import net.idea.modbcum.i.IParameterizedQuery;
import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.MasterDetailsProcessor;

public class SubstanceStudyDetailsProcessor
		extends
		MasterDetailsProcessor<SubstanceRecord, ProtocolApplication<Protocol, String, String, IParams, String>, IQueryCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2258609350382277827L;

	public SubstanceStudyDetailsProcessor(
			IQueryRetrieval<ProtocolApplication<Protocol, String, String, IParams, String>> query) {
		super(query);
	}

	public SubstanceStudyDetailsProcessor() {
		super(new ReadSubstanceStudy());
	}

	@Override
	protected SubstanceRecord processDetail(
			SubstanceRecord target,
			ProtocolApplication<Protocol, String, String, IParams, String> measurement)
			throws Exception {
		if (measurement != null)
			target.addMeasurement(measurement);
		((ReadSubstanceStudy) query).setRecord(null);
		return target;
	}

	@Override
	protected void configureQuery(
			SubstanceRecord target,
			IParameterizedQuery<SubstanceRecord, ProtocolApplication<Protocol, String, String, IParams, String>, IQueryCondition> query)
			throws AmbitException {
		((ReadSubstanceStudy) query).setRecord(null);
		((ReadSubstanceStudy) query).setFieldname(target.getSubstanceUUID());
		if (target.getMeasurements() != null)
			target.getMeasurements().clear();

	}
}
