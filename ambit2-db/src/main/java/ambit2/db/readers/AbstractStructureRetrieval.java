package ambit2.db.readers;

import java.util.ArrayList;
import java.util.List;

import ambit2.core.data.IStructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

public abstract class AbstractStructureRetrieval<R> extends AbstractQuery<IStructureRecord,IStructureRecord,EQCondition,R> implements IRetrieval<R> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static String sql = "select structure.idstructure,idchemical,uncompress(structure) as ustructure,format,type_structure from structure where structure.idstructure =?";
	protected final static int ID_idstructure=1;
	protected final static int ID_idchemical=2;
	protected final static int ID_structure=3;
	protected final static int ID_format=4;
	protected final static int ID_type=5;

	public String getSQL() throws AmbitException {
		return sql;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdstructure()));
		return params;		
	}

}
