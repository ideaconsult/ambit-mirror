package ambit2.rest;

import java.io.IOException;
import java.io.Writer;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.restlet.Request;

import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

/**
 * Generates HTML representation of a resource
 * @author nina
 *
 * @param <T>
 * @param <Q>
 */
public abstract class QueryHTMLReporter<T,Q extends IQueryRetrieval<T>>  extends QueryReporter<T,Q,Writer>  {
	protected QueryURIReporter uriReporter;
	protected boolean headless = false;

	
	public QueryURIReporter getUriReporter() {
		return uriReporter;
	}
	public void setUriReporter(QueryURIReporter uriReporter) {
		this.uriReporter = uriReporter;
	}

	protected DisplayMode _dmode = DisplayMode.table;
	/**
	 * 
	 */
	private static final long serialVersionUID = 16821411854045588L;
	
	/**
	 * 
	 */
	public QueryHTMLReporter() {
		this(null,DisplayMode.table,null,false);
	}
	public QueryHTMLReporter(Request request, DisplayMode _dmode,ResourceDoc doc) {
		this(request,_dmode,doc,false);
	}
	public QueryHTMLReporter(Request request, DisplayMode _dmode,ResourceDoc doc,boolean headless) {
		super();
		this.headless = headless;
		uriReporter =  createURIReporter(request, doc);
		this._dmode = _dmode;
		processors.clear();
		/*
		ValuesReader valuesReader = new ValuesReader();
		Profile profile = new Profile<Property>();
		profile.add(Property.getCASInstance());
		valuesReader.setProfile(profile);
		processors.add(valuesReader);
		*/
		processors.add(new DefaultAmbitProcessor<T,T>() {
			public T process(T target) throws AmbitException {
				processItem(target);
				return target;
			};
		});
		
	}	
	protected abstract QueryURIReporter createURIReporter(Request request, ResourceDoc doc);
	
	@Override
	public void header(Writer w, Q query) {
		try {
			if (!headless) {
				AmbitResource.writeHTMLHeader(w,query==null?"":query.toString(),uriReporter.getRequest(),
						getUriReporter().getResourceRef(),
						getUriReporter()==null?null:getUriReporter().getDocumentation());
				//output.write(AmbitResource.printWidgetHeader(query.toString()));
				//output.write(AmbitResource.printWidgetContentHeader(""));
				output.write("<p>");			
			}
		} catch (IOException x) {}
	}
	
	@Override
	public void footer(Writer output, Q query) {
		try {
			if (!headless) {
				output.write("</p>");
				//output.write(AmbitResource.printWidgetContentFooter());
				//output.write(AmbitResource.printWidgetFooter());		
			
				AmbitResource.writeHTMLFooter(output,query.toString(),uriReporter.getRequest());
			}
			output.flush();
		} catch (Exception x) {
			
		}
		
	}

	public void open() throws DbAmbitException {
		
	}	
}
