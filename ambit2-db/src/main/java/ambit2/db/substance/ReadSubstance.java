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
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

public class ReadSubstance  extends AbstractQuery<CompositionRelation,SubstanceRecord,EQCondition,SubstanceRecord> implements IQueryRetrieval<SubstanceRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3661558183996204387L;
	private static String sql = "select idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name from substance\n";
	private static String  q_idsubstance = "idsubstance=?";
	private static String  q_uuid = "prefix=? and hex(uuid)=?";
	
	private static String sql_relatedsubstances = 
		"select idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name\n"+ 
		"from substance	where idsubstance in (select distinct(idsubstance) from substance_relation where idchemical = ?)";
	
	private static String sql_bychemical =
		"select distinct(substance.idsubstance),prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "+ 
		"from substance join substance_relation using(rs_prefix,rs_uuid) where rs_prefix is not null and rs_uuid is not null and idchemical = ?";

	protected enum _sqlids {
		idsubstance,
		prefix,
		huuid,
		documentType,
		format,
		name,
		publicname,
		content,
		substanceType,
		rs_prefix,
		rs_huuid,
		owner_prefix,
		owner_huuid,
		owner_name;
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
	public ReadSubstance(CompositionRelation composition) {
		super();
		setFieldname(composition);
	}
	@Override
	public String getSQL() throws AmbitException {
		if (getValue()!=null) {
			if (getValue().getIdsubstance()>0) {
				StringBuilder b = new StringBuilder();
				b.append(sql);
				b.append("where ");
				b.append(q_idsubstance);
				return b.toString();
			} else if (getValue().getCompanyUUID()!= null) {
				StringBuilder b = new StringBuilder();
				b.append(sql);
				b.append("where ");
				b.append(q_uuid);
				return b.toString();
			} else if (getValue().getIdchemical()>0) { 
				return sql_bychemical;
			} else
				throw new AmbitException("Unspecified substance");
		} else if (getFieldname()!=null && getFieldname().getSecondStructure()!=null && getFieldname().getSecondStructure().getIdchemical()>0) {
			return sql_relatedsubstances;		
		} else {
			return sql;
		}
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		if (getValue()!=null) {
			if (getValue().getIdsubstance()>0) {
				params1.add(new QueryParam<Integer>(Integer.class, getValue().getIdsubstance()));
				return params1;
			} else if (getValue().getCompanyUUID()!= null) {
				String o_uuid = getValue().getCompanyUUID();
				if (o_uuid==null) throw new AmbitException("Empty substance id");
				String[] uuid = new String[]{null,o_uuid==null?null:o_uuid.toString()};
				if (o_uuid!=null) 
					uuid = I5Utils.splitI5UUID(o_uuid.toString());
				params1.add(new QueryParam<String>(String.class, uuid[0]));
				params1.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
				return params1;
			} else if (getValue().getIdchemical()>0) {
				params1.add(new QueryParam<Integer>(Integer.class, getValue().getIdchemical()));
				return params1;
			} else
				throw new AmbitException("Unspecified substance");
		} else if (getFieldname()!=null && getFieldname().getSecondStructure()!=null && getFieldname().getSecondStructure().getIdchemical()>0) {
			params1.add(new QueryParam<Integer>(Integer.class, getFieldname().getSecondStructure().getIdchemical()));
			return params1;			
		} else return null;
				
	}

	@Override
	public SubstanceRecord getObject(ResultSet rs) throws AmbitException {
		 try {
	            SubstanceRecord r = (getValue()==null)?new SubstanceRecord():getValue();
	            r.clear();
	            r.setIdsubstance(rs.getInt(_sqlids.idsubstance.name()));
	            r.setFormat(rs.getString(_sqlids.format.name()));
	            r.setCompanyName(rs.getString(_sqlids.name.name()));
	            r.setPublicName(rs.getString(_sqlids.publicname.name()));
	            try {
		            String uuid = rs.getString(_sqlids.prefix.name()) + "-" + 
		            		I5Utils.addDashes(rs.getString(_sqlids.huuid.name())).toLowerCase();
		            r.setCompanyUUID(uuid);
	            } catch (Exception xx) {
	            	r.setCompanyUUID(null);
	            }
	            try {
		            String uuid = rs.getString(_sqlids.rs_prefix.name()) + "-" + 
		            		I5Utils.addDashes(rs.getString(_sqlids.rs_huuid.name())).toLowerCase();
		            r.setReferenceSubstanceUUID(uuid);
	            } catch (Exception xx) {
	            	r.setReferenceSubstanceUUID(null);
	            }
	            try {
		            String uuid = rs.getString(_sqlids.owner_prefix.name()) + "-" + 
		            		I5Utils.addDashes(rs.getString(_sqlids.owner_huuid.name())).toLowerCase();
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
