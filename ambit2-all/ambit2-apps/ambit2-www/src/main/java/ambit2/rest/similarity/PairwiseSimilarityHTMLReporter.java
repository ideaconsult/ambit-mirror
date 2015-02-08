package ambit2.rest.similarity;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.relation.SimilarityRelation;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.rest.DisplayMode;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.query.QueryResource;

public class PairwiseSimilarityHTMLReporter  extends QueryHTMLReporter<SimilarityRelation, IQueryRetrieval<SimilarityRelation>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	protected PairwiseSimilarityJSONReporter similarityJSONReporter;
	
	public PairwiseSimilarityHTMLReporter() {
		this(null,DisplayMode.table);
	}
	public PairwiseSimilarityHTMLReporter(Request request,DisplayMode _dmode) {
		this(request,request,_dmode);
	}
	public PairwiseSimilarityHTMLReporter(Request request,Request originalRef,DisplayMode _dmode) {
		super(request,_dmode);

	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		similarityJSONReporter = new PairwiseSimilarityJSONReporter(request);
		return similarityJSONReporter.getCmpReporter();
	}
	@Override
	public void setOutput(Writer output) throws Exception {
		super.setOutput(output);
		similarityJSONReporter.setOutput(output);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<SimilarityRelation> query) {

		super.header(w, query);
		try { 
			String page = "";
			Form form = uriReporter.getResourceRef().getQueryAsForm();
			try {

				page = form.getFirstValue("page");
			} catch (Exception x) {
				page = "0";
			}	
			String maxhits = "";
			try {

				maxhits = form.getFirstValue(QueryResource.max_hits);
			} catch (Exception x) {
				maxhits = "1000";
			}					
			String pagesize = "";
			try {

				pagesize = form.getFirstValue("pagesize");
			} catch (Exception x) {
				pagesize = maxhits; 
			}				
			output.write(String.format("<hr><div class='results'>&nbsp;<a href='#' title='' id='dataset'>Dataset</a> | %s&nbsp; | Download as %s&nbsp; | %s </div>%s ",
					String.format("<a href='%s' title='%s'>License</a>",getLicenseURI(),getLicenseURI()),
					downloadLinks(),
					"Order: <select id='order'></select>",
					String.format("<form method='GET' action=''>Page&nbsp;<input name='page' type='text' title='Page' size='10' value='%s'>&nbsp;"+
							"&nbsp;Page size<input name='pagesize' type='text' title='Page size' size='10' value='%s'><input type='image' src='%s/images/search.png' value='Refresh'></form>",
							page==null?"0":page,
							pagesize==null?"100":pagesize,
							uriReporter.getBaseReference())						
									));
						
			output.write("<div style='margin-top:20px;margin-left:50px;margin-right:50px;'><div id='matrix' style='float:left;'></div>");
			output.write("<div style='float:right;margin-top:20px;minWidth:250'><div class='ui-widget-header ui-corner-top'>Similarity / d Activity / SALI</div><div class='ui-widget-content ui-corner-bottom' id='simheader'></div><br/>");
			output.write("<div class='ui-widget-header ui-corner-top' id='cmp1header'></div><div class='ui-widget-content'><img src='#' id='cmp1' alt=''></div><br/><div class='ui-widget-header ui-corner-top' id='cmp2header'></div><div class='ui-widget-content'><img src='#' alt = '' id='cmp2'></div></div></div>");
			
			output.write("\n<script type='text/javascript'>var oSimilarityMatrix = \n");
			similarityJSONReporter.header(output, query);
		} catch (Exception x) {
			 
		}
	}
		
	@Override
	public void footer(Writer output, IQueryRetrieval<SimilarityRelation> query) {
		try { 
			similarityJSONReporter.footer(output, query);
			output.write(";</script>\n");
		
			output.write("<script type='text/javascript'>drawMatrix(oSimilarityMatrix,200);</script>");
		
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		} 
		super.footer(output, query);
	}
	@Override
	public Object processItem(SimilarityRelation item)
			throws AmbitException {
		try { 
			similarityJSONReporter.processItem(item);
		} catch (Exception x) {}
		return item;
	}
	
	protected void writeMoreColumns(ModelQueryResults model, Writer output) {
	}
	
	protected String downloadLinks() throws IOException {
		StringBuilder w = new StringBuilder();
		MediaType[] mimes = {
				MediaType.TEXT_URI_LIST,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_JSON
				};
		String[] image = {
				"link.png",
				"rdf.gif",
				"json.png"
		};
		String q=uriReporter.getResourceRef().getQuery();
		for (int i=0;i<mimes.length;i++) {
			MediaType mime = mimes[i];
			w.append("&nbsp;");
			w.append(String.format(
					"<a href=\"?%s%smedia=%s\"  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>",
					q==null?"":q,
					q==null?"":"&",
					Reference.encode(mime.toString()),
					uriReporter.getBaseReference().toString(),
					image[i],
					mime,
					mime));	

		}			
		return w.toString();
	}

}
