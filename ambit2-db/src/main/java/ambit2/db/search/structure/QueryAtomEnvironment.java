package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.NumberCondition;
import ambit2.core.data.HashIntDescriptorResult;

/**
 * Similarity via atom environment matrix
 * /query/similarity?search=c1ccccc1O&threshold=0.5&mode=ae
 * @author nina
 *
 */
public class QueryAtomEnvironment extends QuerySimilarity<String,HashIntDescriptorResult,NumberCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7235494968515516274L;

	public enum q_modifier {
		none {
			@Override
			public String toString() {
				return "";
			}
		},
		nl {
			@Override
			public String toString() {
				return "IN NATURAL LANGUAGE MODE";
			}
		},
		nlqe {
			@Override
			public String toString() {
				return "IN NATURAL LANGUAGE MODE WITH QUERY EXPANSION";
			}
		},
		bool {
			@Override
			public String toString() {
				return "IN BOOLEAN MODE";
			}
		},
		qe {
			@Override
			public String toString() {
				return "WITH QUERY EXPANSION";
			}
		};
		public String toString() { return name();};
	}
	public QueryAtomEnvironment() {
		this(q_modifier.bool);
	}
	public QueryAtomEnvironment(q_modifier qe) {
		super();
		setQModifier(qe);
	}
	protected q_modifier qe = q_modifier.bool;

	public q_modifier getQModifier() {
		return qe;
	}


	public void setQModifier(q_modifier qe) {
		this.qe = qe;
	}

	private final static String sql_similarity = 
			"select ? as idquery,idchemical,-1,1 as selected,match (inchi,tags) against (? %s) as metric,null as text\n"+
			"from fpatomenvironments where match (inchi,tags) against (? %s)\n";
			
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) throw new AmbitException("atom environment not defined!");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		String v = null;
		switch (qe) {
		case bool:
			v = value.asBooleanQuery(0);	
			break;

		default:
			v = value.keys2String(-1);
			break;
		}
		
		params.add(new QueryParam<String>(String.class, v));
		params.add(new QueryParam<String>(String.class, v));
		return params;
	}

	
	public String toString() {
		return "Search for similar compounds by atom  environments";
	}
	
	public String getSQL() throws AmbitException {
		return String.format(sql_similarity,qe.toString(),qe.toString());
		
	}
	
}
