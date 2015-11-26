package ambit2.db.search.substance.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;
import ambit2.db.search.test.QueryTest;
import ambit2.db.substance.ids.ReadChemIdentifiersByComposition;

public class ReadChemIdentifiersByCompositionTest extends QueryTest<ReadChemIdentifiersByComposition> {

	@Override
	protected ReadChemIdentifiersByComposition createQuery() throws Exception {
		ReadChemIdentifiersByComposition q = new ReadChemIdentifiersByComposition();
		SubstanceRecord r = new SubstanceRecord(1);
		CompositionRelation cr = new CompositionRelation(r, new StructureRecord(11,100215,null,null), new Proportion());
		r.addStructureRelation(cr);
		q.setFieldname(r);
		return q;
	}

	@Override
	protected void verify(ReadChemIdentifiersByComposition query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			IStructureRecord record = query.getObject(rs);
			Assert.assertEquals(11,record.getIdchemical());
			for (Property p : record.getRecordProperties()) {
				if (p.getLabel().equals("http://www.opentox.org/api/1.1#CASRN"))
					Assert.assertEquals("123-45-6",record.getRecordProperty(p));
				else if (p.getLabel().equals("http://www.opentox.org/api/1.1#ChemicalName"))
					Assert.assertEquals("Chemical name example",record.getRecordProperty(p));
			}
			count++;
		}
		Assert.assertEquals(2,count);
		
	}

}