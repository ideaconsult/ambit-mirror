package ambit2.db.update.structure;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.config.AmbitCONSTANTS.STRUC_TYPE;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

/**
 * Writes structure type value into table structure
 * @author nina
 *
 */
public class CreateStrucType extends AbstractUpdate<IStructureRecord, STRUC_TYPE> {
	protected String[] sql = {
		"insert into structure (idstructure,type_structure) values (?,?) on duplicate key update type_structure=values(type_structure)"	
	};
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if ((getGroup()==null) || (getGroup().getIdstructure()<=0)) throw new AmbitException("No structure");
		if (getObject()==null) throw new AmbitException("Undefined type");
		List<QueryParam> p = new ArrayList<QueryParam>();
		p.add(new QueryParam<Integer>(Integer.class,getGroup().getIdstructure()));
		p.add(new QueryParam<String>(String.class,getObject().toString()));
		return p;
	}

	public String[] getSQL() throws AmbitException {
		return sql;
	}

	public void setID(int index, int id) {
		// TODO Auto-generated method stub
		
	}

}
