package ambit2.rest.structure;

import java.awt.Dimension;
import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.rest.AmbitResource;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryStructureHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.dataEntry.DataEntryResource;
import ambit2.rest.dataset.MetadatasetResource;
import ambit2.rest.facet.DatasetsByEndpoint;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.query.QueryResource;

/**
Generates HTML file with links to structures . TODO - make use of a template engine 
 * @author nina
 *
 * @param <Q>
 */
public class CompoundHTMLReporter<Q extends IQueryRetrieval<IStructureRecord>> 
										extends QueryStructureHTMLReporter<Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7776155843790521467L;
	protected PropertyURIReporter pReporter;
	protected boolean table = true;
	protected int count = 0;
	protected String hilightPredictions = null;
	protected Dimension cellSize = new Dimension(150,150);
	protected Form featureURI = null;
	protected String imgMime = "image/png";
	boolean hierarchy = false;
	//protected RetrieveFieldPropertyValue fieldQuery;

	public CompoundHTMLReporter(Request request,ResourceDoc doc,boolean collapsed,QueryURIReporter urireporter) {
		this(request,doc,collapsed,urireporter,null);
	}
	public CompoundHTMLReporter(Request request,ResourceDoc doc,boolean collapsed,QueryURIReporter urireporter,Template template) {
		this(request,doc,collapsed,urireporter,template,null,null);
	}
	public CompoundHTMLReporter(Request request,ResourceDoc doc,boolean collapsed,QueryURIReporter urireporter,
			Template template,Profile groupedProperties,Dimension d) {
		this("",request, doc, collapsed, urireporter, template,groupedProperties,d);
	}
	public CompoundHTMLReporter(String prefix, Request request,ResourceDoc doc,boolean collapsed,QueryURIReporter urireporter,
				Template template,Profile groupedProperties,Dimension d) {
		super(prefix,request,collapsed,doc);
		
		Reference f = request.getResourceRef().clone(); 
		f.setQuery(null);
		featureURI =  new Form(); 
		
		String[] features = request.getResourceRef().getQueryAsForm().getValuesArray(OpenTox.params.feature_uris.toString());
		if ((features == null) || (features.length==0)) {
			if (f.toString().indexOf("/dataset")>0) 
				featureURI.add(OpenTox.params.feature_uris.toString(),f.addSegment("feature").toString());
			else if (f.toString().indexOf("/compound")>0) 
				featureURI.add(OpenTox.params.feature_uris.toString(),f.addSegment("feature").toString());
		} else for (String ff:features)
			featureURI.add(OpenTox.params.feature_uris.toString(),ff);
		
		if (d != null) cellSize = d; 
		setGroupProperties(groupedProperties);
		setTemplate(template==null?new Template(null):template);
		if (urireporter != null) this.uriReporter = urireporter;
		
		hilightPredictions = request.getResourceRef().getQueryAsForm().getFirstValue("model_uri");
			pReporter = new PropertyURIReporter(request,this.uriReporter==null?null:this.uriReporter.getDocumentation());
			
		table = collapsed;
		getProcessors().clear();
		if ((getGroupProperties()!=null) && (getGroupProperties().size()>0))
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveGroupedValuesByAlias(getGroupProperties())) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveGroupedValuesByAlias)getQuery()).setRecord(target);
					return super.process(target);
				}
			});			
		if (getTemplate().size()>0) 
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveProfileValues(SearchMode.idproperty,getTemplate(),true)) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveProfileValues)getQuery()).setRecord(target);
					return super.process(target);
				}
			});
	
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});			
	
	}
	@Override
	public Writer getOutput() throws AmbitException {
		Writer w = super.getOutput();
		pReporter.setOutput(w);
		uriReporter.setOutput(w);
		return w;
	}
	public CompoundHTMLReporter(Request request,ResourceDoc doc,boolean collapsed,Template template) {
		this(request,doc,collapsed,null,template);
	}
	public CompoundHTMLReporter(Request request,ResourceDoc doc,boolean collapsed) {
		this(request,doc,collapsed,null,null);

	}
	@Override
	protected QueryURIReporter createURIReporter(Request request,ResourceDoc doc) {
		return new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(prefix ,request,doc);
	}
	
	@Override
	public Object processItem(IStructureRecord record) throws AmbitException  {
		
		try {

			if (table) {
				//output.write("<tr>");
				output.write(toURITable(record));
				//output.write("</tr>\n");		
			} else {
				output.write("<div id=\"div-1a\">");
				output.write(toURI(record));
				output.write("</div>");		
			}
			count++;
		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
		return null;
		
	}
	protected String content(String left, String right) throws IOException {
		return String.format(
				"<div class=\"rowwhite\"><span class=\"left\">%s</span><span class=\"center\">%s</span></div>",
				left,right);

	}
	/*
	protected String templates(Reference baseReference) throws IOException {
		StringBuilder w = new StringBuilder();
		w.append("<input type='submit' value='Select table columns'>");
		String[][] options= {
				{"template/All/Identifiers/view/tree","Identifiers"},
				{"template/All/Dataset/view/tree","Datasets"},
				{"template/All/Models/view/tree","Models"},
				//{"template/All/Endpoints/view/tree","Endpoints"},
				{"template/All/Descriptors/view/tree","All descriptors"},				

		};
		
		Form form = uriReporter.getRequest().getResourceRef().getQueryAsForm();
		String[] values = OpenTox.params.feature_uris.getValuesArray(form);
		w.append("<input type=CHECKBOX value=\"\">Default</option>\n");

		for (String option[]:options) {
			String checked = "";
			for (String value:values)
				if (value==null) continue;
				else if (value.equals(String.format("%s/%s", baseReference,option[0]))) 
			{ checked = "CHECKED"; break;}
			w.append(String.format("<input type=CHECKBOX %s STYLE=\"background-color: #516373;color: #99CC00;font-weight: bold;\" value=\"%s/%s\" name=\"%s\">%s</option>\n",
						checked,
						baseReference,
						option[0],
						OpenTox.params.feature_uris.toString(),option[1]));
		}
		
		for (String value:values) {
			if (value==null) continue;
			boolean add = true;
			for (String option[]:options) 
				if (value.equals(String.format("%s/%s", baseReference,option[0]))) { add = false; break;}
			
			if (add)
				w.append(String.format("<input type=CHECKBOX %s STYLE=\"background-color: #516373;color: #99CC00;font-weight: bold;\" value=\"%s\" name=\"%s\"><a href='%s' target='_blank'>%s</a></option>\n",
						"checked",
						value,
						OpenTox.params.feature_uris.toString(),value,value));				
		}
		w.append(String.format("<input type='TEXT' size='30'alt='Enter OpenTox Feature URL here, to be added as column table' name=\"%s\">\n",
				OpenTox.params.feature_uris.toString()));		
	
		return w.toString();
	}	
	*/
	
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
				MediaType.APPLICATION_RDF_XML
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
				"weka.jpg",
				"rdf.gif"
				
		};
		String q=uriReporter.getRequest().getResourceRef().getQuery();
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
	public String resultsForm(Q query) {
		StringBuilder w = new StringBuilder();
		w.append(String.format("<form method=\"post\" action=\"/query\">",""));
		w.append(String.format("<input type=\"text\" name=\"name\" value=\"%s\" size=\"30\">&nbsp;",query.toString()));
		w.append(String.format("<input type=\"hidden\" value='%s' name='queryURI'>\n",uriReporter.getRequest().getResourceRef()));
		w.append("<input type=\"submit\" value='Save search results'>&nbsp;");
		//output.write("</form>");
		//output.write(String.format("<form method=\"post\" action=\"%s/model\">",uriReporter.getBaseReference()));
		//output.write("<input type=\"submit\" value='Predict an endpoint'>&nbsp;");
		
		//output.write(String.format("<form method=\"post\" action=\"%s/algorithm\">",uriReporter.getBaseReference()));
		//output.write("<input type=\"submit\" value='Build a model&nbsp;'>&nbsp;");
		//output.write("<input type=\"submit\" value='Find similar compounds&nbsp;'>&nbsp;");
		//output.write("<input type=\"submit\" value='Search within results&nbsp;'>&nbsp;");
		
		w.append("</form>");	
		return w.toString();
	}
	public void header(Writer w, Q query) {
		try {

			Reference baseReference = uriReporter.getBaseReference();
			
			AmbitResource.writeTopHeader(w,
					collapsed?"Chemical compounds":"Chemical compound"
					,
					uriReporter.getRequest(),
					"",
					uriReporter.getDocumentation()
					);
			
			w.write("<table width='100%' bgcolor='#ffffff'>");
		
			w.write("<tr>");
			w.write("<td align='left' width='256px'>");
			w.write(String.format("<a href=\"http://ambit.sourceforge.net/intro.html\"><img src='%s/images/ambit-logo.png' width='256px' alt='%s' title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
			w.write("</td>");
			w.write("<td align='center'>");
			String query_smiles = "";
			String query_text = "";
			Form form = uriReporter.getRequest().getResourceRef().getQueryAsForm();
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
			String maxrecords = "";
			try {

				maxrecords = form.getFirstValue(QueryResource.max_hits);
			} catch (Exception x) {
				maxrecords = "1000";
			}		
			
			/** This determines if similarity searching will be done via smiles or via URL **/
			String type = "";
			try {

				type = form.getFirstValue("type");
			} catch (Exception x) {
				type = "smiles";
			}				
			w.write("<form action='' name='form' method='get'>\n");
			w.write(String.format("<input name='type' type='hidden' value='%s'>\n",type==null?"smiles":type));
			
			
			String hint= "";
			if (uriReporter.getRequest().getResourceRef().toString().indexOf("similarity")>0) {
				w.write(String.format("<label for='%s'>SMILES</label>&nbsp;",QueryResource.search_param));
				w.write(String.format("<input name='%s' type='text' size='40' title='Enter SMILES' value='%s'>\n",QueryResource.search_param,query_smiles==null?"":query_smiles));
				w.write(String.format("&nbsp;<input type='button' value='Draw molecule' onClick='startEditor(\"%s\");'>",
						uriReporter.getBaseReference()));
				w.write("&nbsp;");
				w.write(String.format("<label for='threshold'>Threshold</label>&nbsp;"));
				w.write(String.format("<input name='threshold' type='text' title='Tanimoto coefficient threshold [0,1], default 0.9' size='20' value='%s'>\n",query_threshold==null?"0.9":query_threshold));
				
				hint = "Draw structure and search for similar compounds";
				//w.write("<input type='submit' value='Search'><br>");

			} else if (uriReporter.getRequest().getResourceRef().toString().indexOf("compound")>0) {
				w.write(String.format("<input name='property' type='text' title='Enter property name (optional)'  size='20' value='%s'>\n",query_property==null?"":query_property));
				w.write("&nbsp;");
				w.write(String.format("<input name='%s' type='text' title='Enter molecule identifier, name or property value (e.g. benzene)'  size='40' value='%s'>\n",QueryResource.search_param,query_smiles==null?"":query_smiles));
				hint = "Search by property or identifier name (optional) and value";
				//w.write("<input type='submit' value='Search'><br>");
			} else {
				w.write("<table cellpadding=0  border='0'>");

				w.write("<tr><th>");
				w.write(String.format("<label for='%s' title='Substructure pattern defined by SMARTS language. Enter manually, or use Draw button on the right'>SMARTS</label>&nbsp;",QueryResource.search_param));
				w.write("</th><td>");
				w.write(String.format("<input name='%s' type='text'   size='60' value='%s'>\n",QueryResource.search_param,query_smiles==null?"":query_smiles));
				w.write(String.format("&nbsp;<input type='button' value='Draw substructure' onClick='startEditor(\"%s\");'>",
						uriReporter.getBaseReference()));	

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
			w.write(hint);		
			w.write("<br>\n<b><i>This site and AMBIT REST services are under development!</i></b>");		
			w.write("</td>");
			w.write("<td align='left' valign='center' width='256px'>");
			//w.write(String.format("<a href=\"http://opentox.org\"><img src=\"%s/images/logo.png\" width=\"256\" alt=\"%s\" title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
			w.write("<input type='submit' value='Search'>");
			w.write("</td></tr>\n");
			w.write("</table>");		
			w.write("<hr>");	
			
			if (hilightPredictions!= null) 
				w.write(String.format("<div><span class=\"center\"><h4>Atoms highlighted by the model <a href=%s target=_blank>%s</a></h4></span></div>",hilightPredictions,hilightPredictions));
			
			
			if (table) {
				
				//output.write(String.format("<div><span class=\"left\">%s</span></div>",templates(baseReference)));
				
				output.write("<div class=\"rowwhite\"><span class=\"center\">");
					
				//output.write(AmbitResource.jsTableSorter("results","pager"));
				output.write("<table id='results' class='tablesorter' border='0' cellpadding='0' cellspacing='1'>"); 
				
				output.write(String.format("<CAPTION CLASS=\"results\">Search results <input type='text' value='%s' readonly> &nbsp;Download as %s&nbsp;Max number of hits:%s</CAPTION>",
						query.toString(),
						downloadLinks(),
						String.format("<input name='max' type='text' title='Maximum number of hits' size='10' value='%s'>\n",maxrecords==null?"100":maxrecords)));//resultsForm(query)
						//,resultsForm(query)
				output.write("<thead><tr>");
				output.write(String.format("<th width='20'>#</th><th width='%d' bgcolor='#99CC00'>Compound</th>",cellSize.width)); //ECB42C
				
				List<Property> props = template2Header(getTemplate(),true);
				int hc = 0;
				
				hierarchy = props.size()>20;
			
				if (hierarchy) {
					output.write("<th>Properties</th>"); //one single cell and proeprtiws written vertically within
					output.write("</tr>\n</thead><tbody>");
				} else {
					for(Property p: props) {
						hc++;
						int max=10;
						int dot = 0;
						int end = p.getTitle().indexOf("Descriptor");
						if (end > 0) {
							dot = p.getTitle().lastIndexOf(".");
							if (dot<0) dot = 0; else dot++;
						} else end = p.getTitle().length();
						if ((end-dot)>max) end = dot + max;
						
						output.write(
							String.format("<th width='%d'><a href='%s' title='%s'>%s</a></th>",
									max,
									//(hc %2)==1?"class=\"results\"":"class=\"results_odd\"",
							p.getUrl(),p.getTitle(),p.getTitle().substring(dot,end)));
					}
					output.write("</tr>\n</thead><tbody><tr class=\"results\">");
					output.write("<th ></th><th ></th>");
					
					hc = 0;
					for(Property p: props) {
						hc++;
						output.write(
							String.format("<th align='center'><a href='%s' title='%s'>%s %s</a></th>",
							pReporter.getURI(p),
							p.getName(),
							p.getName(),p.getUnits()));
					}					
					output.write("</tr>\n");
				}


			
				
			}
			else {
				w.write(downloadLinks());
				w.append("<h4><div class=\"actions\"><span class=\"right\">");				
				w.write(resultsForm(query));
				w.append("</span></div></h4>\n");	

				output.write("<div id=\"div-1\">");
			}
			
			

		} catch (Exception x) {
			x.printStackTrace();
		}		
	};
	public void footer(Writer output, Q query) {
		try {
			if (table) {
				output.write("</tbody></table>");
				output.write("</span></div>");
			}
			else output.write("</div>");
			output.write("</form>\n");
			
			/*
			output.write(String.format("<form method='post' action='%s/compound'>",uriReporter.getBaseReference()));
			output.write("<input type='text' name='identifier' title='Compound identifier'>&nbsp;");
			output.write("<input type='submit' value='Find compound'>");
			output.write("</form>");	
			*/		
			AmbitResource.writeHTMLFooter(output,
					"",
					uriReporter.getRequest()
					);
					
			output.flush();			
		} catch (Exception x) {}		
	};

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public String toURITable(IStructureRecord record) {
		String w = uriReporter.getURI(record);
		StringBuilder b = new StringBuilder();
		
		
		b.append(String.format("<tr class=\"results_%s\">",((count % 2)==0)?"even":"odd"));
		
		b.append(String.format("<td >%d<br>%s<br>%s</td>\n",
				count+1,
				String.format("<a href='%s/query/similarity?search=%s&type=url&threshold=0.85' title='Find similar compounds'><img src=\"%s/images/search.png\" border='0' alt='Find similar' title='Find similar'></a>",
							uriReporter.getBaseReference(),Reference.encode(w),uriReporter.getBaseReference()),
				String.format("<a href='%s/query/smarts?search=%s&type=url&max=100' title='Find substructure'><img src=\"%s/images/search.png\" border='0' alt='Find substructure' title='Find substructure'></a>",
							uriReporter.getBaseReference(),Reference.encode(w),uriReporter.getBaseReference())							
						));
		
		String imguri;
		
		if (hilightPredictions!= null) {
			imguri= String.format("%s?%s=%s&media=%s",hilightPredictions,OpenTox.params.dataset_uri.toString(),w,Reference.encode(imgMime));
		}
		
		else imguri = String.format("%s?media=%s",w,Reference.encode(imgMime));		
				
		b.append(String.format(
				"<td valign='top'><a href=\"%s?media=text/html%s\"><img src=\"%s&w=%d&h=%d\" width='%d' height='%d' alt=\"%s\" title=\"%d\"/></a></td>",
				
				w,
				hilightPredictions==null?"":String.format("&%s=%s",OpenTox.params.model_uri,hilightPredictions),
				imguri, 
				cellSize.width,cellSize.height,
				cellSize.width,cellSize.height,
				w, record.getIdchemical()));


			List<Property> props = template2Header(getTemplate(),true);
			Property prevProperty = null;
			int col = 0;
			
			if (hierarchy) { 
				b.append("<td width='100%'>");
				//b.append("<table border='1' width='90%'>"); //style='border:1px dotted blue;' 
			}	
			
				


			for(Property property: props) {
				col++;
				Object value = record.getProperty(property);
					
				if (value instanceof Number) {
					NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
					nf.setGroupingUsed(false);
					value = nf.format(((Number)value).doubleValue());
				}
					boolean isLong = (value==null)?false:value.toString().length()>255;
					
				
					
					if (hierarchy) {
						if (value==null) continue; //don't write non existing values
					} else
					b.append(String.format("<td %s width='%s'>",
							//"class='results_col'",
							((count%2)==0)?
								((col % 2)==0)?"class='results'":"":
								((col % 2)==0)?"":"class='results_col'",
							isLong?"100":"100")) ; //"#EBEEF1"

					value = value==null?"":value.toString();
					
					boolean isHTML = (value == null)?false:value.toString().indexOf("<html>")>=0;
					String searchValue = (value==null)?null:
						(
						isHTML?null:
						value.toString().length()>255?value.toString().substring(0,255):value.toString()
								);
			
					
					
					StringBuilder f = new StringBuilder();
					for (String ff: featureURI.getValuesArray(OpenTox.params.feature_uris.toString())) {
						f.append(String.format("&%s=%s", OpenTox.params.feature_uris,ff));
					}
					if (hierarchy) {
						
						b.append("ToXML".equals(property.getUrl())?beautifyToXMLField(property,prevProperty):
						String.format("\n<br><a href='%s' target=_blank>%s</a>&nbsp;",pReporter.getURI(property),property.getName()));
						
						b.append(String.format("<label title='%s'><i><font color='black'>%s</font></i></label>",value,searchValue));
						//b.append(value);
						
						prevProperty = property;
					} else {
						b.append("<div>");
				
					
						b.append(String.format("<a href=\"%s/compound?%s=%s%s/%d&property=%s&search=%s%s\">%s</a>", 
							uriReporter.getBaseReference(),
							OpenTox.params.feature_uris.toString(),
							uriReporter.getBaseReference(),
							PropertyResource.featuredef,
							property.getId(),
							Reference.encode(property.getName()),
							searchValue==null?"":Reference.encode(searchValue.toString()),
									
							f,
							value
						));
					}			
					if (hierarchy) {
						//b.append("<br>");
					} else
						b.append("</div>\n");
					
					if (!hierarchy) b.append("</td>\n");
				}
			if (hierarchy) {

				b.append("</td>\n");
			}
			b.append("</tr>\n");		
	
		return b.toString();
	}		
	/**
	 * Some magic to show ToXML hierarchical fields, until we find a better solution. 
	 * Fields come sorted.
	 * @param p
	 * @param previous
	 * @return
	 */
	protected String beautifyToXMLField(Property p,Property previous) {
		StringBuilder b = new StringBuilder();
		
		String title = p.getTitle().replace("http://opentox.org/toxml.owl#", "");
		
		int pos = title.lastIndexOf(".");

		b.append("<br>");
		if (pos>0) {
			if (previous!=null) {
				String prevTitle = previous.getTitle().replace("http://opentox.org/toxml.owl#", "");
				int posPrev = prevTitle.lastIndexOf(".");
				if ((posPrev>0) && title.substring(0,pos).equals(prevTitle.substring(0,posPrev))) {
					//same path, one level down the hierarchy
					posPrev = previous.getName().lastIndexOf(".");
					int npos = p.getName().lastIndexOf(".");
					if ((npos>0)&&(posPrev>0) && !p.getName().substring(0,npos).equals(previous.getName().substring(0,posPrev))) {
						b.append("<br>");
					}
					b.append(getPropertyTitle(p, title.substring(pos+1)));
				} else {
					b.append("<hr>");
					b.append(String.format("<h5>%s</h5>",title.substring(0,pos).replace(".", "\u00BB" )));
					b.append(getPropertyTitle(p, title.substring(pos+1)));
				}
			} else {
				b.append("<hr>");
				b.append(String.format("<h5>%s</h5>",title.substring(0,pos).replace(".", "\u00BB" )));
				b.append(getPropertyTitle(p, title.substring(pos+1)));
			}
			
		}
		else {
			b.append(getPropertyTitle(p, title));
		}
		return b.toString();
	}
	
	protected String getPropertyTitle(Property p,String title) {
		StringBuilder space = new StringBuilder();
		for (int i=0; i < p.getName().length()-1;i++)
			if (".".equals(p.getName().substring(i,i+1))) space.append("&nbsp;"); 
		
		return String.format("%s<a href='%s' title='%s'>%s%s%s</a>&nbsp;",
						space.toString(),
						pReporter.getURI(p),
						p.getName(),
						title,
						"".equals(p.getUnits())?"":",",
						p.getUnits()
						);

	
	}
	@Override
	protected List<Property> template2Header(Template template,
			boolean propertiesOnly) {
		List<Property> h =  super.template2Header(template, propertiesOnly);
		
		
		Collections.sort(h,new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				
				String n1[] = o1.getName().replace(".",":").split(":");
				String n2[] = o2.getName().replace(".",":").split(":");
				
				int c = n1.length<n2.length?n1.length:n2.length;
				int r = 0;
				for (int i=0; i < c;i++) { 
					r = n1[i].compareTo(n2[i]);
					if (r==0) continue;
					return r>0?(i+1):-(i+1);
				}
				return r>0?(c+1):-(c+1);
				
			}
		});	
		
		return h;
	}
	public String toURI(IStructureRecord record) {
		String w = uriReporter.getURI(record);
		StringBuilder b = new StringBuilder();
		
		
		String imguri;
		if (hilightPredictions!= null)
			imguri= String.format("%s?%s=%s&media=%s",
				hilightPredictions,
				OpenTox.params.dataset_uri.toString(),w,
				Reference.encode(imgMime));
		else imguri= String.format("%s?media=%s",w,Reference.encode(imgMime));		
		
		b.append(String.format("<div id=\"div-1b1\"><input type=checkbox name=\"compound[]\" checked value=\"%d\"></div>",record.getIdchemical()));
		
		b.append(String.format(
				"<a href=\"%s\"><img src=\"%s&w=%d&h=%d\" width='%d' height='%d' alt=\"%s\" title=\"%d\"/></a>",
				
				w, imguri, 
				cellSize.width,cellSize.height,
				cellSize.width,cellSize.height,
				w, record.getIdchemical()));
		b.append("<div id=\"div-1d\">");

		b.append(String.format("<a href=\"%s%s%s/url/all?search=%s\">Identifiers</a><br>",
				uriReporter.getBaseReference(),
				QueryResource.query_resource,
				CompoundLookup.resource,
				Reference.encode(w)
				));
		
	
		
		b.append(String.format("<a href=\"%s?%s=%s\">Feature values</a><br>",
				w,
				OpenTox.params.feature_uris,
				Reference.encode(String.format("%s/%s",w,OpenTox.URI.feature))
				));		
		
		b.append(String.format("<a href=\"%s/%s\">Datasets</a><br>",
				w,
				"datasets"
			));	

		
		b.append(String.format("<a href='%s/query%s?%s=%s&condition=startswith&%s=%s' title='List datasets by endpoints'>Datasets by endpoints</a><br>",
				uriReporter.getBaseReference(),
				DatasetsByEndpoint.resource,
				MetadatasetResource.search_features.feature_sameas,
				URLEncoder.encode("http://www.opentox.org/echaEndpoints.owl"),
				OpenTox.params.compound_uri,
				URLEncoder.encode(w)
			));	
	
		b.append(String.format("<a href=\"%s%s\">Data entries</a><br>",
				w,
				DataEntryResource.resourceTag
			));	
		
		b.append(String.format("<a href=\"%s/%s\">Features</a><br>",
				w,
				OpenTox.URI.feature
				));	
		
		b.append(String.format("<a href=\"%s/%s\">QA label</a><br>",
				w,
				"consensus"
				));			
		/*
		String[][] s = new String[][] {
				{PropertyValueResource.featureKey,Property.opentox_CAS,"CAS RN"},
				{PropertyValueResource.featureKey,Property.opentox_EC,"EINECS"},
				{PropertyValueResource.featureKey,Property.opentox_Name,"Chemical name(s)"},
				{PropertyValueResource.featureKey,null,"All available feature values"},
		};
		for (String[] n:s)
				b.append(String.format("<a href=\"%s%s/%s\">%s</a><br>",w,n[0],n[1]==null?"":Reference.encode(n[1]),n[2]));
		
		s = new String[][] {
				{"/template",null,"Feature values by groups"},
				{DataEntryResource.resourceTag,null,"Feature values by dataset"},
				{PropertyResource.featuredef,null,"Features"},
				{null,null,"Model predictions",String.format("%s/model/null/predicted",uriReporter.getBaseReference().toString())},
		};		
		
		for (String[] n:s)
			if (n[0]==null)
				b.append(String.format("<a href=\"%s?%s=%s\">%s</a><br>",w,OpenTox.params.feature_uris.toString(),Reference.encode(n[3]),n[2]));
			else
				b.append(String.format("<a href=\"%s%s/%s\">%s</a><br>",w,n[0],n[1]==null?"":n[1],n[2]));
		*/	
			List<Property> props = template2Header(getTemplate(),true);

				for(int i=0; i < props.size();i++) { 
					Property property = props.get(i);
					if (property.getId()>0) {
						Object value = record.getProperty(property);
						if (value!=null)
						b.append(String.format("<b>%s</b>&nbsp;%s<br>",
								property.getName(),value==null?"":
									value.toString()));
					}
				}

			b.append("</div>");		
	
		return b.toString();
	}		

	@Override
	public void close() throws SQLException {
		super.close();
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
	}
}
