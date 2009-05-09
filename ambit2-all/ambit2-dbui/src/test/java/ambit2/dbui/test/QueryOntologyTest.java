package ambit2.dbui.test;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.swing.JOptionPane;

import ambit2.base.data.Dictionary;
import ambit2.db.search.property.QueryOntology;
import ambit2.dbui.dictionary.OntologyEditor;

public class QueryOntologyTest extends QueryTest<QueryOntology> {

	@Override
	protected QueryOntology createQuery() throws Exception {
		QueryOntology q = new QueryOntology();
		q.setValue(new Dictionary("einecs_structures_V13Apr07.sdf",null));
		return q;
	}

	@Override
	protected void verify(QueryOntology query, ResultSet rs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testSelect() throws Exception {
		/*
		AmbitRows<Object> rows = new AmbitRows<Object>();

		Connection c = datasource.getConnection();
		rows.setConnection(c);
		rows.setQuery(query);
		Assert.assertTrue(rows.size()>0);
		//TemplatePropertyPanel panel = new TemplatePropertyPanel();
		ListEditor p1 = new ListEditor(new RowsModel(rows)) {
			@Override
			protected IAmbitEditor getEditor(Object object) {
				try {
					return EditorPreferences.getEditor(object);
				} catch (Exception x) {
					x.printStackTrace();
					return null;
				}
			}
		};
		
		ListEditor p2 = new ListEditor(new RowsModel(rows)) {
			@Override
			protected IAmbitEditor getEditor(Object object) {
				try {
					return EditorPreferences.getEditor(object);
				} catch (Exception x) {
					x.printStackTrace();
					return null;
				}
			}
		};
		JSplitPane p = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,p1.getJComponent(),p2.getJComponent());
		
		//panel.setObject();
		JOptionPane.showMessageDialog(null,p);
		rows.close();
		*/
		OntologyEditor e = new OntologyEditor();
		Connection c = datasource.getConnection();
		e.setConnection(c);		
		JOptionPane.showMessageDialog(null,e.getJComponent());
		c.close();
		
	};


}
