package ambit2.rest.structure;

import java.awt.Dimension;
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

import ambit2.base.data.Profile;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.AmbitResource;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DisplayMode;
import ambit2.rest.QueryStructureHTMLReporter;
import ambit2.rest.query.QueryResource;

public class CompoundHTMLbyJSONReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryStructureHTMLReporter<Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	boolean hierarchy = false;
	protected Dimension cellSize = new Dimension(150,150);
	
	protected CompoundJSONReporter<IQueryRetrieval<IStructureRecord>> cmp_reporter;
	
	public CompoundHTMLbyJSONReporter(String prefix, Request request,DisplayMode _dmode,
			QueryURIReporter urireporter,
			Template template,Profile groupedProperties,Dimension d,String urlPrefix) {
		super(request,_dmode,null,false);
		cmp_reporter = new CompoundJSONReporter<IQueryRetrieval<IStructureRecord>>(template,groupedProperties,null,null,request,urlPrefix,false,null);
		processors = cmp_reporter.getProcessors();
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new CompoundURIReporter(request,doc);
	}
	@Override
	public void setOutput(Writer output) throws Exception {
		super.setOutput(output);
		cmp_reporter.setOutput(output);
	}
	@Override
	public void header(Writer w, Q query) {
		oldHeader(w, query);
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
			output.write(String.format("<hr><div class='results'><a href='%s' title='%s'>This dataset</a> | %s&nbsp; "+
					"<span class='results'>| <b>Show:</b>  %s | %s | %s | %s | %s | %s | %s | %s | %s |</span><br>Download as %s | %s</div>%s",
					uriReporter.getResourceRef(),
					query.toString(),
					String.format("<a href='%s' title='%s'>License</a>",getLicenseURI(),getLicenseURI()),
					/*
						showRegistry,registry
						showSMILES,smiles
						showInChI,inchi
						showProperties,property
						showEndpoints,endpoint
						showCalculated,calculated
						showNames,name
					 */
					"<input type='checkbox' id='imagesInTable' title='Show/Hide structure diagrams in table' onchange='toggleImagesInTable(event)' checked/>Structure diagrams&nbsp;",
					"<input type='checkbox' id='visShowRegistry' title='Show/Hide registry numbers' onchange='showRegistry(event)' checked/>Identifiers&nbsp;",
					"<input type='checkbox' id='visNames' title='Show/Hide chemical names' onchange='showNames(event)' checked/>Names&nbsp;",
					"<input type='checkbox' id='visSMILES' title='Show/Hide SMILES' onchange='showSMILES(event)' />SMILES&nbsp;",
					"<input type='checkbox' id='visInChI' title='Show/Hide InChI' onchange='showInChI(event)'/>InChI&nbsp;",
					"<input type='checkbox' id='visEndpoints' title='Show/Hide endpoint values' onchange='showEndpoints(event)' checked/>Endpoints&nbsp;",
					"<input type='checkbox' id='visCalculated' title='Show/Hide calculated values' onchange='showCalculated(event)' checked/>Calculated&nbsp;",
					"<input type='checkbox' id='visProperties' title='Show/Hide all properties' onchange='showProperties(event)' />Properties&nbsp;",
					"<input type='checkbox' id='visSimilarity' title='Show/Hide the similaritymetric' onchange='showSimilarity(event)' />Similarity&nbsp;",
					downloadLinks(),
					cmp_reporter.getHilightPredictions()==null?"":String.format("Atoms highlighted by the model <a href=%s target=_blank>%s</a></h4></span></div>",cmp_reporter.getHilightPredictions(),cmp_reporter.getHilightPredictions()),
					String.format("<form method='GET' action=''>Page&nbsp;<input name='page' type='text' title='Page' size='10' value='%s'>&nbsp;"+
							"Page size<input name='pagesize' type='text' title='Page size' size='10' value='%s'><input type='image' src='%s/images/search.png' value='Refresh'></form>",
							page==null?"0":page,
							pagesize==null?"100":pagesize,
							uriReporter.getBaseReference())			
									));
			
			output.write("<table class='compoundtable' id='dataset' width='100%'></table>");
			output.write(String.format("\n<script type=\"text/javascript\">var baseref='%s';\nopentox = \n",
							uriReporter.getBaseReference()));			
			cmp_reporter.header(w, query);
		} catch (Exception x) {} 
	
	}
	@Override
	public void footer(Writer output, Q query) {
		try { 
			cmp_reporter.footer(output, query);
			output.write("</script>\n");
		
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
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
	
	

	protected String downloadLinks() throws IOException {
		StringBuilder w = new StringBuilder();
		MediaType[] mimes = {ChemicalMediaType.CHEMICAL_MDLSDF,
				ChemicalMediaType.CHEMICAL_CML,
				ChemicalMediaType.CHEMICAL_SMILES,
				ChemicalMediaType.CHEMICAL_INCHI,
				MediaType.TEXT_URI_LIST,
				MediaType.APPLICATION_PDF,
				MediaType.TEXT_CSV,
				MediaType.TEXT_PLAIN,
				ChemicalMediaType.WEKA_ARFF,
				ChemicalMediaType.THREECOL_ARFF,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_JSON
				};
		String[] image = {
				"sdf.jpg",
				"cml.jpg",
				"smi.png",
				"inchi.png",		
				"link.png",
				"pdf.png",
				"excel.png",
				"excel.png",
				"weka.png",
				"weka.png",
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
		w.append(String.format(" <a href='%s/ui/_dataset?dataset_uri=%s' title='Test the new dataset browser'>New view mode</a> ",
				uriReporter.getBaseReference().toString(),
				Reference.encode(uriReporter.getResourceRef().toString())
				));		
		return w.toString();
	}
	
	public void oldHeader(Writer w, Q query) {
		try {
			String query_smiles = "";
			String query_text = "";
			Form form = uriReporter.getResourceRef().getQueryAsForm();
			try {
				
				query_text = form.getFirstValue("text");
			} catch (Exception x) {
				query_text = "";
			}			
			try {
				
				query_smiles = form.getFirstValue(QueryResource.search_param);
			} catch (Exception x) {
				query_smiles = "";
			}
			String query_property = "";
			try {

				query_property = form.getFirstValue("property");
			} catch (Exception x) {
				query_property = "";
			}
			String query_threshold = "";
			try {

				query_threshold = form.getFirstValue("threshold");
			} catch (Exception x) {
				query_threshold = "0.9";
			}
			String maxhits = "";
			try {

				maxhits = form.getFirstValue(QueryResource.max_hits);
			} catch (Exception x) {
				maxhits = "1000";
			}		
			String page = "";
			try {

				page = form.getFirstValue("page");
			} catch (Exception x) {
				page = "0";
			}	
			String pagesize = "";
			try {

				pagesize = form.getFirstValue("pagesize");
			} catch (Exception x) {
				pagesize = maxhits; 
			}				
			/** This determines if similarity searching will be done via smiles or via URL **/
			String type = "";
			try {

				type = form.getFirstValue("type");
			} catch (Exception x) {
				type = "smiles";
			}		
			
			Reference baseReference = uriReporter.getBaseReference();
			if (!headless) {
			AmbitResource.writeTopHeader(w,
					isCollapsed()?"Chemical compounds":"Chemical compound"
					,
					uriReporter.getRequest(),
					uriReporter.getResourceRef(),
					"",
					uriReporter.getDocumentation()
					);
			
;
			
			w.write("<table bgcolor='#ffffff'>");
		
			w.write("<tr>");
			w.write("<td align='left' width='256px'>");
			w.write(String.format("<a href=\"http://ambit.sourceforge.net/intro.html\"><img src='%s/images/ambit-logo.png' width='256px' alt='%s' title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
			w.write("</td>");
			w.write("<td align='center'>");
			
			w.write("<form action='' name='form' method='get'>\n");
			w.write(String.format("<input name='type' type='hidden' value='%s'>\n",type==null?"smiles":type));
			
			
			String hint= "";
			if (uriReporter.getResourceRef().toString().indexOf("similarity")>0) {
				w.write(String.format("<label for='%s'>SMILES</label>&nbsp;",QueryResource.search_param));
				w.write(String.format("&nbsp;<input type='image' src=\"%s/images/edit.png\" title='Draw molecule' onClick='startEditor(\"%s\");'>",
						uriReporter.getBaseReference(),uriReporter.getBaseReference()));					
				w.write(String.format("<input name='%s' type='text' size='40' title='Enter SMILES' value='%s'>\n",QueryResource.search_param,query_smiles==null?"":query_smiles));
				
				w.write("&nbsp;");
				w.write(String.format("<label for='threshold'>Threshold</label>&nbsp;"));
				w.write(String.format("<input name='threshold' type='text' title='Tanimoto coefficient threshold [0,1], default 0.9' size='20' value='%s'>\n",query_threshold==null?"0.9":query_threshold));
				
				hint = "Draw structure and search for similar compounds";
				//w.write("<input type='submit' value='Search'><br>");

			} else if (uriReporter.getResourceRef().toString().indexOf("compound")>0) {
				w.write(String.format("<input name='property' type='text' title='Enter property name (optional)'  size='20' value='%s'>\n",query_property==null?"":query_property));
				w.write("&nbsp;");
				w.write(String.format("<input name='%s' type='text' title='Enter molecule identifier, name or property value (e.g. benzene)'  size='40' value='%s'>\n",QueryResource.search_param,query_smiles==null?"":query_smiles));
				hint = "Search by property or identifier name (optional) and value";
				//w.write("<input type='submit' value='Search'><br>");
			} else {
				w.write("<table>");

				w.write("<tr><th>");
				w.write(String.format("<label for='%s' title='Substructure pattern defined by SMARTS language. Enter manually, or use Draw button on the right'>SMARTS</label>&nbsp;",QueryResource.search_param));
				
				w.write(String.format("&nbsp;<input type='image' src=\"%s/images/edit.png\" title='Draw substructure' onClick='startEditor(\"%s\");'>",
						uriReporter.getBaseReference(),uriReporter.getBaseReference()));
				w.write("</th>");
				w.write("<td>");				
				w.write(String.format("<input name='%s' type='text'   size='60' value='%s'>\n",QueryResource.search_param,query_smiles==null?"":query_smiles));

				w.write("</td></tr>\n");
				w.write("<tr><th>");
				w.write(String.format("<label for='text' title='Any text, compound identifiers, property names and values, test names and values'>Keywords</label>"));
				w.write("</th><td>");
				
				w.write(String.format("<input name='text' type='text' title='Enter text to search for'  size='60' value='%s'><br>\n",query_text==null?"":query_text));
				w.write("</td></tr>\n");				
				hint = "Search for substructure and properties";				
				w.write("</table>");
			}
			
			
			
			//w.write(templates(baseReference));
			
			//w.write(baseReference.toString());

			//w.write("</form>\n"); moved in footer
			//w.write(hint);		
			w.write("<br>\n<b><i>");
			w.write(hint);
			w.write("</i></b>");		
			w.write("</td>");
			w.write("<td align='left' valign='center' width='256px'>");
			//w.write(String.format("<a href=\"http://opentox.org\"><img src=\"%s/images/logo.png\" width=\"256\" alt=\"%s\" title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
			w.write("<input type='submit' value='Search'>");
			w.write("</td></tr>\n");
			w.write("</table>");		
			}
				
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}		
	};
}