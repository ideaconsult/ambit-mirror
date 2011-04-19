package ambit2.dbui.test;

import javax.swing.JOptionPane;

import org.junit.After;
import org.junit.Before;

import ambit2.db.search.structure.QuerySMARTS;
import ambit2.ui.EditorPreferences;

public class QuerySmartsEditorTest {
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	public static void main(String args[]) {
		QuerySMARTS query;
		try {
			query = new QuerySMARTS();
			JOptionPane.showMessageDialog(null,EditorPreferences.getEditor(query).getJComponent());
			System.out.println(query.toString());
			System.out.println(query.getSQL());
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
