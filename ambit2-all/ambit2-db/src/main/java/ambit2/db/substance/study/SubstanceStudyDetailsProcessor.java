package ambit2.db.substance.study;

import java.util.List;

import net.idea.modbcum.i.IParameterizedQuery;
import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.MasterDetailsProcessor;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.ProtocolApplication;

public class SubstanceStudyDetailsProcessor
		extends
		MasterDetailsProcessor<SubstanceRecord, List<ProtocolApplication>, IQueryCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2258609350382277827L;

	public SubstanceStudyDetailsProcessor(
			IQueryRetrieval<List<ProtocolApplication>> query) {
		super(query);
	}

	public SubstanceStudyDetailsProcessor() {
		super(new ReadSubstanceStudyFlat());
	}

	@Override
	protected SubstanceRecord processDetail(SubstanceRecord target,
			List<ProtocolApplication> measurements) throws Exception {
		target.setMeasurements(measurements);
		return target;
	}

	@Override
	protected void configureQuery(
			SubstanceRecord target,
			IParameterizedQuery<SubstanceRecord, List<ProtocolApplication>, IQueryCondition> query)
			throws AmbitException {
		((ReadSubstanceStudyFlat) query).setRecord(target.getMeasurements());
		((ReadSubstanceStudyFlat) query).setFieldname(target);
		((ReadSubstanceStudyFlat) query).setPageSize(1000000);
	}
}
