package ambit2.db.update.bookmark;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Bookmark;
import ambit2.db.update.AbstractObjectUpdate;

/**
 * Updates content of the model
 * @author nina
 *
 */
public class UpdateBookmark extends AbstractObjectUpdate<Bookmark>{

	public static final String update_sql = "update bookmark set recalls=?,hasTopic=?,title=?,description=?,date=now() where idbookmark=? and creator=?";

	public UpdateBookmark(Bookmark ref) {
		super(ref);
	}
	public UpdateBookmark() {
		this(null);
	}			
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getObject().getRecalls()));
		params.add(new QueryParam<String>(String.class, getObject().getHasTopic()));
		params.add(new QueryParam<String>(String.class, getObject().getTitle()));
		params.add(new QueryParam<String>(String.class, getObject().getDescription()==null?"":getObject().getDescription()));
		if (getObject().getId() > 0) 
			params.add(new QueryParam<Integer>(Integer.class, getObject().getId()));
		else
			throw new AmbitException("no bookmark specified");
		
		params.add(new QueryParam<String>(String.class, getObject().getCreator()));
		return params.size()==0?null:params;		
				
	}

	public String[] getSQL() throws AmbitException {
		if (getObject().getId() > 0) {
			return new String[] {String.format(update_sql,ReadBookmark.whereID)};
		}
		throw new AmbitException("no bookmark specified");
	}
	public void setID(int index, int id) {
			
	}
}