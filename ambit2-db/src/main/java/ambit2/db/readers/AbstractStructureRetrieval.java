package ambit2.db.readers;

import java.util.ArrayList;
import java.util.List;

import ambit2.core.data.IStructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

public abstract class AbstractStructureRetrieval<R> extends AbstractQuery<IStructureRecord,IStructureRecord,EQCondition> implements IRetrieval<R> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static String sql = "select structure.idstructure,idsubstance,uncompress(structure) as ustructure,format,type_structure from structure where structure.idstructure =?";


	public String getSQL() throws AmbitException {
		return sql;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdstructure()));
		return params;		
	}

}
