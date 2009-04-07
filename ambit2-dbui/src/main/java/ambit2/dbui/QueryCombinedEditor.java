package ambit2.dbui;



import javax.swing.JComponent;
import javax.swing.JPanel;

import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.ui.editors.IAmbitEditor;

public class QueryCombinedEditor extends JPanel implements IAmbitEditor<QueryCombinedStructure> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2731638693126278344L;

	public QueryCombinedEditor() {
		//super(null,new String[] {"id","SQL"},"Queries","Query");
		//ListEditor<QueryCombined<IQueryObject>,IQueryObject>
	}

	public JComponent getJComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	public QueryCombinedStructure getObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		
	}

	public void setObject(QueryCombinedStructure object) {
		// TODO Auto-generated method stub
		
	}
	public boolean confirm() {
		return true;
	}	

}
