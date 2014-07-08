package ambit2.db.update.bookmark;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Bookmark;
import ambit2.db.update.AbstractObjectUpdate;

/**
 * deletes bookmark
 * @author nina
 *
 */
public class DeleteBookmark extends AbstractObjectUpdate<Bookmark> {

	public static final String delete_sql = "delete from bookmark where idbookmark=?";

	public DeleteBookmark(Bookmark ref) {
		super(ref);
	}
	public DeleteBookmark() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getObject().getId() > 0) 
			params.add(new QueryParam<Integer>(Integer.class, getObject().getId()));

		else
			throw new AmbitException("no bookmark specified");
		return params.size()==0?null:params;		
		
	}

	public String[] getSQL() throws AmbitException {
		if (getObject().getId() > 0) {
			return new String[] {String.format(delete_sql,ReadBookmark.whereID)};
		}
		throw new AmbitException("no bookmark specified");
	}
	public void setID(int index, int id) {
			
	}
}