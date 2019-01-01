package ambit2.rest.routers.misc;

import org.restlet.Context;

import ambit2.rest.bookmark.BookmarkResource;
import ambit2.rest.bookmark.BookmarkTopicsResource;
import ambit2.rest.routers.MyRouter;

/**
 * Resource /bookmark
 */
public class BookmarksRouter extends MyRouter {

	public BookmarksRouter(Context context) {
		super(context);
		init();
	}
	

	protected void init() {
		attach(String.format("/{%s}",BookmarkResource.creator),BookmarkResource.class);

		attach(String.format("/{%s}/topics",
				BookmarkResource.creator),BookmarkTopicsResource.class);
		
		attach(String.format("/{%s}/entries",BookmarkResource.creator),BookmarkResource.class);
		attach(String.format("/{%s}/entries/{%s}",
				BookmarkResource.creator,
				BookmarkResource.idbookmark),BookmarkResource.class);	
	}

}
