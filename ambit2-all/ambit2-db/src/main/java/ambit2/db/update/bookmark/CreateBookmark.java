package ambit2.db.update.bookmark;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Bookmark;
import ambit2.db.update.AbstractObjectUpdate;

public class CreateBookmark extends AbstractObjectUpdate<Bookmark>{
	protected int sql_size = 1;
	public static final String create_sql = 
		"INSERT INTO bookmark (idbookmark,creator,recalls,hasTopic,title,description,created,date) values (?,?,?,?,?,?,NOW(),NOW()) " +
		"ON DUPLICATE KEY UPDATE recalls=values(recalls),hasTopic=values(hasTopic),title=values(title),description=values(description),date=now()"
	;
	
	public CreateBookmark(Bookmark ref) {
		super(ref);
	}
	public CreateBookmark() {
		this(null);
	}		

	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		
		params1.add(new QueryParam<Integer>(Integer.class, (getObject().getId()>0)?getObject().getId():null));
		params1.add(new QueryParam<String>(String.class, getObject().getCreator()));
		params1.add(new QueryParam<String>(String.class, getObject().getRecalls()));
		params1.add(new QueryParam<String>(String.class, getObject().getHasTopic()));
		params1.add(new QueryParam<String>(String.class, getObject().getTitle()));
		params1.add(new QueryParam<String>(String.class, getObject().getDescription()==null?"":getObject().getDescription()));

		return params1;
		
	}

	public String[] getSQL() throws AmbitException {
		return new String[] {create_sql};
	}
	public void setID(int index, int id) {
		if (index== (sql_size-1))
			getObject().setId(id);
	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}
}
