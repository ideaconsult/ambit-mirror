package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.db.search.structure.QueryStrucType;

public class QueryStrucTypeTest extends QueryTest<QueryStrucType>{

	@Override
	protected QueryStrucType createQuery() throws Exception {
		return new QueryStrucType(STRUC_TYPE.D2noH);
	}

	@Override
	protected void verify(QueryStrucType query, ResultSet rs) throws Exception {

		int records = 0;
		while (rs.next()) {
			records ++;
			Assert.assertEquals(query.getId().intValue(),rs.getInt(1));
			Assert.assertEquals(10,rs.getInt(2));
			Assert.assertEquals(100214,rs.getInt(3));
		}
		Assert.assertEquals(1,records);		
	}

}
