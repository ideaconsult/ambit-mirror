package ambit2.db.facets.bookmarks;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.Bookmark;
import ambit2.db.search.StringCondition;

public class BookmarksByTopicFacetQuery   extends AbstractFacetQuery<Bookmark,String,StringCondition,BookmarksByTopicFacet>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4741289697057206809L;
	//select hasTopic,creator,count(*) from bookmark where creator="nina" group by creator,hasTopic;
	protected String sql = "select hasTopic,count(idbookmark) from bookmark where creator=? group by creator,hasTopic";
	protected BookmarksByTopicFacet record;
	
	public BookmarksByTopicFacetQuery(String hasTopic) {
		super(hasTopic);
	}
	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(BookmarksByTopicFacet object) {
		return 1;
	}

	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if ((getFieldname()==null) || (getFieldname().getCreator()==null)) throw new AmbitException("No user name");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class,getFieldname().getCreator()));
		return params;
	}
	
	@Override
	protected BookmarksByTopicFacet createFacet(String facetURL) {
		return new BookmarksByTopicFacet();
	}

	@Override
	public BookmarksByTopicFacet getObject(ResultSet rs) throws AmbitException {
		if (record == null) {
			record = createFacet(null);
		}
		record.setCreator(getFieldname().getCreator());
		try {
			record.setValue(rs.getString(1));
			record.setCount(rs.getInt(2));
			return record;
		} catch (Exception x) {
			record.setValue(x.getMessage());
			record.setCount(-1);
			return record;
		}
	}
	
}
