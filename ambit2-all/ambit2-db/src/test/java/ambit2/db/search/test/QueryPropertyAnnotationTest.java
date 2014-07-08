package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.db.update.propertyannotations.ReadPropertyAnnotations;

public class QueryPropertyAnnotationTest extends QueryTest<ReadPropertyAnnotations> {

	@Override
	protected ReadPropertyAnnotations createQuery() throws Exception {
		ReadPropertyAnnotations q = new ReadPropertyAnnotations();
		Property p = Property.getNameInstance();//whatever
		p.setId(1);
		q.setFieldname(p);
		return q;
	}

	@Override
	protected void verify(ReadPropertyAnnotations query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			records ++;
			PropertyAnnotation stat = query.getObject(rs);
			Assert.assertEquals("Feature",stat.getType());
			Assert.assertEquals("test1",stat.getPredicate());
			Assert.assertEquals("test2",stat.getObject());
	
		}
		//Assert.assertEquals(2,records);
	}

}
