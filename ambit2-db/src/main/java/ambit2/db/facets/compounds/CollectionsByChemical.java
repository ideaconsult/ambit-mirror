package ambit2.db.facets.compounds;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.facet.AbstractFacet;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;

/**
 * Returns number of collections (within a folder), where idchemical is a member of
 * collections = query table; folder = sessions table
 * @author nina
 *
 */
public class CollectionsByChemical   extends AbstractFacetQuery<IStructureRecord,String[],StringCondition,IFacet<String>>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4741289697057206809L;
	private final static String sql = "SELECT group_concat(distinct(title)),count(distinct(a1.name)) c from query_results\n"+ 
						   "join query a1 using(idquery) join sessions using(idsessions)\n"+
						   "where title in (%s) and idchemical = ?";
	protected IFacet<String> record;
	
	public CollectionsByChemical(String facetURL) {
		super(facetURL);
	}
	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(IFacet<String> object) {
		return 1;
	}

	@Override
	public String getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		if ((getValue()==null) || getValue().length==0)  throw new AmbitException("Folder not defined");
		for (int i=0; i < getValue().length;i++) { if (i>0) b.append(",");b.append("?"); }
		return String.format(sql,b.toString());
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if ((getFieldname()==null) || (getFieldname().getIdchemical()<=0)) throw new AmbitException("Chemical not defined");
		if ((getValue()==null) || getValue().length==0)  throw new AmbitException("Folder not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		for (String folder : getValue())
			params.add(new QueryParam<String>(String.class,folder));
		params.add(new QueryParam<Integer>(Integer.class,getFieldname().getIdchemical()));		
		return params;
	}
	
	@Override
	protected IFacet<String> createFacet(String facetURL) {
		return new AbstractFacet<String>() {

		    /**
		     * 
		     */
		    private static final long serialVersionUID = -7825712959154973202L;};
	}

	@Override
	public IFacet<String> getObject(ResultSet rs) throws AmbitException {
		if (record == null) {
			record = createFacet(null);
		}
		try {
			record.setValue(rs.getString(1));
			record.setCount(rs.getInt(2));
			return record;
		} catch (Exception x) {
			record.setValue(x.getMessage());
			record.setCount(-1);
			return record;
		}
	}	
	
}