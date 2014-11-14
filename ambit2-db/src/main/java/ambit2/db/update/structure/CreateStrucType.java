package ambit2.db.update.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;

/**
 * Writes structure type value into table structure
 * @author nina
 *
 */
public class CreateStrucType extends AbstractUpdate<IStructureRecord, STRUC_TYPE> {
	protected String[] sql = {
		"update structure set type_structure=? where idstructure=?"	
	};
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if ((getGroup()==null) || (getGroup().getIdstructure()<=0)) throw new AmbitException("No structure");
		if (getObject()==null) throw new AmbitException("Undefined type");
		List<QueryParam> p = new ArrayList<QueryParam>();

		p.add(new QueryParam<String>(String.class,getObject().toString()));
		p.add(new QueryParam<Integer>(Integer.class,getGroup().getIdstructure()));		
		return p;
	}

	public String[] getSQL() throws AmbitException {
		return sql;
	}

	public void setID(int index, int id) {
		// TODO Auto-generated method stub
		
	}

}
