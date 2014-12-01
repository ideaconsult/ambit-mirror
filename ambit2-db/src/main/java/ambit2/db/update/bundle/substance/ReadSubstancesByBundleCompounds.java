package ambit2.db.update.bundle.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.AbstractReadSubstance;

/**
 * Retrieve substances, containing compounds defined by the /bundle/id/compound
 * @author nina
 *
 */
public class ReadSubstancesByBundleCompounds  extends AbstractReadSubstance<SubstanceEndpointsBundle,SubstanceRecord> {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4301930849084992853L;
	private static String sql_relatedsubstances = 
			"select idsubstance,prefix,hex(uuid) as huuid,documentType,format,s.name,publicname,content,substanceType,\n"+
			"s.rs_prefix,hex(s.rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name,\n" +
			"r.cmp_prefix,hex(r.cmp_uuid) cmp_huuid,r.name as compositionname,r.idchemical,r.relation,r.`function`,proportion_typical,proportion_typical_value,proportion_typical_unit,proportion_real_lower,proportion_real_lower_value,proportion_real_upper,proportion_real_upper_value,proportion_real_unit,r.rs_prefix as refstruc,hex(r.rs_uuid) as refstrucuuid\n"+
			"from substance s join substance_relation r using(idsubstance) join bundle_chemicals using(idchemical) where idbundle=?";

	
	public ReadSubstancesByBundleCompounds() {
		super();
	}
	public ReadSubstancesByBundleCompounds(SubstanceEndpointsBundle bundle) {
		super();
		setFieldname(bundle);
	}
	
	@Override
	public String getSQL() throws AmbitException {
		return sql_relatedsubstances;
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
