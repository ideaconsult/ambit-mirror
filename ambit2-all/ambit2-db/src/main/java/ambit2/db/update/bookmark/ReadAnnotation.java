package ambit2.db.update.bookmark;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Bookmark;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.StringCondition;

public class ReadAnnotation extends AbstractQuery<String, String, StringCondition, Bookmark> implements
	IQueryRetrieval<Bookmark> {
	boolean queryExpansion = true;
	public boolean isQueryExpansion() {
		return queryExpansion;
	}

	public void setQueryExpansion(boolean queryExpansion) {
		this.queryExpansion = queryExpansion;
	}

	private static final String sql_qe = " with query expansion ";
    // "select idbookmark,creator,recalls,hasTopic,title,description,created,date from bookmark %s order by date desc";
    protected static final String sql = 
	    "SELECT null,b1.s_source,b1.s_id,b1.o_id,b2.label,b3.label,b1.label,b1.relation,match (b1.s_id,b1.o_id,b1.label) against (? %s) as relevance,hex(b1.uuid) as huuid\n"
	    + "FROM ontobucket b1  left join\n"
	    + "(select label,s_id from ontobucket where relation!='subclass' limit 1) b2 on b1.s_id = b2.s_id\n"
	    + "left join (select label,s_id from ontobucket where relation!='subclass' limit 1) b3 on b1.o_id = b3.s_id\n"
	    + "where match (b1.s_id,b1.o_id,b1.label) against (? %s)";
 
    		
    protected static final String sql_by_relation = String.format("%s and relation=?", sql);
    
    protected static final String sql_labelsonly = 
	    "SELECT null,b1.s_source,b1.s_id,b1.o_id,b1.label,null,null,b1.relation,match (b1.s_id,b1.o_id,b1.label) against (? %s) as relevance,hex(b1.uuid) as huuid\n"
	    + "FROM ontobucket b1\n"
	    + "where match (b1.s_id,b1.o_id,b1.label) against (? %s)";
         
    protected static final String sql_labelsonly_by_relation = String.format("%s and relation=?", sql_labelsonly);
    /**
	 * 
	 */
    private static final long serialVersionUID = -8595708937489941431L;

    public ReadAnnotation(String string) {
	super();
	setCondition(StringCondition.getInstance(StringCondition.C_EQ));
	setFieldname(string);
    }

    public double calculateMetric(Bookmark object) {
	return 1;
    }

    public boolean isPrescreen() {
	return false;
    }

    public List<QueryParam> getParameters() throws AmbitException {
	List<QueryParam> params = new ArrayList<QueryParam>();
	if (getFieldname() != null) {
	    params.add(new QueryParam<String>(String.class, getFieldname()));
	    params.add(new QueryParam<String>(String.class, getFieldname()));
	    if (getValue() != null)
		params.add(new QueryParam<String>(String.class, getValue()));
	} else
	    throw new AmbitException("No search query");
	return params;
    }

    public String getSQL() throws AmbitException {
	if (getValue() == null)
	    return String.format(sql_labelsonly,queryExpansion?sql_qe:"",queryExpansion?sql_qe:"");
	else
	    return String.format(sql_labelsonly_by_relation,queryExpansion?sql_qe:"",queryExpansion?sql_qe:"");

    }

    public Bookmark getObject(ResultSet rs) throws AmbitException {
	try {
	    Bookmark q = new Bookmark();
	    q.setId(rs.getInt(1));
	    q.setCreator(rs.getString(2));
	    q.setRecalls(rs.getString(3));
	    q.setHasTopic(rs.getString(4));
	    try {
		if (rs.getString(7) != null)
		    q.setTitle(rs.getString(7).trim());
	    } catch (Exception x) {
	    }
	    try {
		if ((q.getTitle() == null) && (rs.getString(5) != null))
		    q.setTitle(rs.getString(5).trim());
	    } catch (Exception x) {
	    }
	    try {
		q.setDescription(rs.getString(6).trim());
	    } catch (Exception x) {
	    }
	    try {
		q.setType(rs.getString(8).trim());
	    } catch (Exception x) {
	    }
	    try {
		q.setRelevance(rs.getDouble(9));
	    } catch (Exception x) {
	    }
	    try {
	    	q.setUuid(rs.getString("huuid").trim());
	    } catch (Exception x) {
	    	q.setUuid(null);
	    }
	    return q;
	} catch (Exception x) {
	    return null;
	}
    }

    @Override
    public String toString() {
	return getFieldname() == null ? "Annotation" : getFieldname();
    }
}