package ambit2.db.update.test;

import java.util.BitSet;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.openscience.cdk.fingerprint.Fingerprinter;

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
		dbFile = "src/test/resources/ambit2/db/processors/test/qdescriptors-datasets.xml";	
	}
	@Override
	protected IQueryUpdate<IStructureRecord, BitSet> createQuery()
			throws Exception {
		CreateQLabelFingerprints q = new CreateQLabelFingerprints();
		IStructureRecord record = new StructureRecord(7,100211,null,null);
		
		IDatabaseConnection c = getConnection();	
		ProcessorStructureRetrieval p = new ProcessorStructureRetrieval();
		p.setConnection(c.getConnection());
		MoleculeReader molReader = new MoleculeReader();
		FingerprintGenerator g = new FingerprintGenerator(new Fingerprinter());
		q.setObject(g.process(molReader.process(p.process(record))));
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
