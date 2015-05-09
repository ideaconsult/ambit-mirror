package ambit2.db.update.bundle.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.AbstractReadSubstance;

public class ReadSubstancesByBundle  extends AbstractReadSubstance<SubstanceEndpointsBundle,SubstanceRecord> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 661276311247312738L;
	private static String sql = "select idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name from substance join bundle_substance using(idsubstance) where idbundle=?\n";
	
	private static String sql_chemicals = 
	"SELECT null,'CHEM',hex(idchemical) as huuid,null,null,idchemical as name,idchemical as publicname,null,'chemical',null,null,null,null,null\n"+ 
	"FROM bundle_chemicals	where idbundle=? and idchemical not in \n"+
	"(select idchemical from substance_relation join bundle_substance using(idsubstance) where idbundle=?)";
	
	protected boolean includeStructuresWithoutSubstances = false;
	public boolean isIncludeStructuresWithoutSubstances() {
	    return includeStructuresWithoutSubstances;
	}
	public void setIncludeStructuresWithoutSubstances(boolean includeStructuresWithoutSubstances) {
	    this.includeStructuresWithoutSubstances = includeStructuresWithoutSubstances;
	}
	
	public ReadSubstancesByBundle() {
		super();
	}
	public ReadSubstancesByBundle(SubstanceEndpointsBundle bundle) {
		super();
		setFieldname(bundle);
	}
	
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		if (getFieldname()!=null) {
			params1.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
			return params1;
		}
		throw new AmbitException("Unspecified substance");
	}

	@Override
	protected SubstanceRecord getRecord() {
		return (getValue()==null)?new SubstanceRecord():getValue();
	}

}
