package ambit2.db.search;

import java.util.Hashtable;
import java.util.Map;

import net.idea.modbcum.i.IQueryCondition;

public class StringCondition implements IQueryCondition {
    /**
     * 
     */
    private static final long serialVersionUID = 5190665865318789327L;
    private static Map<String, StringCondition> instances = null;
    public static final String C_EQ = "=";
    public static final String C_NOTEQ = "!=";
    public static final String C_LIKE = "like";
    public static final String C_NOTLIKE = "not like";
    public static final String C_SOUNDSLIKE = "sounds like";
    public static final String C_REGEXP = "regexp";
    public static final String C_STARTS_WITH = "regexp ^";
    public static final String C_ISNULL = "is null";
    public static final String C_ISNOTNULL = "is not null";

    public enum STRING_CONDITION {
	S_EQ {
	    @Override
	    public String toString() {
		return StringCondition.C_EQ;
	    }

	    @Override
	    public String getSQL() {
		return StringCondition.C_EQ;
	    }
	},
	S_NOTEQ {
	    @Override
	    public String toString() {
		return StringCondition.C_NOTEQ;
	    }

	    @Override
	    public String getSQL() {
		return StringCondition.C_NOTEQ;
	    }
	},
	S_LIKE {
	    @Override
	    public String toString() {
		return StringCondition.C_LIKE;
	    }

	    @Override
	    public String getSQL() {
		return StringCondition.C_LIKE;
	    }
	},
	S_NOTLIKE {
	    @Override
	    public String toString() {
		return StringCondition.C_NOTLIKE;
	    }

	    @Override
	    public String getSQL() {
		return StringCondition.C_NOTLIKE;
	    }
	},
	S_SOUNDSLIKE {
	    @Override
	    public String toString() {
		return StringCondition.C_SOUNDSLIKE;
	    }

	    @Override
	    public String getSQL() {
		return StringCondition.C_SOUNDSLIKE;
	    }
	},
	S_REGEXP {
	    @Override
	    public String toString() {
		return "regular expression";
	    }

	    @Override
	    public String getSQL() {
		return StringCondition.C_REGEXP;
	    }
	},
	S_STARTS_WITH {
	    @Override
	    public String toString() {
		return "starts with";
	    }

	    @Override
	    public String getSQL() {
		return StringCondition.C_REGEXP;
	    }

	    @Override
	    public String getParam(String value) {
		if (value == null)
		    return value;
		return '^' + value;
	    }

	    @Override
	    public String getName() {
		return StringCondition.C_STARTS_WITH;
	    }
	},
	S_ISNOTNULL {
	    @Override
	    public String getSQL() {
		return StringCondition.C_ISNOTNULL;
	    }

	},
	S_ISNULL {
	    @Override
	    public String getSQL() {
		return StringCondition.C_ISNULL;
	    }

	};

	public String getName() {
	    return getSQL();
	}

	public abstract String getSQL();

	public String getParam(String value) {
	    return value;
	}
    }

    protected STRING_CONDITION value;

    public StringCondition(STRING_CONDITION value) {
	this.value = value;
    }

    public String getParam(String value) {
	return this.value.getParam(value);
    }

    public String getName() {
	return value.toString();
    }

    public String getSQL() {
	return value.getSQL();
    }

    public static Map<String, StringCondition> getAlowedValues() {
	if (instances == null) {
	    Map<String, StringCondition> list = new Hashtable<String, StringCondition>();
	    for (STRING_CONDITION c : STRING_CONDITION.values())
		list.put(c.getName(), new StringCondition(c));
	    instances = list;
	}
	return instances;
    }

    public static StringCondition getInstance(String condition) {
	try {
	    StringCondition c = getAlowedValues().get(condition);
	    if (c != null)
		return c;
	} catch (Exception x) {

	}
	return getAlowedValues().get(C_EQ);
    }

    @Override
    public String toString() {
	return getSQL();
    }

}
