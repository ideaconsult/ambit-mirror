package ambit2.dbui.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import ambit2.db.SourceDataset;
import ambit2.db.processors.QueryInfo2Query;
import ambit2.db.readers.RetrieveFieldNames;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.QueryCombined;
import ambit2.db.search.QueryDataset;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.QueryField;
import ambit2.db.search.QueryInfo;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;
import ambit2.ui.EditorPreferences;
import ambit2.ui.editors.IAmbitEditor;



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
		assertEquals("Field_1",((QueryField) q.get(0)).getFieldname());
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
			fieldnames.add(fn.getObject(rs));
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
	/*
	protected SourceDataset[] datasets() throws Exception {
		Connection c = datasource.getConnection();
		RetrieveFieldNames fn = new RetrieveFieldNames();
		
		QueryExecutor<RetrieveFieldNames> qe = new QueryExecutor<RetrieveFieldNames>();
		qe.setConnection(c);
		ResultSet rs = qe.process(fn);
		ArrayList<String> fieldnames = new ArrayList<String>();
		while (rs.next()) {
			fieldnames.add(fn.getObject(rs));
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
    */
}
/*
 * AND
select Q1.idquery,s.idchemical,idstructure,Q1.selected as selected,Q1.metric as metric from structure as s
join
(select 1 as idquery,idchemical,idstructure,1 as selected,1 as metric from structure join struc_dataset using(idstructure) join src_dataset using (id_srcdataset) where src_dataset.name = "EINECS")
as QSCOPE
using (idstructure)
join
(select 1 as idquery,idchemical,idstructure,1 as selected,1 as metric from structure join structure_fields using(idstructure) join field_names as f using (idfieldname) where f.name="Field_1" and  value = "20")
as Q1
using (idstructure)
join
(select 1 as idquery,idchemical,idstructure,1 as selected,1 as metric from structure join structure_fields using(idstructure) join field_names as f using (idfieldname) where f.name="Field_2" and  value = "40")
as Q2
using (idstructure)
 */
