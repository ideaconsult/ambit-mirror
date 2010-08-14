package ambit2.db.search.test;

import java.sql.ResultSet;

import ambit2.base.data.Dictionary;
import ambit2.db.search.property.QueryOntology;

public class QueryOntologyTest extends QueryTest<QueryOntology> {

	@Override
	protected QueryOntology createQuery() throws Exception {
		QueryOntology q = new QueryOntology();
		q.setValue(new Dictionary("Physicochemical effects",null));
		return q;
	}

	@Override
	protected void verify(QueryOntology query, ResultSet rs) throws Exception {
		int count=0;
		while (rs.next()) {
			//System.out.println(query.getObject(rs));
			count++;
		}
		//System.out.println(count);
	}

}
