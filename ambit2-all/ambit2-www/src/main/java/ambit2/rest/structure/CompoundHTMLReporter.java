package ambit2.rest.structure;

import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.rest.AmbitResource;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.property.PropertyURIReporter;

/**
Generates HTML file with links to structures . TODO - make use of a template engine 
 * @author nina
 *
 * @param <Q>
 */
public class CompoundHTMLReporter<Q extends IQueryRetrieval<IStructureRecord>> 
			extends QueryHTMLReporter<IStructureRecord,Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7776155843790521467L;
	protected PropertyURIReporter pReporter;
	protected boolean table = true;
	protected int count = 0;
	//protected RetrieveFieldPropertyValue fieldQuery;

	public CompoundHTMLReporter(Request request,boolean collapsed,QueryURIReporter urireporter) {
		this(request,collapsed,urireporter,null);
	}
	public CompoundHTMLReporter(Request request,boolean collapsed,QueryURIReporter urireporter,Template template) {
		super(request,collapsed);
		setTemplate(template==null?new Template(null):template);
		if (urireporter != null) this.uriReporter = urireporter;
		pReporter = new PropertyURIReporter(request);
		table = collapsed;
		getProcessors().clear();
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
		return w;
	}
	public CompoundHTMLReporter(Request request,boolean collapsed,Template template) {
		this(request,collapsed,null,template);
	}
	public CompoundHTMLReporter(Request request,boolean collapsed) {
		this(request,collapsed,null,null);

	}
	@Override
	protected QueryURIReporter createURIReporter(Request request) {
		return new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(request);
	}
	@Override
	public void processItem(IStructureRecord record) throws AmbitException  {
		
		try {
			if (table) {
				output.write("<tr>");
				output.write(toURITable(record));
				output.write("</tr>");		
				
			} else {
				output.write("<div id=\"div-1a\">");
				output.write(toURI(record));
				output.write("</div>");		
			}
		} catch (Exception x) {
			logger.error(x);
		}
		count++;
	}
	
	public void header(Writer w, Q query) {
		try {
			String property = "";

			Reference baseReference = uriReporter.getBaseReference();
			
			AmbitResource.writeTopHeader(w,
					collapsed?"Chemical compounds":"Chemical compound"
					,
					uriReporter.getRequest(),
					""
					);
			
			w.write("<table width='100%' bgcolor='#ffffff'>");
		
			w.write("<tr>");
			w.write("<td align='left' width='256px'>");
			w.write(String.format("<a href=\"http://ambit.sourceforge.net/intro.html\"><img src='%s/images/ambit-logo.png' width='256px' alt='%s' title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
			w.write("</td>");
			w.write("<td align='center'>");
			String query_smiles = "";
			try {
				Form form = uriReporter.getRequest().getResourceRef().getQueryAsForm();
				query_smiles = form.getFirstValue("search");
			} catch (Exception x) {
				query_smiles = "";
			}

			w.write("<form action='' method='get'>\n");
			w.write(String.format("<input name='property' size='20' value='%s'>\n",property));
			w.write("&nbsp;");
			w.write(String.format("<input name='search' size='60' value='%s'>\n",query_smiles==null?"":query_smiles));

			w.write("<input type='submit' value='Search'><br>");
			//w.write(baseReference.toString());

			w.write("</form>\n");
			w.write("Search by property or identifier name (optional) and value");		
			w.write("<br><b><i>This site and AMBIT REST services are under development!</i></b>");		
			w.write("</td>");
			w.write("<td align='right' width='256px'>");
			w.write(String.format("<a href=\"http://opentox.org\"><img src=\"%s/images/logo.png\" width=\"256\" alt=\"%s\" title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));

			w.write("</td></tr>");
			w.write("</table>");		
			
			
			
			w.write("<hr>");
			/*
			AmbitResource.writeSearchForm(w,
					collapsed?"Chemical compounds":"Chemical compound"
					,
					uriReporter.getBaseReference(),
					""
					);
			
			*/
			
			MediaType[] mimes = {ChemicalMediaType.CHEMICAL_MDLSDF,
					ChemicalMediaType.CHEMICAL_SMILES,
					ChemicalMediaType.CHEMICAL_CML,
					MediaType.TEXT_URI_LIST,
					MediaType.TEXT_XML,
					MediaType.APPLICATION_PDF,
					MediaType.TEXT_CSV,
					ChemicalMediaType.WEKA_ARFF,					
					};
			String[] image = {
					"structures.gif",
					"structures.gif",
					"structures.gif",
					"link.png",
					"xml.png",
					"pdf.png",
					"excel.png",
					"weka.jpg"					
					
			};
			
			for (int i=0;i<mimes.length;i++) {
				MediaType mime = mimes[i];
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s?accept-header=%s\"  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>",
						"",
						mime,
						uriReporter.getBaseReference().toString(),
						image[i],
						mime,
						mime));	
			}				
			
			output.write("<h4><div class=\"actions\"><span class=\"right\">");
			output.write(String.format("<form method=\"post\" action=\"\">",""));
			output.write(String.format("%s","Query name:&nbsp;"));

			output.write(String.format("<input type=\"text\" name=\"name\" value=\"%s\" size=\"30\">&nbsp;",query.toString()));
			output.write("<input type=\"submit\" value='Save search results'>&nbsp;");
			//output.write("</form>");
			//output.write(String.format("<form method=\"post\" action=\"%s/model\">",uriReporter.getBaseReference()));
			//output.write("<input type=\"submit\" value='Predict an endpoint'>&nbsp;");
			
			//output.write(String.format("<form method=\"post\" action=\"%s/algorithm\">",uriReporter.getBaseReference()));
			//output.write("<input type=\"submit\" value='Build a model&nbsp;'>&nbsp;");
			//output.write("<input type=\"submit\" value='Find similar compounds&nbsp;'>&nbsp;");
			//output.write("<input type=\"submit\" value='Search within results&nbsp;'>&nbsp;");
			
			output.write("</form>");	
			output.write("</span></div></h4>\n");	
			
			if (table) {
				output.write("<table border='0' width='99%'>"); 
				output.write("<th bgcolor='#99CC00'>Compound</th>"); //ECB42C
				List<Property> props = template2Header(getTemplate(),true);

				for(Property p: props) {
					
					output.write(
							String.format("<th bgcolor='#99CC00' align='center'><a href='%s'>%s</a></th>",
						pReporter.getURI(p),p.getName()));

				}				
			}
			else
				output.write("<div id=\"div-1\">");
			
			

		} catch (Exception x) {}		
	};
	public void footer(Writer output, Q query) {
		try {
			if (table) output.write("</table>");
			else output.write("</div>");
			//output.write("</form>");			
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
		
		b.append("<tr>");
		

		b.append(String.format(
				"<th><a href=\"%s\"><img src=\"%s?accept-header=image/png&w=150&h=150\" alt=\"%s\" title=\"%d\"/></a></th>",
				
				w, w, 
				w, record.getIdchemical()));


			List<Property> props = template2Header(getTemplate(),true);

			for(Property property: props) {
				//if (property.getId()>0) {
					Object value = record.getProperty(property);
					b.append(String.format("<td bgcolor='%s' width='100' align='right'>",((count % 2)==1)?"#F9FFE6":"#FBFFEC" )) ; //"#EBEEF1"
					if (value != null) {
						//?property=MW&search=100+..+200
						b.append(String.format("<a href=\"%s/compound?property=%s&search=%s\"><img src=\"%s/images/search.png\" border='0' alt='Search for %s=%s' title='Search for %s=%s'></a>", 
								uriReporter.getBaseReference(),
								Reference.encode(property.getName()),
								Reference.encode(value.toString()),
								uriReporter.getBaseReference(),		
								property.getName(),
								value.toString(),
								property.getName(),
								value.toString()							
						));
						//?property=MW&search=100+..+200
						b.append(String.format("&nbsp;<a href=\"%s/compound?search=%s\"><img src=\"%s/images/search.png\" border='0' alt='Relaxed search for %s' title='Relaxed search for %s'></a>", 
								uriReporter.getBaseReference(),
								Reference.encode(value.toString()),
								uriReporter.getBaseReference(),		
								value.toString(),
								value.toString()							
						));						
					}
					b.append(String.format("<br><a href='%s'>%s</a>",
							String.format("%s/feature/compound/%d/feature_definition/%d", uriReporter.getBaseReference(),record.getIdchemical(),property.getId()),
							value==null?"":
								value.toString().length()>40?value.toString().substring(0,40):value.toString()
										));					
					b.append("</td>");
				}

			b.append("</tr>");		
	
		return b.toString();
	}		
	
	public String toURI(IStructureRecord record) {
		String w = uriReporter.getURI(record);
		StringBuilder b = new StringBuilder();
		
		
		b.append(String.format("<div id=\"div-1b1\"><input type=checkbox name=\"compound[]\" checked value=\"%d\"></div>",record.getIdchemical()));
		
		b.append(String.format(
				"<a href=\"%s\"><img src=\"%s?accept-header=image/png\" alt=\"%s\" title=\"%d\"/></a>",
				
				w, w, 
				w, record.getIdchemical()));
		b.append("<div id=\"div-1d\">");

		String[][] s = new String[][] {
				{"feature",CDKConstants.CASRN,"CAS RN"},
				{"feature","EC","EINECS"},
				{"feature",CDKConstants.NAMES,"Chemical name(s)"},
				{"feature",null,"All available feature values"},
				{"template",null,"Feature values by groups"},
				{"tuple",null,"Feature values by dataset"},
				//{"feature_definition",null,"Feature definitions"},
				{"model",null,"Model predictions"},
				
		};
			for (String[] n:s)
			b.append(String.format("<a href=\"%s/%s/%s\">%s</a><br>",w,n[0],n[1]==null?"":n[1],n[2]));
			
			List<Property> props = template2Header(getTemplate(),true);

			for(Property property: props) 
				if (property.getId()>0) {
					Object value = record.getProperty(property);
					b.append(String.format("<b>%s</b>&nbsp;%s<br>",
							property.getName(),value==null?"":
								value.toString()));
				}
			//b.append("</table>");
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
