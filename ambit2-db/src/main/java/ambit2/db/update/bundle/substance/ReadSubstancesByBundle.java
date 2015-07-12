package ambit2.db.update.bundle.substance;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.facet.BundleRoleFacet;
import ambit2.db.substance.AbstractReadSubstance;

public class ReadSubstancesByBundle extends
		AbstractReadSubstance<SubstanceEndpointsBundle, SubstanceRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 661276311247312738L;
	private static String sql = "select idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name,tag,remarks from substance join bundle_substance using(idsubstance) where idbundle=?\n";

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
		if (getFieldname() != null) {
			params1.add(new QueryParam<Integer>(Integer.class, getFieldname()
					.getID()));
			return params1;
		}

		throw new AmbitException("Unspecified bundle");
	}

	@Override
	protected SubstanceRecord getRecord() {
		return (getValue() == null) ? new SubstanceRecord() : getValue();
	}

	protected boolean enableFeatures = false;

	public boolean isEnableFeatures() {
		return enableFeatures;
	}

	public void setEnableFeatures(boolean enableFeatures) {
		this.enableFeatures = enableFeatures;
	}

	@Override
	public SubstanceRecord getObject(ResultSet rs) throws AmbitException {
		SubstanceRecord record = super.getObject(rs);
		try {
			if (enableFeatures) {
				LiteratureEntry reference = LiteratureEntry
						.getBundleReference(fieldname);
				String value = rs.getString("remarks");
				if (value != null) {
					Property tag = new Property("tag", reference);
					tag.setEnabled(true);
					record.setProperty(tag, rs.getString("tag"));
				}

				value = rs.getString("remarks");
				if (value != null) {
					Property remarks = new Property("remarks", reference);
					remarks.setEnabled(true);
					record.setProperty(remarks, rs.getString("remarks"));
				}
			} else {
				BundleRoleFacet facet = new BundleRoleFacet(null);
				facet.setCount(1);
				SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle();
				bundle.setID(getFieldname().getID());
				facet.setValue(bundle);
				facet.setRemarks(rs.getString("remarks"));
				facet.setTag(rs.getString("tag"));
				record.addFacet(facet);
			}
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		return record;
	}
}
