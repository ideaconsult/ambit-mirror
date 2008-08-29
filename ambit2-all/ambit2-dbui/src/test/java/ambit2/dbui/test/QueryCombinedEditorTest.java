package ambit2.dbui.test;

import javax.swing.JOptionPane;

import junit.framework.TestCase;
import ambit2.db.search.QueryCombined;
import ambit2.db.search.QueryField;
import ambit2.db.search.QueryStructureByID;
import ambit2.dbui.QueryCombinedEditor;

public class QueryCombinedEditorTest extends TestCase {
	public void test() throws Exception {
		QueryCombined l = new QueryCombined();
		l.add(new QueryField());
		l.add(new QueryStructureByID(100,200));
		
		QueryCombinedEditor q = new QueryCombinedEditor();
		q.setObject(l);
		JOptionPane.showMessageDialog(null,q);

	}
}
