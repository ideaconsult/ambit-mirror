package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;

import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.QueryDistance;

public class QueryDistanceTest extends QueryTest<QueryDistance> {

	public void test() throws Exception {
		Assert.assertEquals("select ? as idquery,-1,idstructure,1 as selected,1 as metric from atom_structure join atom_distance using(iddistance) where atom1 = ? and atom2 = ? and distance < ?",
				query.getSQL());
	}
	
	@Override
	protected QueryDistance createQuery() throws Exception {
		QueryDistance q = new QueryDistance();
		q.setFieldname(new Bond(new Atom("C"),new Atom("N")));
		q.setValue(10.0);
		q.setCondition(NumberCondition.getInstance("<"));
		return q;
	}

	@Override
	protected void verify(QueryDistance query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			count++;
		}
		Assert.assertTrue(count >0);
	}
	@Override
	public void testSelect() throws Exception {
	}	

}
