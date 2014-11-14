package ambit2.db.readers;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;

/**
 * Retrieve structures, given a chemical or structure ID. If FieldName is true, retrieves all structures per chemical
 * @author nina
 *
 * @param <R>
 */
public abstract class AbstractStructureRetrieval<R> extends AbstractQuery<Boolean,IStructureRecord,EQCondition,R> implements IQueryRetrieval<R> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static String sql = "select structure.idstructure,idchemical,uncompress(structure) as ustructure,format,type_structure,atomproperties from structure where structure.%s =? order by type_structure desc";
	protected enum _sqlids {
		idstructure,
		idchemical,
		ustructure,
		format,
		type_structure,
		atomproperties;
		public int getIndex() {
			return ordinal()+1;
		}
	}


	//protected boolean smartsProperties = false;
	
	public AbstractStructureRetrieval() {
		super();
		setFieldname(false);
	}
	public String getSQL() throws AmbitException {
		return String.format(sql,getFieldname()?"idchemical":"idstructure");
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getFieldname()?getValue().getIdchemical():getValue().getIdstructure()));
		return params;		
	}
	public double calculateMetric(R object) {return 1;};
	public boolean isPrescreen() {
		return false;
	}

}
