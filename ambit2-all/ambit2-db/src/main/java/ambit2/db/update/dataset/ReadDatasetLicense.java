package ambit2.db.update.dataset;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SourceDataset;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.StringCondition;

/**
 * Retrieve {@link SourceDataset} by id
 * @author nina
 *
 */
public class ReadDatasetLicense extends AbstractQuery<String,Integer,StringCondition,String>  implements IQueryRetrieval<String>{

	public static final String select_license_byid = "SELECT licenseURI FROM src_dataset where id_srcdataset = ?";
	public static final String select_license_byname = "SELECT licenseURI FROM src_dataset where name = ?";
	/**
	 * 
	 */
	private static final long serialVersionUID = -5560670663328542819L;


	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) return null;	
		List<QueryParam> params = new ArrayList<QueryParam>();
		if ((getValue()!=null) && (getValue()>0))
			params.add(new QueryParam<Integer>(Integer.class,getValue()));
		else if (getFieldname()!=null)
			params.add(new QueryParam<String>(String.class,getFieldname()));
		return params;
	}

	public String getSQL() throws AmbitException {
		if ((getValue()!=null) && (getValue()>0))
			return select_license_byid;
		else if (getFieldname()!=null)
			return select_license_byname;
		else throw new AmbitException("Invalid dataset identifier");
	}


	@Override
	public String toString() {
		return (getValue()!=null?getValue()>0
				?String.format("/dataset/%d",getValue())
				:String.format("/dataset/%s",getFieldname())
				:"Datasets");
	}

	@Override
	public String getObject(ResultSet rs) throws AmbitException {
		try {
			return rs.getString(1);
        } catch (SQLException x) {
        	throw new AmbitException(x);
        }
    }	

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(String object) {
		return 1;
	}

}
