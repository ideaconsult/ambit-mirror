package ambit2.rest.similarity.space;

import java.io.IOException;
import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.db.search.structure.pairwise.ChemSpaceCell;
import ambit2.db.search.structure.pairwise.ChemicalSpaceQuery;
import ambit2.rest.DisplayMode;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.query.QueryResource;

public class ChemicalSpaceHTMLReporter  extends QueryHTMLReporter<ChemSpaceCell, IQueryRetrieval<ChemSpaceCell>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	protected ChemicalSpaceJSONReporter similarityJSONReporter;
	
	public ChemicalSpaceHTMLReporter(Request request,DisplayMode _dmode,ResourceDoc doc, ChemicalSpaceJSONReporter reporter) {
		this(request,request,_dmode,doc,reporter);
	}
	public ChemicalSpaceHTMLReporter(Request request,Request originalRef,DisplayMode _dmode,ResourceDoc doc, ChemicalSpaceJSONReporter reporter) {
		super(request,_dmode,doc);
		this.similarityJSONReporter = reporter;
		setUriReporter(similarityJSONReporter.getCmpReporter());
	}
	
	
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return null;
	}
	@Override
	public void setOutput(Writer output) throws Exception {
		super.setOutput(output);
		similarityJSONReporter.setOutput(output);
	}
	
	@Override
	public void header(Writer w, IQueryRetrieval<ChemSpaceCell> query) {

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
					String.format("Order: <select id='order'></select>&nbsp;Matrix cell color <select id='mcolor'></select>&nbsp;| Elapsed time %d ms.",System.currentTimeMillis() - getStartTime()),
					String.format("<form method='GET' action=''>&nbsp;Page&nbsp;<input name='page' type='text' title='Page' size='10' value='%s'>&nbsp;"+
							"&nbsp;Page size<input name='pagesize' type='text' title='Page size' size='10' value='%s'><input type='image' src='%s/images/search.png' value='Refresh'></form>",
							page==null?"0":page,
							pagesize==null?"100":pagesize,
							uriReporter.getBaseReference()				
									)
							));
						
			output.write("<div style='margin-top:20px;margin-left:50px;margin-right:50px;'><div style='float:left;width:760px'><div id='bar'><svg></svg></div><br><div id='matrix'></div></div>");
			output.write("<div style='float:right;margin-top:20px;minWidth:250'>");
			output.write("<div class='ui-widget-header ui-corner-top'>SAS Map</div><div class='ui-widget-content ui-corner-bottom' class='chartWrap' id='chart'><svg/>");
			output.write("<br/><label>X</label>&nbsp;<select id='xaxis'></select>");
			output.write("<br/><label>Y</label>&nbsp;<select id='yaxis'></select>");
			output.write("</div><br/>");
			output.write("<div  style='margin-top:250px' class='ui-widget-header ui-corner-top'>Selected cell</div><div class='ui-widget-content ui-corner-bottom' id='simheader'>Similarity&nbsp;<label id='sh_similarity'></label><br/>dActivity&nbsp;<label id='sh_dactivity'/><br/>Ratio&nbsp;<label id='sh_ratio'/><br/>Ridge&nbsp;<label id='sh_edge'/></div></div><br/>");
			/*
			output.write("<div class='ui-widget-header ui-corner-top' id='cmp1header'>Column</div><div class='ui-widget-content'><img src='#' id='cmp1' alt='' \"></div> ");
			output.write("<div class='ui-widget-header ui-corner-top' id='cmp2header'>Row</div><div class='ui-widget-content'><img src='#' id='cmp2' alt='' \"></div>");
			*/
			output.write("</div>");
			output.write("</div>");

			output.write("<div style='margin-left:0px;minWidth:200;margin-top:900px'>");
			output.write("<ol id='cmp2list' class='spacegrid'></ol>");
			output.write("</div>");		
			
			output.write("<div style='margin-left:0px;minWidth:200;margin-top:1150px'>");
			output.write("<ol id='cmp1list' class='spacegrid'></ol>");
			output.write("</div>");			
	
			/*//hide cmp div 
		String detailsDiv = details==null?"":
			String.format("<a href=\"#\" style=\"padding: 5px 10px;\" onClick=\"toggleDiv('%s'); return false;\">Details: %s</a><br><div style='display: none;' id='details'><p>%s</p></div>\n",
					"details",
					x.getMessage(),
					details);
			 */

			output.write("\n<script type='text/javascript'>var oSpace = \n");
			similarityJSONReporter.header(output, query);
		} catch (Exception x) {
			 x.printStackTrace();
		}
	}
		
	@Override
	public void footer(Writer output, IQueryRetrieval<ChemSpaceCell> query) {
		try { 
			similarityJSONReporter.footer(output, query);
			output.write(";</script>\n");
		
			
			output.write(String.format("<script type='text/javascript'>spaceMatrix(oSpace,200,'%s');</script>",
					query==null?"":((ChemicalSpaceQuery)query).getMethod().name()));

			output.write("<br><div class='results'>Elapsed time " + (System.currentTimeMillis() - getStartTime()) + " ms.</div>");
			
		} catch (Exception x) {
			x.printStackTrace();
		} 
		super.footer(output, query);
	}
	@Override
	public Object processItem(ChemSpaceCell item)
			throws AmbitException {
		try { 
			similarityJSONReporter.processItem(item);
		} catch (Exception x) {}
		return item;
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
