package ambit2.db.update.qlabel;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.QLabel;
import ambit2.base.interfaces.IStructureRecord;

/**
 * Deletes quality label given a structure
 * @author nina
 *
 */
public class DeleteStructureQLabel extends	AbstractUpdate<IStructureRecord, QLabel> {

	protected String sql = "delete FROM quality_structure where ";
	
	protected String sql_defaultuser = "delete FROM quality_structure where user_name = SUBSTRING_INDEX(user(),'@',1) and ";
	
	protected String whereStructure = "idstructure=?";
	protected String whereUser = "user_name=?";
	protected String whereLabel = "label=?";
	
	public DeleteStructureQLabel(IStructureRecord record, QLabel label) {
		setGroup(record);
		setObject(label);
	}
	public DeleteStructureQLabel() {
		this(null,null);
	}	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		
		if ((getGroup()!=null) && (getGroup().getIdstructure()>0))	
			params1.add(new QueryParam<Integer>(Integer.class, getGroup().getIdstructure()));
		
		if (getObject()!=null) {
			if (getObject().getLabel()!= null)
				params1.add(new QueryParam<String>(String.class, getObject().getLabel().toString()));
			if (getObject().getUser()!=null) 
				params1.add(new QueryParam<String>(String.class, getObject().getUser().getName()));
			
		}
		return params1;
	}

	public String[] getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		b.append(((getObject()==null) || (getObject().getUser()==null))?sql_defaultuser:sql);
		boolean first = true;
		if ((getGroup()!=null) && (getGroup().getIdstructure()>0))	{
			b.append(first?"":" and ");	first = false;
			b.append(whereStructure);
		}
		if (getObject()!=null) {
			if (getObject().getLabel()!=null) {
				b.append(first?"":" and ");	first = false;
				b.append(whereLabel);
			}
			if (getObject().getUser()!=null) {
				b.append(first?"":" and ");	first = false;
				b.append(whereUser);
			}
		}
		return new String[] {b.toString()};
	}

	public void setID(int index, int id) {
		// TODO Auto-generated method stub
		
	}


}
