package ambit2.db.search;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.structure.ScopeQuery;

/**
 * Set of {@link IQueryObject}, combined with logical "and or logical "or".
 * 
 * @author nina
 * 
 * @param <Q>
 */
public abstract class QueryCombined<T> implements IQueryRetrieval<T> {
    /**
     * 
     */
    private static final long serialVersionUID = -2233255580366294449L;
    protected ArrayList<IQueryRetrieval<T>> queries;
    protected Integer id = null;
    protected static final String union = "\nunion\n";
    protected static final String join = "\njoin\n";
    protected IQueryObject<T> scope;

    public enum COMBINE {
	AND {
	    @Override
	    public String toString() {
		return "All queries";
	    }
	},
	OR {
	    @Override
	    public String toString() {
		return "Any query";
	    }
	}
    };

    protected COMBINE combine_queries = COMBINE.AND;

    public COMBINE getCombine_queries() {
	return combine_queries;
    }

    public void setCombine_queries(COMBINE combine_queries) {
	this.combine_queries = combine_queries;
    }

    public List<IQueryRetrieval<T>> getQueries() {
	return queries;
    }

    public Integer getId() {
	return id;
    }

    public QueryCombined() {
	super();
	queries = new ArrayList<IQueryRetrieval<T>>();
    }

    public void setId(Integer id) {
	this.id = id;
	for (IQueryObject<T> q : queries)
	    q.setId(id);
	if (getScope() != null)
	    getScope().setId(id);
    }

    public String getSQL() throws AmbitException {
	if (queries.size() == 0)
	    throw new AmbitException("Undefined query");
	else if ((scope == null) && (queries.size() == 1))
	    return queries.get(0).getSQL();
	else if (isCombine_as_and())
	    return getSQL_and();
	else
	    return getSQL_or();
    }

    /**
     * SQLs of simple queries, union-ed. Example:
     * 
     * <pre>
     * select ?,idstructure,1 from structure where idstructure <= ?
     * union
     * select ?,idstructure,1 from structure where idstructure between ? and ?
     * </pre>
     * 
     * @return sql string
     * @throws AmbitException
     */
    protected String getSQL_or() throws AmbitException {
	StringBuffer b = new StringBuffer();
	String c = "";
	for (IQueryObject<T> q : queries) {
	    b.append(c);
	    b.append(q.getSQL());
	    c = union;
	}
	if (scope == null) {
	    b.append(groupBy());
	    return b.toString();
	} else {
	    StringBuffer bb = new StringBuffer();
	    c = getScopeSQL();
	    bb.append(c);
	    join(scope, "SCOPE", bb);
	    bb.append(join);
	    bb.append("(\n");
	    bb.append(b.toString());
	    bb.append(")\n");
	    bb.append("as U\n");
	    bb.append(String.format("\nusing (%s)", joinOn()));
	    b.append(groupBy());
	    return bb.toString();
	}
    }

    protected abstract String joinOn();

    protected String groupBy() {
	return "";
    }

    protected String getMainSQL() {
	return "select Q1.idquery,s.idchemical,s.idstructure,Q1.selected as selected,Q1.metric as metric,Q1.text from structure as s";
    }

    /**
     * SQLs of simple queries, join-ed
     * 
     * @return sql string
     * @throws AmbitException
     */
    protected String getSQL_and() throws AmbitException {
	StringBuffer b = new StringBuffer();
	/**
	 * select Q1.id,idstructure,1 from structure as S join (select 1 as
	 * id,idstructure,1 from structure as S where idstructure <= 180) as Q1
	 * using(idstructure) join (select 1 as id,idstructure,1 from structure
	 * where idstructure between 150 and 200) as Q2 using(idstructure)
	 */
	String c = getMainSQL();
	b.append(c);
	if (scope != null) {
	    join(scope, "SCOPE", b);
	}
	for (int i = 0; i < queries.size(); i++) {
	    IQueryObject q = queries.get(i);
	    join(q, Integer.toString(i + 1), b);
	}
	b.append(groupBy());
	return b.toString();
    }

    protected abstract String getScopeSQL();

    protected void join(IQueryObject<T> q, String qname, StringBuffer b) throws AmbitException {
	b.append(join);
	b.append("(");
	b.append(q.getSQL());
	b.append(")\n");
	b.append("as Q");
	b.append(qname);
	b.append(String.format("\nusing (%s)", joinOn()));

    }

    /**
     * SQL Parameters
     */
    public List<QueryParam> getParameters() throws AmbitException {
	setId(getId());
	ArrayList<QueryParam> param = new ArrayList<QueryParam>();
	if (scope != null) {
	    List<QueryParam> p = scope.getParameters();
	    if (p != null)
		for (int j = 0; j < p.size(); j++) {
		    QueryParam qp = p.get(j);
		    param.add(qp);
		}
	}
	for (int i = 0; i < queries.size(); i++) {
	    IQueryObject<T> q = queries.get(i);
	    List<QueryParam> p = q.getParameters();
	    for (int j = 0; j < p.size(); j++) {
		QueryParam qp = p.get(j);
		param.add(qp);
	    }
	}

	return param;
    }

    public boolean isCombine_as_and() {
	return combine_queries.equals(COMBINE.AND);
    }

    public void setCombine_as_and(boolean combine_as_and) {
	combine_queries = (combine_as_and) ? COMBINE.AND : COMBINE.OR;
    }

    public IQueryObject<T> getScope() {
	return scope;
    }

    public void setScope(IQueryObject<T> scope) {
	if (scope instanceof QueryCombined)
	    return;
	if (scope instanceof ScopeQuery) {
	    this.scope = (IQueryObject) ((ScopeQuery) scope).getValue();
	} else
	    this.scope = scope;
    }

    public void add(IQueryRetrieval<T> query) {
	queries.add(query);
    }

    public IQueryRetrieval<T> get(int index) {
	return queries.get(index);
    }

    public int size() {
	return queries.size();
    }

    public T getObject(ResultSet rs) throws AmbitException {
	if (queries.size() > 0)
	    return queries.get(0).getObject(rs);
	else
	    return null;
    }

    public boolean isPrescreen() {
	for (IQueryRetrieval q : queries)
	    if (q.isPrescreen())
		return true;
	return false;
    }

    public double calculateMetric(T object) {
	for (IQueryRetrieval q : queries)
	    if (q.isPrescreen()) {
		double metric = q.calculateMetric(object);
		if (metric > 0)
		    return metric;
	    }
	return 0;
    }

    @Override
    public String toString() {
	StringBuilder b = new StringBuilder();
	for (IQueryObject<T> q : queries) {
	    b.append(q.toString());
	    b.append(',');
	}
	if (scope != null) {
	    b.append(" within ");
	    b.append(scope.toString());
	}
	return b.toString();
    }

    public String getKey() {
	return null;
    }

    public String getCategory() {
	return null;
    }

    @Override
    public boolean supportsPaging() {
	return true;
    }
}
