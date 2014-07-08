package ambit2.db.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SubstanceRecord;
import ambit2.db.substance.ReadSubstanceByStudy._studysearchmode;

public class ReadSubstanceByStudy  extends AbstractReadSubstance<_studysearchmode,String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5352529378365800440L;
	private SubstanceRecord record = new SubstanceRecord();

	
	public enum _studysearchmode {
		citation {
			@Override
			String getDefaultSearchValue() {
				return "Protein Corona";//test
			}
		},
		guideline {
			@Override
			String getDefaultSearchValue() {
				return "OECD";
			}
		},
		topcategory {
			@Override
			String getDefaultSearchValue() {
				return "P-CHEM";
			}
		},
		endpointcategory {
			@Override
			String getDefaultSearchValue() {
				return "EC_FISHTOX_SECTION";
			}
		},
		params {
			@Override
			String getDefaultSearchValue() {
				return "Pimephales promelas";
			}
		};
		abstract String getDefaultSearchValue();
	}
	private static String sql_bycitation =
		"select substance.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "+ 
		"from substance where idsubstance in " +
		" (select idsubstance from `substance` `s` join `substance_protocolapplication` `p` "+
		"on((`s`.`prefix` = `p`.`substance_prefix`) and (`s`.`uuid` = `p`.`substance_uuid`)) "+
		"where reference regexp ? )";
	
	private static String sql_byguideline =
		"select substance.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "+ 
		"from substance where idsubstance in " +
		" (select idsubstance from `substance` `s` join `substance_protocolapplication` `p` "+
		"on((`s`.`prefix` = `p`.`substance_prefix`) and (`s`.`uuid` = `p`.`substance_uuid`)) "+
		"where guidance regexp ? )";	
	
	private static String sql_bytopcategory =
		"select substance.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "+ 
		"from substance where idsubstance in " +
		" (select idsubstance from `substance` `s` join `substance_protocolapplication` `p` "+
		"on((`s`.`prefix` = `p`.`substance_prefix`) and (`s`.`uuid` = `p`.`substance_uuid`)) "+
		"where topcategory = ? )";
	
	private static String sql_byendpointcategory =
		"select substance.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "+ 
		"from substance where idsubstance in " +
		" (select idsubstance from `substance` `s` join `substance_protocolapplication` `p` "+
		"on((`s`.`prefix` = `p`.`substance_prefix`) and (`s`.`uuid` = `p`.`substance_uuid`)) "+
		"where endpointcategory = ? )";	
	
	private static String sql_byprotocolparams =
		"select substance.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "+ 
		"from substance where idsubstance in " +
		" (select idsubstance from `substance` `s` join `substance_protocolapplication` `p` "+
		"on((`s`.`prefix` = `p`.`substance_prefix`) and (`s`.`uuid` = `p`.`substance_uuid`)) "+
		"where params regexp ? )";	

	public ReadSubstanceByStudy() {
		this(_studysearchmode.guideline,"OECD");
	}
	public ReadSubstanceByStudy(_studysearchmode type,String id) {
		super();
		setFieldname(type);
		setValue(id);
	}	
	@Override
	public String getSQL() throws AmbitException {
		switch (getFieldname()) {
		case citation: {
			return sql_bycitation;
		}
		case guideline: {
			return sql_byguideline;
		}
		case topcategory: {
			return sql_bytopcategory;
		}
		case endpointcategory: {
			return sql_byendpointcategory;
		}
		case params: {
			return sql_byprotocolparams;
		}
		}
		throw new AmbitException("Unsupported "+getFieldname().name());
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()==null) throw new AmbitException("Empty type");
		params.add(new QueryParam<String>(String.class, getValue()==null?getFieldname().getDefaultSearchValue():getValue()));
		return params;
	}

	@Override
	protected SubstanceRecord getRecord() {
		return record;
	}
	
	@Override
	public String toString() {
		return String.format("%s=%s", getFieldname()==null?"":getFieldname(),getValue()==null?getFieldname().getDefaultSearchValue():getValue());
	}

}