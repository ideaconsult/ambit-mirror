package ambit2.rest.structure;

import java.awt.Dimension;
import java.io.Writer;

import org.restlet.Request;

import ambit2.base.data.Profile;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.DisplayMode;
import ambit2.rest.QueryStructureHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;

public class CompoundHTMLbyJSONReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryStructureHTMLReporter<Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;

	protected CompoundJSONReporter<IQueryRetrieval<IStructureRecord>> cmp_reporter;
	
	public CompoundHTMLbyJSONReporter(String prefix, Request request,ResourceDoc doc,DisplayMode _dmode,
			QueryURIReporter urireporter,
			Template template,Profile groupedProperties,Dimension d,String urlPrefix) {
		super(request,_dmode,doc,false);
		cmp_reporter = new CompoundJSONReporter<IQueryRetrieval<IStructureRecord>>(template,groupedProperties,request,doc,urlPrefix);
		processors = cmp_reporter.getProcessors();
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new CompoundURIReporter(request,doc);
	}
	@Override
	public void setOutput(Writer output) throws AmbitException {
		super.setOutput(output);
		cmp_reporter.setOutput(output);
	}
	@Override
	public void header(Writer w, Q query) {
		super.header(w, query);
		try { 
			output.write("<table class='compoundtable' id='model' width='100%'></table>");
			output.write("\n<script type=\"text/javascript\">cmpArray = \n");
			cmp_reporter.header(w, query);
		} catch (Exception x) {} 
	
	}
	@Override
	public void footer(Writer output, Q query) {
		try { 
			cmp_reporter.footer(output, query);
			output.write(";</script>\n");
		
		} catch (Exception x) {
			x.printStackTrace();
		} 
		super.footer(output, query);
	}
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {

		try { 
			cmp_reporter.processItem(item);
		} catch (Exception x) {}
		return item;
	}
	
}