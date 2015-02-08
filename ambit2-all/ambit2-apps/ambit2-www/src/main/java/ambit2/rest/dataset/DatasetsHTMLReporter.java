package ambit2.rest.dataset;

import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.core.processors.structure.key.IStructureKey.Matcher;
import ambit2.rest.AmbitResource;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DisplayMode;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.facet.DatasetChemicalsQualityStatsResource;
import ambit2.rest.facet.DatasetStrucTypeStatsResource;
import ambit2.rest.facet.DatasetStructureQualityStatsResource;
import ambit2.rest.facet.DatasetsByEndpoint;
import ambit2.rest.model.ModelResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.QueryResource;


/**Generates html page for {@link QueryDatasetResource}
 * @author nina
 *
 */
@Deprecated
public class DatasetsHTMLReporter extends QueryHTMLReporter<ISourceDataset, IQueryRetrieval<ISourceDataset>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	public static String fileUploadField = "file";
	public DatasetsHTMLReporter() {
		this(null,DisplayMode.table,false);
	}
	public DatasetsHTMLReporter(Request baseRef,DisplayMode _dmode,boolean headless) {
		this(baseRef,baseRef,_dmode,headless);
	}
	public DatasetsHTMLReporter(Request baseRef,Request originalRef,DisplayMode _dmode,boolean headless) {
		super(baseRef,_dmode,headless);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new DatasetURIReporter<IQueryRetrieval<ISourceDataset>,ISourceDataset>(request);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<ISourceDataset> query) {
		try {
			if (!headless)
			AmbitResource.writeHTMLHeader(w,query.toString(),uriReporter.getRequest(),
					getUriReporter().getResourceRef());
	

		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		/**
		 * /dataset
		 */

		if (_dmode.isCollapsed()) { 
			
			if (!headless) 
			uploadUI("",w, query);
			
			try {
				w.write("<table  class='datatable' id='metadataset'>");
				w.write("<caption align='left'>");
			} catch (Exception x) {}
			if (!headless) 
			try {
				w.write(String.format("<a href='%s/query%s?%s=%s&condition=startswith' title='List datasets by endpoints'>%s</a>|&nbsp;",
						uriReporter.getBaseReference(),
						DatasetsByEndpoint.resource,
						MetadatasetResource.search_features.feature_sameas,
						URLEncoder.encode("http://www.opentox.org/echaEndpoints.owl"),
						"Datasets by endpoints"));
			} catch (Exception x) {
				
			}
			
			final String alphabet = "abcdefghijklmnopqrstuvwxyz";
			if (!headless) 
			try {
				w.write(String.format("<a href='?search=' title='List all datasets'>%s</a>|&nbsp","All"));
				for (int i=0; i < alphabet.length(); i++) {
					String search = alphabet.substring(i,i+1);
					w.write(String.format("<a href='?search=^%s' title='Search for datasets with name staring with %s'>%s</a>&nbsp",
								search.toUpperCase(),search.toUpperCase(),search.toUpperCase()));
				}
				w.write("|&nbsp;");
				for (int i=0; i < alphabet.length(); i++) {
					String search = alphabet.substring(i,i+1);
					w.write(String.format("<a href='?search=^%s' title='Search for datasets with name staring with %s'>%s</a>&nbsp",
								search.toLowerCase(),search.toLowerCase(),search.toLowerCase()));
				}
				w.write("|&nbsp;");
				for (int i=0; i < 10; i++) {
					w.write(String.format("<a href='?search=^%s' title='Search for datasets with name staring with %s'>%s</a>&nbsp",
								i,i,i));
				}			
				w.write("|&nbsp;");
				w.write(String.format("<a href='' title='Refresh this page'>%s</a>","Refresh"));

			} catch (Exception x) {
				
			}
			
			if (!headless)  {
				String page = Long.toString(query.getPage());
				final Form form = uriReporter.getResourceRef().getQueryAsForm();
				try {
					
					page = form.getFirstValue("page")==null?page:form.getFirstValue("page");
				} catch (Exception x) {
					page = Long.toString(query.getPage());
				}			
				String pageSize =  Long.toString(query.getPageSize());
			
				try {
					
					pageSize = form.getFirstValue("pagesize")==null? Long.toString(query.getPageSize()):form.getFirstValue("pagesize");
				} catch (Exception x) {
					pageSize = Long.toString(query.getPageSize());
				}	
				String search = "";
				try {
					
					search = form.getFirstValue("search");
				} catch (Exception x) {
					search = "";
				}			
				
				try {
		
					output.write("<div><span class=\"center\">");
					output.write("<form method='GET' action=''>");
					output.write(String.format("Page:<input name='page' type='text' title='Page' size='10' value='%s'>\n",page==null?"0":page));
					if (search !=null) output.write(String.format("<input name='search' type='hidden' value='%s'>\n",search));
					output.write(String.format("<b>Page size:</b><input name='pagesize' type='text' title='Page size' size='10' value='%s'>\n",pageSize==null?"50":pageSize));
					output.write(String.format("<input type='image' src='%s/images/page_go.png' onsubmit='submit-form();' value='Refresh'>",uriReporter.getBaseReference()));			
					output.write("</form>");
					output.write("</p></span></div><p>");
	
				} catch (Exception x) {
					
				} finally {
					
				}
			}
			
			try {
				w.write("</caption>");
				w.write("<thead>");
				w.write("<th>Stars</th>");
				w.write("<th>Name</th>");
				w.write("<th>License, Rights holder, Maintainer</th>");
				w.write("</thead>");
				w.write("<tbody>");
			} catch (Exception x) {}
		} 
		
		
			

		/**
		 * else /dataset/{id}/metadata
		 */
		
			
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<ISourceDataset> query) {
		try {
			if (_dmode.isCollapsed()) output.write("</tbody></table>");
			if (!headless)
			AmbitResource.writeHTMLFooter(output,query.toString(),uriReporter.getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
	}
	public void uploadUI(String uri, Writer output, IQueryRetrieval<ISourceDataset> query) {		
		try {
			output.write(String.format("<p><a href='#' onClick=\"javascript:toggleDiv('upload');\">Upload new dataset</a></p>"));
			
			output.write(String.format("<div id='%s' style='display: %s;''>","upload","none"));
			
			output.write(AmbitResource.printWidgetHeader("Upload"));
			output.write(AmbitResource.printWidgetContentHeader(""));
			output.write("<p>");
			String[][] methods = new String[][] {
					{"post","Add new dataset","Adds all compounds and data from the file, even empty structures."},
					{"put","Import properties","Import properties only for compounds from the file, which could be found in the database"}
			};
			output.write("<table width='95%' border='0' >");
			output.write("<tr>");
			for (int i=0; i < methods.length;i ++) {
				String[] method = methods[i];
				output.write("<td width='50%'>\n");
				output.write("<table border='0' width='95%'>");
				output.write("<caption>");
				output.write(String.format("<label accesskey='F' title='%s'>%s</label>",
						method[2],
						String.format("%s (SDF, MOL, SMI, CSV, TXT, ToxML (.xml) file)",method[1])
				)); 	
				output.write("</caption>");
				output.write("<tbody>");
				output.write(String.format("<form method=\"post\" action=\"%s?method=%s\" ENCTYPE=\"multipart/form-data\">",uri,method[0]));
				//file
				output.write("<tr bgcolor='#F4F2EB'>");
				output.write("<th>File<label title='Mandatory'>*</label></th>");
				output.write("<td>");
				output.write(String.format("<input type=\"file\" name=\"%s\" accept=\"%s\" title='%s' size=\"60\">",
						fileUploadField,
						ChemicalMediaType.CHEMICAL_MDLSDF.toString(),
						String.format("%s (SDF, MOL, SMI, CSV, TXT, ToxML (.xml) file)",method[1]))); 
				output.write("</td>");
				output.write("</tr>");
				//title
				output.write("<tr bgcolor='#F4F2EB'>");
				output.write("<th>Dataset name</th>");
				output.write("<td>");
				output.write(String.format("<input type=\"text\" name='title' title='%s' size=\"60\">","Dataset name (dc:title)")); 
				output.write("</td>");
				output.write("</tr>");
				//match
				output.write("<tr bgcolor='#F4F2EB'>");
				output.write("<th>Match</th>");
				output.write("<td>");
				output.write("<select name='match'>");
				for (Matcher matcher : IStructureKey.Matcher.values())
					output.write(String.format("<option title='%s' value='%s' %s>%s</option>",
							    "On import, finds the same compound in the database by matching with the selected criteria \""+matcher.getDescription()+"\"\n",
								matcher.toString(),
								IStructureKey.Matcher.CAS.equals(matcher)?"selected":"",
								(matcher.getDescription().length()>60)?matcher.getDescription().substring(0,60)+"...":matcher.getDescription()));
				output.write("</select>");
				output.write("</td>");
				output.write("</tr>");

				//URL
				output.write("<tr bgcolor='#F4F2EB'>");
				output.write("<th>URL</th>");
				output.write("<td>");
				output.write(String.format("<input type=\"text\" name='seeAlso' title='%s' size=\"60\">","Related URL (rdfs:seeAlso)")); 
				output.write("</td>");
				
				output.write("</tr>");
				
				output.write("<tr bgcolor='#F4F2EB'>");
				output.write("<th>License</th>");
				output.write("<td>");
				output.write("<select name='license'>");
				for (ISourceDataset.license license : ISourceDataset.license.values())
					output.write(String.format("<option title='%s' value='%s'>%s</option>",
							    license.getTitle(),
								license.getURI(),
								license.getURI()));
				output.write("</select>");
				output.write("</td>");
				output.write("</tr>");

				output.write("<tr bgcolor='#F4F2EB'><td align='right' colspan='2'><input type='submit' value='Submit'></td></tr>");
				output.write("</form>");
				output.write("</tbody>");
				output.write("</table>");
				
				output.write("</td>\n");
			}
			output.write("</tr>");
			output.write("</table>");
			output.write("</p>");
			output.write(AmbitResource.printWidgetContentFooter());
			output.write(AmbitResource.printWidgetFooter());
			output.write("</div>");
		} catch (Exception x) {}
		
	}
	@Override
	public Object processItem(ISourceDataset dataset) throws AmbitException {
		try {
			StringWriter w = new StringWriter();
			uriReporter.setOutput(w);
			uriReporter.processItem(dataset);
			

			String paging = "page=0&pagesize=10";
			if (_dmode.isCollapsed()) {
	
				
				output.write("<tr>");
				String datasetName = dataset.getName();
				output.write(String.format("<td>%s</td>",dataset.getStars()<0?"":dataset.getStars()));
				output.write(String.format(
						"<td><h4>%s</h4>",
						(datasetName==null)||(datasetName.equals(""))?Integer.toString(dataset.getID()):datasetName.replace("\\", " ")
						));
			
				
				output.write(String.format(
						"&nbsp;Browse : <a href=\"%s?%s\">/dataset/%d</a>",
						w.toString(),
						paging,
						dataset.getID()
						));	
				
				if ((dataset instanceof SourceDataset) && ((SourceDataset)dataset).getURL().startsWith("http")) {
					final String url = ((SourceDataset)dataset).getURL();
					output.write(String.format("<br>&nbsp;Source: <a href='%s' target='_blank'>%s</a>",url,url.length()>100?url.substring(url.lastIndexOf("/")+1):url));
				}
				
				output.write(String.format(
						"<br>&nbsp;Metadata: <a href=\"%s%s?%s\">/dataset/%d/metadata</a>",
						w.toString(),
						"/metadata",
						paging,
						dataset.getID()
						));	
				
				output.write(String.format(
						"<br>&nbsp;Columns: <a href=\"%s%s\" title='Retrieve feature list (columns)'>/dataset/%d%s</a>",
						w.toString(),
						PropertyResource.featuredef,
						dataset.getID(),
						PropertyResource.featuredef
						));		
		
	
				
				output.write(String.format(
						"<br>&nbsp;Substructure search: <a href=\"%s%s?search=CCCCCCO\">/dataset/%d%s</a>",
						w.toString(),
						"/smarts",
						dataset.getID(),
						"/smarts"
						));
				output.write("&nbsp;");
				
				output.write(String.format(
						"<br>&nbsp;Similarity search: <a href=\"%s%s?search=CCCCCCO\">/dataset/%d%s</a>",
						w.toString(),
						"/similarity",
						dataset.getID(),
						"/similarity"
						));				
				
				output.write(String.format(
						"<br>&nbsp;Model search: <a href='%s%s?dataset=%s' title='Find models where this dataset is used as a training dataset'>%s?search=/dataset/%d</a>",
						uriReporter.getBaseReference(),
						ModelResource.resource,
						Reference.encode(w.toString()),
						ModelResource.resource,
						dataset.getID()
						));		
				
				http://localhost:8080/ambit2/model?dataset=http%3A%2F%2Fapps.ideaconsult.net%3A8080%2Fambit2%2Fdataset%2F9026
				output.write("</td>");
				output.write("<td>");
				output.write(String.format(
						"<b>%s</b><br>&nbsp;%s<br>&nbsp;%s",
						getLicenseLabel(dataset.getLicenseURI()),
						dataset.getrightsHolder()==null?"":"Rights holder: "+dataset.getrightsHolder(),
						dataset.getMaintainer()==null?"":"Maintainer: "+dataset.getMaintainer()));	

				MediaType[] mimes = {ChemicalMediaType.CHEMICAL_MDLSDF,

						ChemicalMediaType.CHEMICAL_CML,
						ChemicalMediaType.CHEMICAL_SMILES,						
						MediaType.TEXT_URI_LIST,
						MediaType.APPLICATION_PDF,
						MediaType.TEXT_CSV,
						ChemicalMediaType.WEKA_ARFF,
						ChemicalMediaType.THREECOL_ARFF,
						MediaType.APPLICATION_RDF_XML,
						MediaType.APPLICATION_JSON
						};
				String[] image = {
						"sdf.jpg",
						"cml.jpg",
						"smi.png",						
						"link.png",
						"pdf.png",
						"excel.png",
						"weka.png",
						"weka.png",
						"rdf.gif",
						"json.png"
						
				};
				output.write("<br><b>Download:</b>&nbsp;");
				for (int i=0;i<mimes.length;i++) {
					MediaType mime = mimes[i];

					output.write(String.format(
							"<a href=\"%s%s?media=%s&%s\"  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a> ",
							w.toString(),
							"",
							//CompoundResource.compound,
							Reference.encode(mime.toString()),
							paging,
							uriReporter.getBaseReference().toString(),
							image[i],
							mime,
							mime));	
				}	
				output.write("</td>");
				output.write("</tr>");
			}  else 
				renderMetadata(dataset, w.toString(),null);
	

		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
		return null;
	}

	protected String getLicenseLabel(String licenseURI) {
		try {
			if (licenseURI!=null)
			for (ISourceDataset.license license : ISourceDataset.license.values()) 
				if (license.getURI().equals(licenseURI)) {
					return license.getTitle();
				}
		} catch (Exception x) {}
		return licenseURI;
	}
	protected Object renderMetadata(ISourceDataset dataset,String uri,IQueryRetrieval<ISourceDataset> query)  throws AmbitException  {
		try {
			//output.write("<h4>Dataset metadata</h4>");
			
			output.write(AmbitResource.printWidgetHeader(String.format("<a href='%s' target=_blank>%s</a>",uri,dataset.getName())));
					//String.format("<a href='#' onClick=\"javascript:toggleDiv('dataset_%d');\">More</a>\n",dataset.getID())));

			//	output.write(String.format("<div id='dataset_%d' style='float:right;display: %s;''>\n",dataset.getID(),"none"));		
			output.write(AmbitResource.printWidgetContentHeader(""));
			output.write("<p>");	
			output.write("<form method='post' action='?method=put'>\n");
			output.write("<table width='90%'>\n");
			output.write(String.format("<tr><th>%s</th><td>%s</td></tr>\n", "Dataset URI",uri));
			
			output.write(String.format("<tr><th>%s</th><td>", "Number of structures"));

			//output.write(String.format("<span id='D%d'></span><script>$('#D%d').load('%s/admin/stats/chemicals_in_dataset?dataset_uri=%s&media=%s');</script>",			
			output.write(String.format("<span id='D%d'></span><script>loadStats('%s','%s','#D%d');</script>", 
					dataset.getID(),
					uriReporter.getBaseReference(),
					Reference.encode(uri),
					dataset.getID()
					));
			output.write("</td></tr>\n");
			//output.write(String.format("<tr><th>%s</th><td>%s</td></tr>\n", "Number of structures",uri));
			
			http://localhost:8080/ambit2/admin/stats/chemicals_in_dataset?dataset_uri=http://localhost:8080/ambit2/dataset/5
			output.write(String.format("<tr><th>%s</th><td><input type='text' size='60' name='title' value='%s' %s></td></tr>\n", 
						"Dataset name",dataset.getName(),headless?"readonly":""));
			
			String licenseLabel = getLicenseLabel(dataset.getLicenseURI());
			
			StringBuilder select = new StringBuilder();
			select.append("<select name='licenseOptions'>\n");
			ISourceDataset.license selected = null;
			for (ISourceDataset.license l : ISourceDataset.license.values()) {
				select.append(String.format("<option value='%s' %s>%s</option>\n",
						l.getURI(),
						l.getURI().equals(dataset.getLicenseURI())?"selected='selected'":"",
						l.getTitle()));
				if ((selected==null) & l.getURI().equals(dataset.getLicenseURI())) 
					selected = l;
			}
			select.append(String.format("<option value='Other' %s>Other</option>\n",selected==null?"selected='selected'":""));			
			select.append("</select>");
			
			if (dataset.getLicenseURI()!=null)
				output.write(String.format("<tr><th>%s</th><td>%s<br><input type='text' size='60' name='license' title='%s' value='%s' %s></td></tr>\n", 
							"License/Rights",
							select.toString(),
							licenseLabel==null?dataset.getLicenseURI():licenseLabel,
							dataset.getLicenseURI()==null?"":dataset.getLicenseURI(),
							headless?"readonly":""
								
							));
			else
				output.write(String.format("<tr><th>License</th><td><input type='text' size='60' name='license' title='Enter license URI' value='' %s ></td></tr>\n",
						headless?"readonly":"")); 
			
			if (dataset.getrightsHolder()!=null)
				output.write(String.format("<tr><th>%s</th><td><input type='text' size='60' title='Rights holder (URI)' name='rightsHolder' value='%s' %s ></td></tr>\n", 
							"Rights holder",
							dataset.getrightsHolder(),
							headless?"readonly":""));

			else
				output.write(String.format(
						"<tr><th>Rights holder</th><td><input type='text' size='60' title='Rights holder (URI)' name='rightsHolder' value=' ' %s ></td></tr>\n",
						headless?"readonly":"")); 			
			
			if (!headless)
			output.write(String.format("<tr><th>%s</th><td><input align='bottom' type=\"submit\" value=\"%s\"></td></tr>\n", "","Update"));								
			output.write(String.format("<tr><th>%s</th><td>%s</td></tr>\n", "Source",dataset.getSource()));
			//don't write ip addresses
			if ((dataset instanceof SourceDataset) && ((SourceDataset)dataset).getURL().startsWith("http"))
				output.write(String.format("<tr><th>%s</th><td>%s</td></tr>\n", "See also",((SourceDataset)dataset).getURL()));
			
			output.write("</table>");
			output.write("</form>");
		
			output.write(String.format("<a href='%s'>%s</a>&nbsp;\n", uri, "Browse the dataset"));
			output.write(String.format("<a href='%s/compounds'>%s</a>&nbsp;\n", uri, "Browse the compounds only"));
			output.write(String.format("<a href='%s/feature'>%s</a>&nbsp;\n", uri, "Browse the dataset features"));
			
			
			output.write(String.format("<a href='%s%s%s' target='_blank'>%s</a>&nbsp;\n", 
					uri,QueryResource.query_resource,DatasetStrucTypeStatsResource.resource,"Structure type statistics"));
			output.write(String.format("<a href='%s%s%s' target='_blank'>%s</a>&nbsp;\n", 
					uri,QueryResource.query_resource,DatasetChemicalsQualityStatsResource.resource,"Consensus label statistics"));
			output.write(String.format("<a href='%s%s%s' target='_blank'>%s</a>&nbsp;\n", 
					uri,QueryResource.query_resource,DatasetStructureQualityStatsResource.resource,"Structure quality label statistics"));
			

			output.write(String.format("<a href='%s/similarity?search=c1ccccc1' target='_blank'>%s</a>&nbsp;\n", uri, "Search for similar compounds within this dataset"));
			output.write(String.format("<a href='%s/smarts?search=c1ccccc1' target='_blank'>%s</a>&nbsp;\n", uri,"Search compounds by SMARTS"));


			output.write(String.format("<a href='%s/chart/bar?dataset_uri=%s&param=sk1024'  target='_blank'>%s</a>&nbsp;\n", 
					uriReporter.getBaseReference(),uri,"Structure fragments bar chart"));
			output.write(String.format("<a href='%s/chart/bar?dataset_uri=%s&param=fp1024' target='_blank'>%s</a>&nbsp;\n", 
					uriReporter.getBaseReference(),uri,"Hashed fingerprints bar chart"));			
			//output.write("</div>\n");
			output.write("</p>");
			output.write(AmbitResource.printWidgetContentFooter());
			output.write(AmbitResource.printWidgetFooter());
			//output.write("<h4>Add more data to this dataset</h4>");
			//uploadUI(uri,output, query);
		} catch (Exception x) {
			
		}
		return dataset;
	}

}
/*
public String getReCaptchaHtml() {
ReCaptcha recaptcha = createReCaptcha();
return recaptcha.createRecaptchaHtml("You did not type the captcha correctly", new Properties());
}

private ReCaptcha createReCaptcha() {
String publicKey = //your public key
String privateKey = //your private key
return ReCaptchaFactory.newReCaptcha(publicKey, privateKey, true);
}

@ValidationMethod(on = "submit")
public void captchaValidation(ValidationErrors errors) {
    ReCaptchaResponse response = createReCaptcha().checkAnswer(context.getRequest().getRemoteAddr(),
            context.getRequest().getParameter("recaptcha_challenge_field"),
            context.getRequest().getParameter("recaptcha_response_field"));
    if (!response.isValid()) {
        errors.add("Captcha", new SimpleError("You didn't type the captcha correctly!"));
    }
}
*/