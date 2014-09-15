package ambit2.db.processors.quality;

import java.sql.SQLException;
import java.util.BitSet;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.processors.AbstractUpdateProcessor;
import ambit2.db.update.fp.CreateFingerprintStructure;

public class FPStructureWriter extends AbstractUpdateProcessor<IStructureRecord, BitSet> {
	protected MoleculeReader molReader = new MoleculeReader();
	protected FingerprintGenerator g = new FingerprintGenerator(new Fingerprinter());
	/**
	 * 
	 */
	private static final long serialVersionUID = -5985642442961325777L;
	
	public FPStructureWriter() {
		setQueryCreate(new CreateFingerprintStructure());
	}
	@Override
	public BitSet create(IStructureRecord record) throws SQLException,
			OperationNotSupportedException, AmbitException {
		getQueryCreate().setGroup(record);		
		try {
			IAtomContainer a = molReader.process(record);
			if ((a==null) || (a.getAtomCount()==0)) {
				getQueryCreate().setObject(null);
			} else {
				getQueryCreate().setObject(g.process(a));
			}
			
		} catch (Exception x) {
			getQueryCreate().setObject(null);
		}
		return super.create(record);
	}
}
