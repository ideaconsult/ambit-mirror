package ambit2.rest;

import java.io.IOException;
import java.io.Writer;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.restlet.Request;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryHeaderReporter;

/**
 * HTML generation for structure queries
 * @author nina
 *
 * @param <Q>
 */
public abstract class QueryStructureHTMLReporter<Q extends IQueryRetrieval<IStructureRecord>>  extends QueryHeaderReporter<Q,Writer>  {
	protected QueryURIReporter uriReporter;
	protected String prefix ;
	protected boolean headless=false;
	
	public boolean isHeadless() {
		return headless;
	}
	public void setHeadless(boolean headless) {
		this.headless = headless;
	}
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
	public QueryStructureHTMLReporter(ResourceDoc doc,boolean headless) {
		this(null, DisplayMode.table,doc,headless);
	}
	public QueryStructureHTMLReporter(Request request,  DisplayMode _dmode,ResourceDoc doc,boolean headless) {
		this("", request, _dmode, doc,headless);
	}
	public QueryStructureHTMLReporter(String prefix,Request request, DisplayMode _dmode,ResourceDoc doc,boolean headless) {
		super();
		this.headless = headless;
		this.prefix = prefix;
		uriReporter =  createURIReporter(request,doc);
		this._dmode = _dmode;
		processors.clear();
		/*
		ValuesReader valuesReader = new ValuesReader();
		Profile profile = new Profile<Property>();
		profile.add(Property.getCASInstance());
		valuesReader.setProfile(profile);
		processors.add(valuesReader);
		*/
		processors.add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});
		
	}	
	protected abstract QueryURIReporter createURIReporter(Request request,ResourceDoc doc);
	
	@Override
	public void header(Writer w, Q query) {
		try {
			if (headless) return;
			AmbitResource.writeHTMLHeader(w,query.toString(),uriReporter.getRequest(),
					uriReporter.getResourceRef(),uriReporter.getDocumentation());
		} catch (IOException x) {}
	}
	
	@Override
	public void footer(Writer output, Q query) {
		try {
			if (headless) return;
			AmbitResource.writeHTMLFooter(output,query.toString(),uriReporter.getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
		
	}

	public void open() throws DbAmbitException {
		
	}	
	protected boolean isCollapsed() {
		return DisplayMode.table==_dmode;
	}
}