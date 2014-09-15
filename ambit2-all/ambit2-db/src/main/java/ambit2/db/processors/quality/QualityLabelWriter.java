package ambit2.db.processors.quality;

import java.sql.SQLException;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.core.processors.structure.key.SmilesKey;
import ambit2.db.processors.AbstractRepositoryWriter;
import ambit2.db.update.qlabel.CreateQLabelFingerprints;
import ambit2.db.update.qlabel.SmilesUniquenessCheck;

public class QualityLabelWriter extends AbstractRepositoryWriter<IStructureRecord, IStructureRecord> {
	protected CreateQLabelFingerprints query = new CreateQLabelFingerprints();
	protected SmilesUniquenessCheck querySmiles = new SmilesUniquenessCheck();
	protected MoleculeReader molReader = new MoleculeReader();
	protected FingerprintGenerator g = new FingerprintGenerator(new Fingerprinter());
	protected SmilesKey smilesKey = new SmilesKey();
	/**
	 * 
	 */
	private static final long serialVersionUID = -7068666045941631192L;
	@Override
	public IStructureRecord create(IStructureRecord record) throws SQLException,
			OperationNotSupportedException, AmbitException {
		IAtomContainer a = molReader.process(record);
		if ((a==null) || (a.getAtomCount()==0)) {
			query.setObject(null);
			query.setGroup(record);
			exec.process(query);
			
			querySmiles.setObject(null);
			querySmiles.setGroup(record);
			exec.process(querySmiles);
		} else {
			query.setObject(g.process(a));
			query.setGroup(record);
			exec.process(query);
			
			querySmiles.setObject(smilesKey.process(a));
			querySmiles.setGroup(record);
			exec.process(querySmiles);
		}
		return record;
	}
}
