package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.structure.QueryExactStructure;

public class QueryExactStructureTest extends QueryTest<QueryExactStructure> {

	@Override
	protected QueryExactStructure createQuery() throws Exception {
		QueryExactStructure q = new QueryExactStructure();
		q.setChemicalsOnly(true);

		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer c = p.parseSmiles("[Br-].CC[P+](c1ccccc1)(c2ccccc2)c3ccccc3"); 
		q.setValue(c);	
		return q;
	}

	@Override
	protected void verify(QueryExactStructure query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			IStructureRecord record = query.getObject(rs);

			count++;
		}
		Assert.assertEquals(1,count);
	}

}
