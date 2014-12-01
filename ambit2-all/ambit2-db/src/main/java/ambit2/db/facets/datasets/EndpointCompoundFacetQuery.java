package ambit2.db.facets.datasets;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;

/**
 * Lists number of datasets, given endpoint and (optionally) a compound
 * @author nina
 *
 */
public class EndpointCompoundFacetQuery extends AbstractFacetQuery<Property,IStructureRecord,StringCondition,EndpointCompoundFacet> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8340773122431657623L;
	protected EndpointCompoundFacet record;
	protected static String sql_dataset = 
		"select comments,count(distinct(id_srcdataset))\n"+
		"from src_dataset\n"+ 
		"join template_def using(idtemplate) \n"+
		"join properties using(idproperty) \n"+
		"join catalog_references cr on cr.idreference=properties.idreference\n"+ 
		"where " +
		"comments %s ? \n"+
		"and cr.type=\"Dataset\"\n" +
		"group by comments\n";

	protected static String sql_dataset_bycompound = 
		"select comments,count(distinct(id_srcdataset))\n"+
		"from src_dataset\n"+ 
		"join template_def using(idtemplate) \n"+
		"join properties using(idproperty) \n"+
		"join catalog_references cr on cr.idreference=properties.idreference\n"+
		"join struc_dataset using(id_srcdataset)\n"+
		"join structure using(idstructure)\n"+
		"where " +
		"comments %s ? \n"+
		"and cr.type=\"Dataset\"\n" +
		"and idchemical=?\n" +
		"group by comments\n";
	
	//"comments regexp \"^http://www.opentox.org/echaEndpoints.owl\" \n"+
	/**
	 * 
	 */

	public EndpointCompoundFacetQuery(String url) {
		super(url);
		setCondition(StringCondition.getInstance(StringCondition.C_STARTS_WITH));
		record = createFacet(url);	
	}
	
	@Override
	protected EndpointCompoundFacet createFacet(String facetURL) {
		EndpointCompoundFacet record = new EndpointCompoundFacet(facetURL);
		record.setProperty(getFieldname());
		record.setDataset(getValue());
		return record;
	}
	@Override
	public double calculateMetric(EndpointCompoundFacet object) {
		return 1;
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {

		List<QueryParam> params = new ArrayList<QueryParam>();
		
		String endpoint = "http://www.opentox.org/echaEndpoints.owl";
		if (getFieldname()!=null) {
			endpoint = getFieldname().getLabel()!=null?getFieldname().getLabel():endpoint;
		} else {
			setCondition(StringCondition.getInstance(StringCondition.C_STARTS_WITH));
		}
		params.add(new QueryParam<String>(String.class,endpoint));
		if ((getValue()!=null) && (getValue().getIdchemical()>0)) {
			params.add(new QueryParam<Integer>(Integer.class,getValue().getIdchemical()));
		}
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		if ((getValue()!=null) && (getValue().getIdchemical()>0)) 
			return String.format(sql_dataset_bycompound,getCondition().getSQL());
		else 
			return String.format(sql_dataset,getCondition().getSQL());
	}

	@Override
	public EndpointCompoundFacet getObject(ResultSet rs) throws AmbitException {
		if (record == null) {
			record = createFacet(null);
		}
		record.setProperty(getFieldname());
		record.setDataset(getValue());		
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

	@Override
	public String toString() {
		return "Dataset by endpoint";
	}
}
