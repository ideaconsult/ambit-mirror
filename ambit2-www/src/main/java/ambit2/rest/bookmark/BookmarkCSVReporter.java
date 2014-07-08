package ambit2.rest.bookmark;

import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.restlet.Request;

import ambit2.base.data.Bookmark;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;

public class BookmarkCSVReporter<Q extends IQueryRetrieval<Bookmark>> extends QueryReporter<Bookmark, Q, Writer>  {
	protected QueryURIReporter<Bookmark, IQueryRetrieval<Bookmark>> uriReporter;
	public BookmarkCSVReporter(Request baseRef,ResourceDoc doc) {
		super();
		uriReporter = new BookmarkURIReporter<IQueryRetrieval<Bookmark>>(baseRef,null);
	}
	@Override
	public void open() throws DbAmbitException {
		
	}

	@Override
	public void header(Writer output, Q query) {
		try {
		output.write("URI,Title,Description,Topic,Recalls,Created,Creator\n");
		} catch (Exception x) {}
		
	}

	@Override
	public void footer(Writer output, Q query) {
		
	}

	@Override
	public Object processItem(Bookmark item) throws AmbitException {
		try {
			output.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
					uriReporter.getURI(item),
					item.getTitle(),
					item.getDescription(),
					item.getHasTopic(),
					item.getRecalls(),
					item.getCreated(),
					item.getCreator()
					));
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		return item;
	}

}
