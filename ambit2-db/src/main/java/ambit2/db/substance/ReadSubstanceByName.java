package ambit2.db.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SubstanceRecord;

public class ReadSubstanceByName extends AbstractReadSubstance<String, String> {
    /**
	 * 
	 */
    private static final long serialVersionUID = -8701796200692548800L;
    /**
	 * 
	 */
    private SubstanceRecord record = new SubstanceRecord();

    public enum _namecondition {
	startwith, regexp, like // , fulltext
    }

    protected _namecondition condition = _namecondition.startwith;

    private static String sql_byname_regexp = "select substance.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "
	    + "from substance where name regexp ? or publicname regexp ?";

    private static String sql_byname_like = "select substance.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "
	    + "from substance where name like ? or publicname like ?";

    public ReadSubstanceByName() {
	this(_namecondition.startwith.name(), null);
    }

    public ReadSubstanceByName(String condition, String value) {
	super();
	setFieldname(condition);
	setValue(value);
    }

    @Override
    public void setFieldname(String fieldname) {
	try {
	    condition = _namecondition.valueOf(fieldname);
	} catch (Exception x) {
	    condition = _namecondition.startwith;
	}
	super.setFieldname(condition.name());
    }

    @Override
    public String getSQL() throws AmbitException {
	switch (condition) {
	case like: {
	    return sql_byname_like;
	}
	case regexp: {
	    return sql_byname_regexp;
	}
	default: {
	    return sql_byname_like;
	}
	}
    }

    @Override
    public List<QueryParam> getParameters() throws AmbitException {
	if (getValue() == null)
	    throw new AmbitException("No search value");
	List<QueryParam> params = new ArrayList<QueryParam>();
	switch (condition) {

	case regexp: {
	    params.add(new QueryParam<String>(String.class, getValue()));
	    params.add(new QueryParam<String>(String.class, getValue()));
	    break;
	}
	case like: {
	    String val = getValue().replace("*", "%");
	    params.add(new QueryParam<String>(String.class, val));
	    params.add(new QueryParam<String>(String.class, val));
	    break;
	}
	default: {
	    String val = getValue() + "%";
	    params.add(new QueryParam<String>(String.class, val));
	    params.add(new QueryParam<String>(String.class, val));
	    break;
	}
	}

	return params;
    }

    @Override
    protected SubstanceRecord getRecord() {
	return record;
    }

    @Override
    public String toString() {
	return String.format("name | publicname %s %s", getFieldname(), getValue());
    }

}