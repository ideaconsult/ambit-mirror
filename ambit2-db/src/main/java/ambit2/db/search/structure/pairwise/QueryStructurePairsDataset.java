package ambit2.db.search.structure.pairwise;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SourceDataset;

/**
 * Selects a structure by idstructure
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryStructurePairsDataset extends	QueryStructurePairs<SourceDataset, SourceDataset> {

	protected final String sql =
		"SELECT\n"+
		"if(s1.idstructure <s2.idstructure,s1.idstructure,s2.idstructure) id1,\n"+
		"if(s1.idstructure >s2.idstructure,s1.idstructure,s2.idstructure) id2\n"+
		"FROM struc_dataset s1\n"+
		"join\n"+
		"struc_dataset s2\n"+
		"where\n"+
		"s1.id_srcdataset=?\n"+
		"and\n"+
		"s2.id_srcdataset=?\n"+
		"and\n"+
		"s1.idstructure != s2.idstructure\n"+
		"group by id1,id2\n"+
		"order by id1";
	/**
	 * 
	 */
	private static final long serialVersionUID = -526806487145457640L;



	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname() == null) throw new AmbitException("Properties not defined!");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getFieldname().getId()));
		params.add(new QueryParam<Integer>(Integer.class, getValue().getId()));
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}


}
