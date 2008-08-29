package ambit2.db.search.test;

import java.sql.ResultSet;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;

import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryDistance;

public class QueryDistanceTest extends QueryTest<QueryDistance> {

	public void test() throws Exception {
		System.out.println(query.getValue());
		assertEquals("select ? as idquery,-1,idstructure,1 as selected,1 as metric from atom_structure join atom_distance using(iddistance) where atom1 = ? and atom2 = ? and distance < ?",
				query.getSQL());
		System.out.println(query.getSQL());
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
		assertTrue(count >0);
	}

}
