package ambit2.db.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SubstanceRecord;

public class ReadByReliabilityFlags  extends AbstractReadSubstance<String,String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8701796200692548800L;
	/**
	 * 
	 */
	private SubstanceRecord record = new SubstanceRecord();
	enum _type {
		reliability {
			@Override
			public String getDefaultValue() {
				return "1 (reliable without restriction)";
			}
		},
		isRobustStudy {
			@Override
			public QueryParam getQueryParam(String value) {
				try {
					return new QueryParam<Integer>(Integer.class, Integer.parseInt(value));
				} catch (Exception x) {
					return new QueryParam<Integer>(Integer.class, 1);
				}
			}
		},
		purposeFlag {
			@Override
			public String getDefaultValue() {
				return "key study";
			}
		},
		studyResultType {
			@Override
			public String getDefaultValue() {
				return "experimental result";
			}
		};
		public String getDefaultValue() {
			return "true";
		}
		public QueryParam getQueryParam(String value) {
			return new QueryParam<String>(String.class, value==null?getDefaultValue():value);
		}
	}


	
	private static String sql_byreliability =
		"select s.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType," +
		"rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name\n"+
		"from substance s join (\n"+
		"select substance_prefix,substance_uuid from substance_protocolapplication\n"+ 
		"where %s = ?\n"+
		"group by substance_prefix,substance_uuid\n"+
		") p on s.prefix=p.substance_prefix and s.uuid=p.substance_uuid";
				
	public ReadByReliabilityFlags(String type,String id) {
		super();
		setFieldname(type);
		setValue(id);
	}	
	@Override
	public void setFieldname(String fieldname) {
		try {
			super.setFieldname(_type.valueOf(fieldname).name());
		} catch (Exception x) {
			super.setFieldname(_type.reliability.name());
		}
	}
	@Override
	public String getSQL() throws AmbitException {
		return String.format(sql_byreliability,getFieldname()==null?_type.reliability.name():getFieldname());
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		_type t = _type.reliability;
		try {
			 t = _type.valueOf(fieldname);
		} catch (Exception x) {}
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(t.getQueryParam(getValue()));
		return params;
	}

	@Override
	protected SubstanceRecord getRecord() {
		return record;
	}
	
	@Override
	public String toString() {
		return String.format("%s=%s", getFieldname()==null,getValue());
	}

}