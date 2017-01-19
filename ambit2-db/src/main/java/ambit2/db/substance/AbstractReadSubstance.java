package ambit2.db.substance;

import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.q.conditions.EQCondition;
import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.db.search.AbstractQuery;

public abstract class AbstractReadSubstance<F, T> extends
		AbstractQuery<F, T, EQCondition, SubstanceRecord> implements
		IQueryRetrieval<SubstanceRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8441610930610137578L;
	protected static String sql = "select idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name from substance\n";
	
	protected enum _sqlids {
		idsubstance, prefix, huuid, documentType, format, name, publicname, content, substanceType, rs_prefix, rs_huuid, owner_prefix, owner_huuid, owner_name;
		public int getIndex() {
			return ordinal() + 1;
		}
	}

	protected abstract SubstanceRecord getRecord();

	@Override
	public SubstanceRecord getObject(ResultSet rs) throws AmbitException {
		try {
			SubstanceRecord r = getRecord();
			r.clear();
			r.setIdsubstance(rs.getInt(_sqlids.idsubstance.name()));
			r.setFormat(rs.getString(_sqlids.format.name()));
			r.setSubstanceName(rs.getString(_sqlids.name.name()));
			r.setPublicName(rs.getString(_sqlids.publicname.name()));
			try {
				String uuid = I5Utils.getPrefixedUUID(
						rs.getString(_sqlids.prefix.name()),
						rs.getString(_sqlids.huuid.name()));

				r.setSubstanceUUID(uuid);
			} catch (Exception xx) {
				r.setSubstanceUUID(null);
			}
			try {
				String uuid = I5Utils.getPrefixedUUID(
						rs.getString(_sqlids.rs_prefix.name()),
						rs.getString(_sqlids.rs_huuid.name()));
				r.setReferenceSubstanceUUID(uuid);
			} catch (Exception xx) {
				r.setReferenceSubstanceUUID(null);
			}
			try {
				String uuid = I5Utils.getPrefixedUUID(
						rs.getString(_sqlids.owner_prefix.name()),
						rs.getString(_sqlids.owner_huuid.name()));
				r.setOwnerUUID(uuid);
			} catch (Exception xx) {
				r.setOwnerUUID(null);
			}
			try {
				String owner_name = rs.getString(_sqlids.owner_name.name());
				r.setOwnerName(owner_name);
			} catch (Exception xx) {
				r.setOwnerName(null);
			}
			Blob o = rs.getBlob(_sqlids.content.name());
			if (o != null) {
				byte[] bdata = o.getBytes(1, (int) o.length());
				r.setContent(new String(bdata, Charset.forName("UTF-8")));
			}
			r.setSubstancetype(rs.getString(_sqlids.substanceType.name()));
			// rs.getString(_sqlids.documentType.name());
			return r;
		} catch (SQLException x) {
			x.printStackTrace();
			throw new AmbitException(x);
		}
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(SubstanceRecord object) {
		return 1;
	}
}
