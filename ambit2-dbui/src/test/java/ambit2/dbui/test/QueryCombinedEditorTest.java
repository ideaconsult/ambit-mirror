package ambit2.dbui.test;

import javax.swing.JOptionPane;

import junit.framework.TestCase;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryDataset;
import ambit2.db.search.structure.QueryField;
import ambit2.db.search.structure.QueryFieldNumeric;
import ambit2.db.search.structure.QuerySimilarityStructure;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.db.search.structure.QueryStructure;
import ambit2.dbui.QueryCombinedEditor;

public class QueryCombinedEditorTest extends TestCase {
	public void test() throws Exception {
		QueryCombinedStructure l = new QueryCombinedStructure();
		l.add(new QueryField());
		//l.add(new QueryStructureByID(100,200));
		l.add(new QuerySimilarityStructure());
		l.add(new QueryFieldNumeric());
		l.add(new QueryDataset());
		l.add(new QueryStructure());
		l.add(new QueryStoredResults());
		
		QueryCombinedEditor q = new QueryCombinedEditor();
		q.setObject(l);
		JOptionPane.showMessageDialog(null,q.getJComponent());
		System.out.println(q.getObject().getCombine_queries());
		System.out.println(q.getObject().getScope());

	}
}
