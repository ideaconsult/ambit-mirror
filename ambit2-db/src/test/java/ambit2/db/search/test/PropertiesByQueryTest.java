package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.property.PropertiesByQuery;

public class PropertiesByQueryTest extends QueryTest<PropertiesByQuery> {

	@Override
	protected PropertiesByQuery createQuery() throws Exception {
		PropertiesByQuery q = new PropertiesByQuery();
		IStoredQuery v = new StoredQuery();
		v.setId(2);
		q.setValue(v);
		return q;
	}

	@Override
	protected void verify(PropertiesByQuery query, ResultSet rs)
			throws Exception {
		int records = 0;
		while (rs.next()) {
			records ++;
			//Property d = query.getObject(rs);
			//Assert.assertEquals("Endpoints",d.getParentTemplate());
			//Assert.assertEquals("Physicochemical effects",d.getTemplate());			
		}
		Assert.assertEquals(0,records);		
	}

}
