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
import ambit2.db.update.fp.CreateFingerprintChemical;
import ambit2.db.update.fp.CreateFingerprintStructure;

public class FingerprintChemical_crud_test extends CRUDTest<IStructureRecord,BitSet> {

	@Override
	protected IQueryUpdate<IStructureRecord, BitSet> createQuery()
			throws Exception {
		CreateFingerprintChemical q = new CreateFingerprintChemical();
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
		CreateFingerprintChemical q = new CreateFingerprintChemical();

		return q;
	}

	@Override
	protected void createVerify(IQueryUpdate<IStructureRecord, BitSet> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT * from fp1024 where idchemical=%d ",query.getGroup().getIdchemical()));
		Assert.assertEquals(1,table.getRowCount());
		

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
		CreateFingerprintStructure q = new CreateFingerprintStructure();

		return q;
	}

	@Override
	protected void deleteVerify(IQueryUpdate<IStructureRecord, BitSet> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IQueryUpdate<IStructureRecord, BitSet> updateQuery()
			throws Exception {
		CreateFingerprintChemical q = new CreateFingerprintChemical();

		return q;
	}

	@Override
	protected void updateVerify(IQueryUpdate<IStructureRecord, BitSet> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void testCreateNew() throws Exception {
		
		
	}
	@Override
	public void testDelete() throws Exception {
	
	}
	@Override
	public void testUpdate() throws Exception {
	
	}
}
