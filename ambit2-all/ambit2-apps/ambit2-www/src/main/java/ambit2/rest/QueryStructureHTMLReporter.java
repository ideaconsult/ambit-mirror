package ambit2.rest;

import java.io.IOException;
import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.reporters.QueryHeaderReporter;

/**
 * HTML generation for structure queries
 * 
 * @author nina
 * 
 * @param <Q>
 */
public abstract class QueryStructureHTMLReporter<Q extends IQueryRetrieval<IStructureRecord>> extends
	QueryHeaderReporter<Q, Writer> {
    protected QueryURIReporter uriReporter;
    protected String prefix;
    protected boolean headless = false;

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
    public QueryStructureHTMLReporter(boolean headless) {
	this(null, DisplayMode.table, headless);
    }

    public QueryStructureHTMLReporter(Request request, DisplayMode _dmode, boolean headless) {
	this("", request, _dmode, headless);
    }

    public QueryStructureHTMLReporter(String prefix, Request request, DisplayMode _dmode, boolean headless) {
	super();
	this.headless = headless;
	this.prefix = prefix;
	uriReporter = createURIReporter(request, null);
	this._dmode = _dmode;
	processors.clear();
	/*
	 * ValuesReader valuesReader = new ValuesReader(); Profile profile = new
	 * Profile<Property>(); profile.add(Property.getCASInstance());
	 * valuesReader.setProfile(profile); processors.add(valuesReader);
	 */
	processors.add(new DefaultAmbitProcessor<IStructureRecord, IStructureRecord>() {
	    /**
		     * 
		     */
	    private static final long serialVersionUID = -6258307658499487505L;

	    public IStructureRecord process(IStructureRecord target) throws Exception {
		processItem(target);
		return target;
	    };
	});

    }

    protected abstract QueryURIReporter createURIReporter(Request request, ResourceDoc doc);

    @Override
    public void header(Writer w, Q query) {
	try {
	    if (headless)
		return;
	    AmbitResource.writeHTMLHeader(w, query.toString(), uriReporter.getRequest(), null);
	} catch (IOException x) {
	}
    }

    @Override
    public void footer(Writer output, Q query) {
	try {
	    if (headless)
		return;
	    AmbitResource.writeHTMLFooter(output, query.toString(), uriReporter.getRequest());
	    output.flush();
	} catch (Exception x) {

	}

    }

    public void open() throws DbAmbitException {

    }

    protected boolean isCollapsed() {
	return DisplayMode.table == _dmode;
    }
}