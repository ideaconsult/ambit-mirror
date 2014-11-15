package ambit2.rest.bookmark;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;

import ambit2.base.data.Bookmark;

public class BookmarkURIReporter <Q extends IQueryRetrieval<Bookmark>> extends QueryURIReporter<Bookmark, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868430033131766579L;
	public BookmarkURIReporter(Request baseRef) {
		super(baseRef,null);
	}

	@Override
	public String getURI(String ref, Bookmark item) {
		return String.format("%s%s/%s/entries/%d",ref,BookmarkResource.resource,item.getCreator(),item.getId());
	}

}