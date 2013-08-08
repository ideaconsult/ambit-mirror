package ambit2.db.substance;

import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

public class ReadSubstance  extends AbstractQuery<Boolean,SubstanceRecord,EQCondition,SubstanceRecord> implements IQueryRetrieval<SubstanceRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3661558183996204387L;
	private static String sql = "select idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType from substance where idsubstance=?";
	protected enum _sqlids {
		idsubstance,
		prefix,
		huuid,
		documentType,
		format,
		name,
		publicname,
		content,
		substanceType;
		public int getIndex() {
			return ordinal()+1;
		}
	}
	public ReadSubstance() {
		super();
	}
	public ReadSubstance(SubstanceRecord record) {
		super();
		setValue(record);
	}

	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null || getValue().getIdsubstance()<=0) throw new AmbitException("Substance not defined");
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<Integer>(Integer.class, getValue().getIdsubstance()));
		return params1;
	}

	@Override
	public SubstanceRecord getObject(ResultSet rs) throws AmbitException {
		 try {
	            SubstanceRecord r = getValue();
	            r.clear();
	            r.setIdsubstance(rs.getInt(_sqlids.idsubstance.name()));
	            r.setFormat(rs.getString(_sqlids.format.name()));
	            r.setName(rs.getString(_sqlids.name.name()));
	            r.setPublicName(rs.getString(_sqlids.publicname.name()));
	            try {
		            String uuid = rs.getString(_sqlids.prefix.name()) + "-" + 
		            		I5Utils.addDashes(rs.getString(_sqlids.huuid.name())).toLowerCase();
		            r.setI5UUID(uuid);
	            } catch (Exception xx) {
	            	r.setI5UUID(null);
	            }
	            Blob o = rs.getBlob(_sqlids.content.name());
	            if (o!=null) {
	            	byte[] bdata = o.getBytes(1, (int) o.length());
	            	r.setContent(new String(bdata,Charset.forName("UTF-8")));
	            }
	            r.setSubstancetype(rs.getString(_sqlids.substanceType.name()));
	            //rs.getString(_sqlids.documentType.name());
	            return r;
	        } catch (SQLException x){
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
