package ambit2.db.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.db.substance.ReadSubstanceByOwner._ownersearchmode;

/**
 * All substance given an owner name or UUID
 * @author nina
 *
 */
public class ReadSubstanceByOwner extends AbstractReadSubstance<_ownersearchmode,String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7362476423007097726L;

	private SubstanceRecord record = new SubstanceRecord();

	
	public enum _ownersearchmode {
		owner_name {
			@Override
			String getDefaultSearchValue() {
				return "OECD / Paris / France";
			}
		},
		owner_uuid {
			@Override
			String getDefaultSearchValue() {
				return "IUC4-44BF02D8-47C5-385D-B203-9A8F315911CB";
			}
		};
		abstract String getDefaultSearchValue();
	}

	private static String sql_by_owner_name =
		"select substance.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "+ 
		"from substance where owner_name regexp ?";
	
	private static String sql_by_owner_uuid = 
		"select substance.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "+ 
		"from substance where owner_prefix=? and owner_uuid=unhex(?)";
	
	public ReadSubstanceByOwner() {
		this(_ownersearchmode.owner_uuid,_ownersearchmode.owner_uuid.getDefaultSearchValue());
	}
	public ReadSubstanceByOwner(_ownersearchmode type,String id) {
		super();
		setFieldname(type);
		setValue(id);
	}	
	@Override
	public String getSQL() throws AmbitException {
		switch (getFieldname()) {
		case owner_name: {
			return sql_by_owner_name;
		}
		case owner_uuid: {
			return sql_by_owner_uuid;
		}
		}
		throw new AmbitException("Unsupported "+getFieldname().name());
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()==null) throw new AmbitException("Empty type");
		switch (getFieldname()) {
		case owner_name: {
			params.add(new QueryParam<String>(String.class, getValue()==null?getFieldname().getDefaultSearchValue():getValue()));
			return params;
		}
		case owner_uuid: {
			String[] uuid = I5Utils.splitI5UUID(getValue());
			params.add(new QueryParam<String>(String.class, uuid[0]));
			params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));		
			return params;
		}
		}
		throw new AmbitException("Unsupported "+getFieldname().name());
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