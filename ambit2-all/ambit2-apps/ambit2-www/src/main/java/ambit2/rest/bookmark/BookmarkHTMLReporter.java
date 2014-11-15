package ambit2.rest.bookmark;

import java.io.Writer;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.Bookmark;
import ambit2.db.update.bookmark.ReadBookmark;
import ambit2.rest.DisplayMode;
import ambit2.rest.QueryHTMLReporter;

public class BookmarkHTMLReporter extends QueryHTMLReporter<Bookmark, IQueryRetrieval<Bookmark>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;

	public BookmarkHTMLReporter() {
		this(null,DisplayMode.table,null);
	}
	public BookmarkHTMLReporter(Request baseRef, DisplayMode _dmode, ResourceDoc doc) {
		super(baseRef,_dmode,doc);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new BookmarkURIReporter<IQueryRetrieval<Bookmark>>(request);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<Bookmark> query) {
		super.header(w, query);
		try {
			//w.write(String.format("<h3>Bookmarks </h3>",""));
			
			Reference ref = getUriReporter().getResourceRef().clone();
			ref.setQuery(null);
			
			Bookmark bm = null;
			if (query instanceof ReadBookmark)
				bm = ((ReadBookmark)query).getFieldname();
			//rdfa
			output.write("<div xmlns:dc=\"http://purl.org/dc/elements/1.1/\">");
			output.write("<div xmlns:a=\"http://www.w3.org/2000/10/annotation-ns#\">");
			w.write("<table class='datatable' width='99%'>");
			w.write("<caption><h3>Bookmarks</h3>");
			w.write(String.format("<a href='%s/topics'>Topics</a> | ",ref));
			w.write(String.format("<a href='?media=%s'>RDF/XML</a> |",URLEncoder.encode(MediaType.APPLICATION_RDF_XML.toString())));
			w.write(String.format("<a href='?media=%s'>RDF N3</a> |",URLEncoder.encode(MediaType.TEXT_RDF_N3.toString())));
			w.write(String.format("<a href='?media=%s'>CSV</a>",URLEncoder.encode(MediaType.TEXT_CSV.toString())));
			w.write("</caption>");
			w.write("<thead>");
			w.write("<tr ><form action='' method='get'>");
			w.write("<th>Bookmark URI</th>");
			w.write(String.format("<th>Topic <input type='text' name='hasTopic' value='%s' type='text'/></th>",
					bm==null?"":bm.getHasTopic()==null?"":bm.getHasTopic()));
			w.write(String.format("<th>Title <input type='text' name='search' value='%s' type='text'/></th>",
					bm==null?"":bm.getTitle()==null?"":bm.getTitle()));
			w.write(String.format("<th>URI <input type='text' name='recalls' value='%s' type='text'/></th>",
					bm==null?"":bm.getRecalls()==null?"":bm.getRecalls()));
			w.write("<th>Creator <input type='text' name='creator' type='text'/></th>");
			w.write("<th>Description <input type='submit' value='search'/></th>");
			w.write("<th>Created</th>");
			w.write("</form>");
			w.write("</tr></thead>");
			w.write("<tbody>");

		} catch (Exception x) {}
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<Bookmark> query) {
		try {
			output.write("</tbody></table>");
		} catch (Exception x) {}
		super.footer(output, query);
	}
	@Override
	public Object processItem(Bookmark item) throws AmbitException  {
		try {
			String uri = uriReporter.getURI(item);

			output.write(String.format("<tr><div about='%s'>",uri));

			output.write(String.format(
						"<td><a href=\"%s\"><span property=\"dc:title\">%s</span></a></td>",
						uriReporter.getURI(item),
						uriReporter.getURI(item)));
			
			output.write(String.format(
					"<td><div><span property=\"a:hasTopic\"><a href='?hasTopic=%s'>%s</a></span></div></td>",
					Reference.encode(item.getHasTopic()),
					item.getHasTopic()));	
			
			output.write(String.format(
					"<td><div><span property=\"dc:title\"><a href='?search=%s'>%s</a></span></div></td>",
					Reference.encode(item.getTitle()),
					item.getTitle()				
					));					
		
			
			output.write(String.format(
						"<td><a href=\"%s\" target='_blank'><span property=\"a:recalls\">%s</span></a></td>",
						item.getRecalls(),
						item.getRecalls()));	


			output.write(String.format(
					"<td>%s</td>",
					item.getCreator()));	
			
			
			output.write(String.format(
					"<td><div><span property=\"dc:description\">%s</span></div></td>",
					item.getDescription()));			
			
			output.write(String.format(
					"<td>%s</td>",
					SimpleDateFormat.getInstance().format(new Date(item.getCreated()))));				
				
				output.write("</div><tr>");
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		} finally {
			
		}
		return null;
	}

}