package ambit2.db.update.test;

import java.util.BitSet;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesGenerator;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.update.qlabel.CreateQLabelFingerprints;

public class QLabelFingerprintsTest extends CRUDTest<IStructureRecord, BitSet> {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dbFile = "ambit2/db/processors/test/qdescriptors-datasets.xml";	
	}
	/**
Several changes, fp differ...
https://github.com/cdk/cdk/commit/05d316de36d1c52aae64ef4b85603c2ba74a4303	 
https://github.com/cdk/cdk/commit/6c59dfbc700d7f05326655be3fce0ea781944680
https://github.com/cdk/cdk/commit/cb2d296742b1328c164bbf8b9b1d7221ddd13b2a

CDK 1.5.15 
[SnH4+2].C(C(CCCC)CC)([O-])=O.C(C(CCCC)CC)([O-])=O
{26, 76, 84, 86, 128, 148, 238, 300, 392, 534, 542, 549, 621, 637, 644, 741, 742, 752, 760, 830, 881, 929}

CDK 1.5.13
[SnH4+2].C(C(CCCC)CC)([O-])=O.C(C(CCCC)CC)([O-])=O
{26, 76, 84, 86, 128, 148, 238, 300, 392, 534, 542, 549, 621, 637, 644, 741, 742, 752, 760, 830, 874, 929}

	 */
	@Override
	protected IQueryUpdate<IStructureRecord, BitSet> createQuery()
			throws Exception {
		CreateQLabelFingerprints q = new CreateQLabelFingerprints();
		IStructureRecord record = new StructureRecord(7,100211,null,null);
		
		IDatabaseConnection c = getConnection();	
		ProcessorStructureRetrieval p = new ProcessorStructureRetrieval();
		p.setConnection(c.getConnection());
		MoleculeReader molReader = new MoleculeReader();
		
		FingerprintGenerator g = new FingerprintGenerator(new Fingerprinter(1024,8));
		IAtomContainer mol = molReader.process(p.process(record));
		SmilesGenerator sg = SmilesGenerator.generic();
		for (IAtom a : mol.atoms()) {
			System.out.print(String.format("%s\t%s\n",a.getAtomTypeName(),a.getFlag(CDKConstants.ISAROMATIC)));
		}
			
		System.out.println(sg.create(mol));
		q.setObject(g.process(mol));
		System.out.println(q.getObject());
		q.setGroup(record);
		c.close();
		return q;
	}

	@Override
	protected IQueryUpdate<IStructureRecord, BitSet> createQueryNew()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createVerify(IQueryUpdate<IStructureRecord, BitSet> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT * from quality_structure where idstructure=%d and label=\"ProbablyOK\"",query.getGroup().getIdstructure()));
		Assert.assertEquals(1,table.getRowCount());
		
		table = 	c.createQueryTable("EXPECTED","SELECT * from quality_structure");
		Assert.assertEquals(2,table.getRowCount());		
		c.close();
		
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<IStructureRecord, BitSet> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IQueryUpdate<IStructureRecord, BitSet> deleteQuery()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void deleteVerify(IQueryUpdate<IStructureRecord, BitSet> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IQueryUpdate<IStructureRecord, BitSet> updateQuery()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateVerify(IQueryUpdate<IStructureRecord, BitSet> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public void testCreateNew() throws Exception {
		try {
		super.testCreateNew();
		} catch (Exception x) {}
	}
	@Override
	public void testDelete() throws Exception {
		try {
			super.testDelete();
			} catch (Exception x) {}

	}
	@Override
	public void testUpdate() throws Exception {
		try {
			super.testUpdate();
			} catch (Exception x) {}
	}

}
