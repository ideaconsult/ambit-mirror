package ambit2.db.update.dataset;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.modbcum.i.query.QueryParam;

/**
 * 
 * @author nina
 *
 */
public class QueryCountDatasetIntersection<FACET extends IFacet<String>> extends QueryCount<FACET> {
	
	public QueryCountDatasetIntersection(String facetURL) {
		super(facetURL);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2679397674845722288L;
	protected boolean fieldname_dataset = true;
	protected boolean value_dataset = true;
	
	protected static String sql_datasets = 
	"select concat('Intersection of /dataset/',d1.id_srcdataset,' and /dataset/',d2.id_srcdataset),count(distinct(s1.idchemical))\n"+
	"from structure s1 join struc_dataset d1 on d1.idstructure=s1.idstructure\n"+
	"join structure s2 on s1.idchemical=s2.idchemical\n"+
	"join struc_dataset d2 on d2.idstructure=s2.idstructure\n"+
	"where d1.id_srcdataset=? and d2.id_srcdataset=?\n";
	
	protected static String sql_queries = 
		"select concat('Intersection of /dataset/R',s1.idquery,' and /dataset/R',s2.idquery),count(distinct(s1.idchemical))\n"+
		"from query_results s1 \n"+
		"join query_results s2 on s1.idchemical=s2.idchemical\n"+
		"where s1.idquery = ? and s2.idquery = ?\n";

	protected static String sql_query_and_dataset = 
		"select concat('Intersection of /dataset/R',s1.idquery,' and /dataset/',d2.id_srcdataset),count(distinct(s1.idchemical))\n"+
		"from query_results s1 \n"+
		"join structure s2 on s1.idchemical=s2.idchemical\n"+
		"join struc_dataset d2 on d2.idstructure=s2.idstructure\n"+		
		"where s1.idquery = ? and d2.id_srcdataset = ?\n";
	
	
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (fieldname_dataset)
			if (value_dataset) {
				params.add(new QueryParam<Integer>(Integer.class, getParam(getFieldname())));
				params.add(new QueryParam<Integer>(Integer.class, getParam(getValue())));
			} else {
				params.add(new QueryParam<Integer>(Integer.class, getParam(getValue())));
				params.add(new QueryParam<Integer>(Integer.class, getParam(getFieldname())));
			}
		else {
				params.add(new QueryParam<Integer>(Integer.class, getParam(getValue())));
				params.add(new QueryParam<Integer>(Integer.class, getParam(getFieldname())));
		}		
		return params;
	}
	
	@Override
	public void setFieldname(String fieldname) {
		super.setFieldname(fieldname);
		fieldname_dataset = !fieldname.startsWith(QR_PREFIX);
	}
	@Override
	public void setValue(String value) {
		super.setValue(value);
		value_dataset = !value.startsWith(QR_PREFIX);
	}
	
	public String getSQL() throws AmbitException {
		if (fieldname_dataset && value_dataset)
			return sql_datasets;
		if (!fieldname_dataset && !value_dataset)
			return sql_queries;
		else return sql_query_and_dataset;
	}

}
