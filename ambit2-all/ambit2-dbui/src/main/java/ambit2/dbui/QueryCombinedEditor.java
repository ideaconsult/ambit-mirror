package ambit2.dbui;



import java.util.List;

import javax.swing.JComponent;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.ui.editors.ListEditor;

public class QueryCombinedEditor  implements IAmbitEditor<QueryCombinedStructure> {
	protected JComponent component = null;
	protected QueryCombinedStructure query;
	protected ListEditor<List<IQueryRetrieval<IStructureRecord>>,IQueryRetrieval<IStructureRecord>> listEditor;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2731638693126278344L;

	public QueryCombinedEditor() {
		//super(null,new String[] {"id","SQL"},"Queries","Query");
		listEditor = new ListEditor<List<IQueryRetrieval<IStructureRecord>>,IQueryRetrieval<IStructureRecord>>(
				null,new String[] {"name"},"Query","Query"
				);
		component = listEditor;
	}

	public JComponent getJComponent() {
		return component;
	}

	public QueryCombinedStructure getObject() {
		return query;
	}

	public boolean isEditable() {
		return true;
	}

	public void setEditable(boolean editable) {
	}

	public void setObject(QueryCombinedStructure object) {
		this.query = object;
		listEditor.setObject(object.getQueries());
		
		
	}
	public boolean confirm() {
		return true;
	}	

}
