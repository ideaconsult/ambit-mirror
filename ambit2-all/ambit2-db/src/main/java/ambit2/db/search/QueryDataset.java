package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import ambit2.core.data.IStructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.db.SourceDataset;

/**
 * src_dataset.name = ? 
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryDataset extends AbstractQuery<String,SourceDataset,EQCondition,IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8329798753353233477L;
	public final static String sqlField = 
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure join struc_dataset using(idstructure) join src_dataset using (id_srcdataset) where src_dataset.name = ?";
	public QueryDataset() {
		setCondition(EQCondition.getInstance());
	}
	public String getSQL() throws AmbitException {
		return sqlField;
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getValue().getName()));
		return params;
	}
	@Override
	public String toString() {
		return getValue().getName();
	}

}
