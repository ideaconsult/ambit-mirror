package ambit2.dbui;



import ambit2.db.search.IQueryObject;
import ambit2.db.search.QueryCombined;
import ambit2.ui.editors.ListEditor;

public class QueryCombinedEditor extends ListEditor<QueryCombined<IQueryObject>,IQueryObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2731638693126278344L;

	public QueryCombinedEditor() {
		super(null,new String[] {"id","SQL"},"Queries","Query");
	}

}
