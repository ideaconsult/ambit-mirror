package ambit2.dbui;

import ambit2.db.search.QueryField;
import ambit2.ui.editors.BeanEditor;

public class QueryFieldEditor extends BeanEditor<QueryField> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4483885648170842874L;

	public QueryFieldEditor() {
		super(null,  new String[] {"value"}, 
				new String[]{"Identifier"}, 
				"Search by any identifier" );
	}
}
