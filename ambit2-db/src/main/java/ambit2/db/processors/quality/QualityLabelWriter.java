package ambit2.db.processors.quality;

import java.sql.SQLException;

import javax.naming.OperationNotSupportedException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.processors.AbstractRepositoryWriter;
import ambit2.db.update.qlabel.CreateQLabelFingerprints;

public class QualityLabelWriter extends AbstractRepositoryWriter<IStructureRecord, IStructureRecord> {
	protected CreateQLabelFingerprints query = new CreateQLabelFingerprints();
	protected MoleculeReader molReader = new MoleculeReader();
	protected FingerprintGenerator g = new FingerprintGenerator();
	/**
	 * 
	 */
	private static final long serialVersionUID = -7068666045941631192L;
	@Override
	public IStructureRecord create(IStructureRecord record) throws SQLException,
			OperationNotSupportedException, AmbitException {
		query.setObject(g.process(molReader.process(record)));
		query.setGroup(record);
		exec.process(query);
		return record;
	}
}
