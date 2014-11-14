package ambit2.dbui.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IAmbitEditor;
import ambit2.db.processors.QueryInfo2Query;
import ambit2.db.search.QueryCombined;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.QueryInfo;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.RetrieveFieldNames;
import ambit2.db.search.structure.QueryDataset;
import ambit2.db.search.structure.QueryField;
import ambit2.ui.EditorPreferences;



public class QueryInfoTest extends QueryTest<IQueryObject> {
	@Override
	protected IQueryObject createQuery() throws Exception {
		QueryInfo info = new QueryInfo();
		QueryInfo.setFieldnames(fill_in());
		//info.setMolecule(MoleculeFactory.makeBenzene());
		info.setFieldname1("Field_1");
		info.setCondition1("=");
		info.setIdentifier1("20");
		
		info.setFieldname2("Field_2");
		info.setCondition2("=");
		info.setIdentifier2("40");
		info.setCombine(QueryInfo.COMBINE_ANY);
		info.setDataset(new SourceDataset("EINECS"));
		
		IAmbitEditor e = EditorPreferences.getEditor(info);
		assertNotNull(e);
		//JOptionPane.showMessageDialog(null,e.getJComponent());		
		
		QueryInfo2Query processor = new QueryInfo2Query();
		IQueryObject query = processor.process(info);
		
		assertTrue(query instanceof QueryCombined);
		
		QueryCombined q = (QueryCombined) query;
		assertEquals(2,q.size());
		
		//first query
		assertTrue(q.get(0) instanceof QueryField);
		assertEquals("Field_1",((QueryField) q.get(0)).getFieldname().getName());
		assertEquals("20",((QueryField) q.get(0)).getValue());
		assertEquals(StringCondition.getInstance("="),((QueryField) q.get(0)).getCondition());				
		
		assertTrue(q.get(1) instanceof QueryField);
		assertEquals("Field_2",((QueryField) q.get(1)).getFieldname());
		assertEquals("40",((QueryField) q.get(1)).getValue());
		assertEquals(StringCondition.getInstance("="),((QueryField) q.get(1)).getCondition());
		
		//scope
		assertNotNull(q.getScope());
		assertTrue(q.getScope() instanceof QueryDataset);
		QueryDataset d = (QueryDataset) q.getScope();
		assertEquals("EINECS",d.getValue().getName());

		System.out.println(q.getSQL());
		
		List<QueryParam> p = query.getParameters();
		for (int i=0; i < p.size(); i++) {
			System.out.print(p.get(i).getType());
			System.out.print('\t');
			System.out.print(p.get(i).getValue());
			System.out.print('\n');
		}
		
		//fail("not implemented");
		return query;
	}
	@Override
	protected void verify(IQueryObject query, ResultSet rs) throws Exception {
		System.out.println(query.getSQL());

		
		while (rs.next()) {
			System.out.println(rs.getString(2));
		}		
	}
	
	protected String[] fill_in() throws Exception {
		Connection c = datasource.getConnection();
		RetrieveFieldNames fn = new RetrieveFieldNames();
		
		QueryExecutor<RetrieveFieldNames> qe = new QueryExecutor<RetrieveFieldNames>();
		qe.setConnection(c);
		ResultSet rs = qe.process(fn);
		ArrayList<String> fieldnames = new ArrayList<String>();
		while (rs.next()) {
			fieldnames.add(fn.getObject(rs).getName());
		}
		rs.close();
		qe.close();
		if (!c.isClosed())
			c.close();
		
		String[] t = new String[fieldnames.size()];
		for (int i=0; i < t.length;i ++)
			t[i]=fieldnames.get(i);
		return t;	
	}
	
}

