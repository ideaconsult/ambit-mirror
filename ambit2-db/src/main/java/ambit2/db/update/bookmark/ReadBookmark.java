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

/**
 * Bookmark
 * @author nina
 *
 */
public class ReadBookmark  extends AbstractQuery<Bookmark, Integer, StringCondition, Bookmark>  implements IQueryRetrieval<Bookmark> {
	protected static String sql = 
		"select idbookmark,creator,recalls,hasTopic,title,description,created,date from bookmark %s order by date desc";
	
	protected static String whereID = " idbookmark = ? ";
	protected static String whereUser = " creator = substr(?,1,45) ";
	protected static String whereLabel = " title = substr(?,1,45) ";
	protected static String whereType = " hasTopic = substr(?,1,255) ";
	protected static String whereRecalls = " recalls = ? ";
	/**
	 * 
	 */
	private static final long serialVersionUID = -8595708937489941431L;

	public ReadBookmark(Integer id) {
		this(id,null);
	}
	public ReadBookmark(Integer id,Bookmark bookmark) {
		super();
		setValue(id);
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setFieldname(bookmark);	
	}
	public ReadBookmark() {
		this(null);
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
			if (getFieldname().getCreator()!=null)
				params.add(new QueryParam<String>(String.class, getFieldname().getCreator()));
			if (getFieldname().getHasTopic()!=null)
				params.add(new QueryParam<String>(String.class, getFieldname().getHasTopic()));
			if (getFieldname().getTitle()!=null)
				params.add(new QueryParam<String>(String.class, getFieldname().getTitle()));
			if (getFieldname().getRecalls()!=null)
				params.add(new QueryParam<String>(String.class, getFieldname().getRecalls()));				
		}
		
		if (getValue()!=null) 
			params.add(new QueryParam<Integer>(Integer.class, getValue()));

		return params.size()==0?null:params;
	}

	public String getSQL() throws AmbitException {
		String where = "where";
		StringBuilder b = new StringBuilder();
		
		if (getFieldname()!= null) {

			if (getFieldname().getCreator() != null) {
				b.append(where);
				b.append(whereUser);
				where = " and ";
			}
			if (getFieldname().getHasTopic()!= null) {
				b.append(where);
				b.append(whereType);
				where = " and ";
			}		
			if (getFieldname().getTitle()!= null) {
				b.append(where);
				b.append(whereLabel);
				where = " and ";
			}	
			if (getFieldname().getRecalls()!= null) {
				b.append(where);
				b.append(whereRecalls);
				where = " and ";
			}					
		}
	
		if (getValue()!= null) {
			b.append(where);
			b.append(whereID);
		}
		return String.format(sql,b.toString());
	}

	public Bookmark getObject(ResultSet rs) throws AmbitException {
		try {
			Bookmark q = new Bookmark();
			q.setId(rs.getInt(1));
			q.setCreator(rs.getString(2));
			q.setRecalls(rs.getString(3));
			q.setHasTopic(rs.getString(4));
			q.setTitle(rs.getString(5));
			q.setDescription(rs.getString(6));
			q.setCreated(rs.getTimestamp(7).getTime());
			q.setDate(rs.getTime(8).getTime());
			
			return q;
		} catch (Exception x) {
			return null;
		}
	}
	@Override
	public String toString() {
		return getValue()==null?"Bookmarks":String.format("Bookmark id=%d",getValue());
	}
}
