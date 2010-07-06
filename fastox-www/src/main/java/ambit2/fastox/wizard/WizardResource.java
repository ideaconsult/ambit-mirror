package ambit2.fastox.wizard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ambit2.fastox.ModelTools;
import ambit2.fastox.models.ReportingResource;
import ambit2.fastox.steps.StepException;
import ambit2.fastox.steps.StepProcessor;
import ambit2.fastox.users.IToxPredictSession;
import ambit2.fastox.users.IToxPredictUser;
import ambit2.fastox.users.MemoryUsersStorage;
import ambit2.fastox.users.UserResource;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.fastox.wizard.Wizard.WizardMode;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
	
public abstract class WizardResource extends ServerResource {
	protected static String jsGoogleAnalytics = null;
	protected IToxPredictSession session;
	protected WizardStep step;
	protected List<String> tabs;
	protected String tabIndex = null;
	protected WizardMode mode;
	protected String meta;
	protected Wizard wizard;
	public static final String key = "mode";
	protected int stepIndex;
	protected static String version = null;
	protected String helpResource = null;
	
	public WizardResource(int stepIndex) throws ResourceException {
		super();
		this.stepIndex = stepIndex;
		setMode(WizardMode.A);
	}
	
	protected String readVersion() {
		if (version!=null)return version;
		final String build = "Implementation-Build:";
		Representation p=null;
		try {
			ClientResource r = new ClientResource(String.format("%s/meta/MANIFEST.MF",getRequest().getRootRef()));
			p = r.get();
			String text = p.getText();
			//String text = build + ":0.0.1-SNAPSHOT-r1793-1266340980278";
			int i = text.indexOf(build);
			if (i>=0) {
				version = text.substring(i+build.length());
				i = version.lastIndexOf('-');
				if (i > 0) 
					version = String.format("%s-%s", 
							version.substring(1,i),
							new Date(Long.parseLong(version.substring(i+1).replace("-", "").trim())));
			}
		} catch (Exception x) {
			version = version + "_" + getRequest().getRootRef() + "_" + x.getMessage();
		} finally {
			//try { p.release();} catch (Exception x) {}
		}
		return version;
	}
	public void setMode(WizardMode mode) {
		this.mode = mode;
	}
	protected List<String> createTabs() {
		List<String> tabs = new ArrayList<String>();
		tabs.add(step.getTitle());
		return tabs;
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			session = getSession(getUserKey());
			if (session == null)
				getResponse().redirectSeeOther(getRootRef());
			
		} catch (Exception x) {
			getResponse().redirectSeeOther(getRootRef());
		}
		
		getVariants().add(new Variant(MediaType.TEXT_HTML));
		Object t = getRequestAttributes().get(WizardStep.tab);
		tabIndex = t==null?getDefaultTab():Reference.decode(t.toString());
		
		try {
			setMode(WizardMode.valueOf(getRequest().getAttributes().get(key).toString()));
		} catch (Exception x) { setMode(WizardMode.A);}

		wizard = Wizard.getInstance(mode);
		
		try {
			this.step = wizard.getStep(stepIndex);
		} catch (Exception x) {
			step = null;
		}
		
		tabs = createTabs();
		meta = "";
		readVersion();
		
		
	}
	protected String getUserKey() throws ResourceException {
		Object object = getRequest().getAttributes().get(UserResource.resourceKey);
		if (object!=null) return Reference.decode(object.toString());
		return null;
	}	
	protected IToxPredictSession getSession(String id) {
		return MemoryUsersStorage.getSession(id);
	}
	protected IToxPredictSession getSession(IToxPredictUser user) {
		return MemoryUsersStorage.getSession(user);
	}
	protected IToxPredictSession addSession(IToxPredictUser user) {
		return MemoryUsersStorage.addSession(user);
	}


	protected String getDefaultTab() {
		return "Help";
	}
	public void header(Writer w,String meta) throws IOException {
		
		Reference baseReference = getRequest()==null?null:getRequest().getRootRef();
		w.write(
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
			);
		w.write(String.format("<html><head><title>%s</title>%s\n",step.getTitle(),meta));
		//w.write(String.format("<link href=\"%s/style/global.css\" rel=\"stylesheet\" type=\"text/css\">",baseReference));
		
		//w.write("<meta name=\"mssmarttagspreventparsing\" content=\"true\" />");
		//w.write("<meta http-equiv=\"imagetoolbar\" content=\"false\" />");

		w.write(String.format("<style type=\"text/css\" media=\"all\">\n@import \"%s/style/global.css\";\n</style>",baseReference));

		w.write("<meta name=\"robots\" content=\"index,nofollow\"><META NAME=\"GOOGLEBOT\" CONTENT=\"index,NOFOLLOW\">");
		w.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/jme/jme.js\"></script>\n",baseReference));
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/jquery/jquery-1.4.2.min.js\"></script>\n",baseReference));
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/jquery/stats.js\"></script>\n",baseReference));
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/jquery/jquery.tablesorter.min.js\"></script>\n",baseReference));
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/jquery/jquery-ui-1.8.1.custom.min.js\"></script>\n",baseReference));
		//w.write(String.format("<link type=\"text/css\" href=\"%s/css/redmond/jquery-ui-1.8.1.custom.css\" rel=\"stylesheet\" />\n",baseReference));
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/jquery/jquery.blockUI.js\"></script>\n",getRequest().getRootRef()));
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/scripts/animatedcollapse.js\"></script>\n",baseReference));
		w.write("<script type=\"text/javascript\">\n");
		w.write("animatedcollapse.addDiv('help_step', 'fade=1')\n");
		w.write("for (i=1;i<=5;i++) { animatedcollapse.addDiv('wiz' + i, 'fade=1'); }\n");
		w.write("animatedcollapse.addDiv('wiz6', 'fade=1,width=25%')\n");
		w.write("animatedcollapse.ontoggle=function($, divobj, state){ //fires each time a DIV is expanded/contracted\n");
		w.write("//$: Access to jQuery\n");
		w.write("//divobj: DOM reference to DIV being expanded/ collapsed. Use \"divobj.id\" to get its ID\n");
		w.write("//state: \"block\" or \"none\", depending on state\n");
		w.write("}\n");
		w.write("animatedcollapse.init()\n");
		w.write("</script>\n");
		w.write("<script>function changeImage(img,src)  {    document.getElementById(img).src=src;} </script>\n");
		w.write("</head>\n");
		w.write("<body>");
		w.write(String.format("<link rel=\"stylesheet\" href=\"%s/style/tablesorter.css\" type=\"text/css\" media=\"screen\" title=\"Flora (Default)\">",baseReference));
		//w.write(String.format("<link rel=\"stylesheet\" href=\"%s/style/scrollable.css\" type=\"text/css\" media=\"screen\">",baseReference));
		w.write(ReportingResource.js());

	}
	public void top(Writer writer) throws IOException {
		writer.write("<table width='99%' border='0'>");
		writer.write("<tr>");
		writer.write("<td align='left'>");
		writer.write(String.format("<a href='%s' title='ToxPredict, OpenTox Demo application'><img src='%s%s' alt='ToxPredict home page' title='%s'></a>",
				getRootRef(),
				getRootRef(),
				"/images/ToxPredict_rgb_72.png",
				"ToxPredict home page",
				"ToxPredict, OpenTox Demo application"));	
		writer.write("<h5><a href='http://opentox.org' target=_blank>OpenTox</a> demo application</h5>");
		writer.write("</td>");
		writer.write("<td align='right'>");
		writer.write(String.format("<div class='user'>Welcome,&nbsp;<a href='%s/%s/%s'>%s</a></div><br>",
				getRequest().getRootRef(),
				UserResource.resource,
				Reference.encode(session==null?"guest":session.getUser().getId()),
				session==null?"guest":session.getUser().getName()));			
		writer.write(String.format("<a href='%s/admin/%s' target='admin' title='Configuration'>%s</a><br>",
				getRequest().getRootRef(),
				session.getUser().getId(),
				"Admin"));		
		writer.write("<a href=\"javascript:animatedcollapse.toggle('help_step')\" title=\"Help\">Help</a>");		
		writer.write("</td>");
		writer.write("<tr>");
		
		writer.write("</table>");	
	}
	public void navigator(Writer writer) throws IOException {
		

		writer.write("<ul id=\"mainNav\" class=\"wizardStep\">\n");
		
		Form query = getRequest().getResourceRef().getQueryAsForm();
		
		if (step.getIndex()>0)
		for (int i=1; i < wizard.size(); i++) {
			WizardStep thestep = wizard.getStep(i) ;
			Reference stepRef = new Reference(
					String.format("%s/%s/%s/%s%s",
					getRequest().getRootRef().toString(),
					UserResource.resource,
					session.getUser().getId(),
					mode,
					thestep.getResource()));
			//stepRef.setQuery(query.getQueryString());
			
			if (i < step.getIndex()) {
				writer.write(String.format(
						"<li class=\"%s\"><a href=\"%s\" title=\"\"><em>%d.&nbsp;%s</em><span>%s</span></a></li>\n",
						(i+1)==step.getIndex()?"lastDone":"done",
						stepRef,
						i,
						thestep.getTitle(),
						thestep.getDescription()
						));			
			} else 
			if (i == step.getIndex()) {
				writer.write(String.format(
						"<li class=\"current %s\"><a title=\"\"><em>%d.&nbsp;%s</em><span>%s</span></a></li>\n",
						(i==(wizard.size()-1))?(step.getIndex()==(wizard.size()-1))?"mainNavNoBg\"":"mainNavLast\"":"",
						i,
						step.getTitle(),
						step.getDescription()
						));				
			} else if (i > step.getIndex()) {
				writer.write(String.format(
						"<li %s><a title=\"\"><em>%d.&nbsp;%s</em><span>%s</span></a></li>\n",
						(i==(wizard.size()-1))?"class=\"mainNavLast\"":"",
						i,
						thestep.getTitle(),
						thestep.getDescription()						
						));					
			}
			


		}
		if ((step.getIndex()!=0) && (step.getIndex()!=(wizard.size()-1)))
		writer.write(String.format("<li class=\"next\"><INPUT name=\"next\" onClick=\"getSmiles()\" type=\"submit\" value=\"\" tabindex=\"1\" title='Click here for the next step' class=\"button\"></li>"));

		writer.write("</ul>\n");

		//<div class="clearfloat">&nbsp;</div>
	}
	public void help(Writer writer)  throws IOException {
		writer.write("<div class=\"clearfloat\">&nbsp;</div>");

		writer.write(String.format("<div class=\"help\" id=\"help_step\" style=\"display:none;\">%s<div style=\"text-align:right; font-style:italic;\"><a href=\"javascript:animatedcollapse.hide('help_step')\">Hide</a></div></div>",getHelp()));
	}
	public void renderErrorsTab(Writer writer, String key)  throws IOException {
		Iterator<String> keys = session.getErrorKeys();
		if (keys!=null)	while (keys.hasNext()) {
			String k = keys.next(); 
			writer.write(String.format("<div class='errors'>%s</div><br>",
					session.getError(k)==null?"":session.getError(k).getMessage()));
		}		
		//if (session.getError(key) != null) 
			//writer.write(String.format("<div class='errors'>%s</div>",session.getError(key).getMessage()));
	}
	public void renderHelpTab(Writer writer, String key)  throws IOException {
		writer.write(String.format("<FIELDSET><LEGEND>%s</LEGEND>",key));
		writer.write("<TEXTAREA name=\"help\" value=\"\" tabindex=\"1\"></TEXTAREA>");
		writer.write("</FIELDSET>");		
	}	
	public void renderFormHeader(Writer writer, String key)  throws IOException {
		writer.write(String.format("<form name='%s' method='POST' action='%s/%s/%s/%s%s'>","form",
				getRootRef(),UserResource.resource,
				Reference.encode(session.getUser().getId()),mode,wizard.nextStep(step)));
	}
	public void renderFormContent(Writer writer, String key)  throws IOException {
		//writer.write(String.format("<FIELDSET><LEGEND>%s</LEGEND>",key));
		//writer.write("<INPUT name=\"next\" type=\"submit\" value=\"Next\" tabindex=\"1\">");
		//writer.write("</FIELDSET>");
	}	
	public void renderFormFooter(Writer writer,String key)  throws IOException {
		writer.write(String.format("</form>"));
	}
	public void renderResults(Writer writer,String key)  throws IOException {
		writer.write(String.format("<FIELDSET><LEGEND>%s</LEGEND>","Results"));

		writer.write("</FIELDSET>");	
	}	
	public void renderTabs(Writer writer)  throws IOException {
		
		writer.write("<div id=\"tabs4\">");
				
		for (String key: tabs) {

				Reference tab = new Reference(String.format("%s/%s/%s/%s%s/%s",
						getRootRef(),
						UserResource.resource,
						session.getUser().getId(),
						mode,
						step.getResource(),
						Reference.encode(key)));
				tab.setQuery(getRequest().getResourceRef().getQuery());
				writer.write(String.format("<li %s><a href='%s'><span>%s</span></a></li>",
							key.equals(tabIndex)?"id='current'":"",
							tab,key));	

		}
		//}
		writer.write("</div>");

	
		renderFormContent(writer,tabIndex);
		renderResults(writer,tabIndex);
		//renderErrorsTab(writer, tabIndex);

	}		
	public void footer(Writer output)  throws IOException  {

		Reference baseReference = getRequest()==null?null:getRequest().getRootRef();
		output.write("<div class=\"footer\">");
		output.write("<span class=\"right\">");
		output.write(String.format("<a href='http://www.opentox.org'><img src=%s/images/logo.png border='0' width='99' height='32'></a>",baseReference));
		output.write(String.format("<a href='http://ambit.sourceforge.net'><img src=%s/images/ambit-logo.png border='0' width='114' height='32'></a>&nbsp;",baseReference));
		//output.write(String.format("<a href='http://www.cefic.be'><img src=%s/images/logocefic.png border='0' width='115' height='60'></a>&nbsp;",baseReference));
		//output.write(String.format("<a href='http://www.cefic-lri.org'><img src=%s/images/logolri.png border='0' width='115' height='60'></a>&nbsp;",baseReference));
		
		output.write("<br>Developed by Ideaconsult Ltd. (2005-2010)<br>"); 
		output.write(String.format("Version:&nbsp;<a href='%s/meta/MANIFEST.MF' target=_blank alt='%s' title='Web application version'>%s</a><br>",
				getRequest().getRootRef(),
				version==null?"":version,
				version));
		output.write("  <A HREF=\"http://validator.w3.org/check?uri=referer\">");
		output.write(String.format("    <IMG SRC=\"%s/images/valid-html401-blue-small.png\" ALT=\"Valid HTML 4.01 Transitional\" TITLE=\"Valid HTML 4.01 Transitional\" HEIGHT=\"16\" WIDTH=\"45\" border=\"0\">",baseReference));
		output.write("  </A>&nbsp; ");
		output.write("<A HREF=\"http://jigsaw.w3.org/css-validator/check/referer\">");
		output.write(String.format("    <IMG SRC=\"%s/images/valid-css-blue-small.png\" TITLE=\"Valid CSS\" ALT=\"Valid CSS\" HEIGHT=\"16\" WIDTH=\"45\" border=\"0\">",baseReference));
		output.write("  </A>");

		output.write("</span>");		
		output.write("</div>");
		output.write("\n");
		output.write(jsGoogleAnalytics()==null?"":jsGoogleAnalytics());
		
		output.write("</body>");
		output.write("</html>");

	}		
	protected String getMeta() {
		return meta;
	}
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		return new OutputRepresentation(variant.getMediaType()) {
			@Override
			public void write(OutputStream out) throws IOException {
				OutputStreamWriter writer = null;
				try {
					writer = new OutputStreamWriter(out,"UTF-8");	  
					header(writer,getMeta());
					renderFormHeader(writer,tabIndex);
					top(writer);
					navigator(writer);
					help(writer);
					if (tabs.size()>1) renderTabs(writer);
					else {
						String key = tabs.get(0);
						renderFormHeader(writer,key);
						renderFormContent(writer,key);
						renderResults(writer,key);
						
					}
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					if ((x.getMessage()!=null) && x.getMessage().equals("Not Found")) {
						ResourceException xx = new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,
								"We did not find any matching entries for the search you performed in the OpenTox database. Please go back to Step 1 of your ToxPredict workflow and try again.");
						session.setError(step.getTitle(),xx);
						throw xx;
						
					} else throw new ResourceException(x);
				} finally {
					renderErrorsTab(writer, key);
					navigator(writer);
					renderFormFooter(writer,key);	

					footer(writer);
					writer.flush();
					out.close();
				}
			}
		};
	}
	protected Representation processForm(Representation entity, Variant variant)
			throws ResourceException {
		try {
			StepProcessor p = step.getProcessor();
			p.setBaseReference(getRequest().getRootRef());
			Form form = p.process(entity,session);
			//getRequest().getResourceRef().setQuery(form.getQueryString());
			//return get(variant);
			getResponse().redirectSeeOther(getRequest().getResourceRef());
			return null;
		} catch (StepException x) {
			session.setError(x.getKey(),x);
			getResponse().redirectSeeOther(getRequest().getReferrerRef());
			return null;			
		} catch (ResourceException x) {
			session.setError(step.getTitle(),x);
			getResponse().redirectSeeOther(getRequest().getReferrerRef());
			return null;
		} catch (Exception x) {
			if ((x.getCause()!=null) && (x.getCause() instanceof StepException)) {
				session.setError(
						((StepException)x.getCause()).getKey(),
						(Exception)x.getCause());
				getResponse().redirectSeeOther(getRequest().getReferrerRef());
				return null;					
			} else {
				session.setError(step.getTitle(),x);
				getResponse().redirectSeeOther(getRequest().getReferrerRef());
				return null;
			}

		}		
	}	
	protected Representation processMultipartForm(Representation entity, Variant variant) throws ResourceException {
		return get(variant);
	}	
	protected Representation processError(Representation entity, Variant variant, Status status) throws ResourceException {
		throw new ResourceException(status);
	}	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		
		if (session!=null) {
			session.getUser().setTimeStamp(step==null?null:
			String.format("<a href='%s'>%s</a>",getRequest().getResourceRef(),step.getDescription())
			);
			try {session.getUser().setClientinfo(getRequest().getClientInfo().getAddress());} catch (Exception x) {}
		}
		session.clearErrors();
		if (!entity.isAvailable())
			return processError(entity,variant,Status.CLIENT_ERROR_BAD_REQUEST);
		if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType()))
			return processForm(entity, variant);
		else if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),true))
			return processMultipartForm(entity, variant);
		else return processError(entity,variant,Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
	}
	@Override
	public String toString() {
		return String.format("%s&nbsp;%s",getApplication().getName(),step.getTitle());
	}

	
	public static String jsGoogleAnalytics() {
		if (jsGoogleAnalytics==null) try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					WizardResource.class.getClassLoader().getResourceAsStream("ambit2/fastox/config/googleanalytics.js"))
			);
			StringBuilder b = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
            	b.append(line);
            	b.append('\n');
            }
            jsGoogleAnalytics = b.toString();
			reader.close();
			
		} catch (Exception x) { jsGoogleAnalytics = null;}
		return jsGoogleAnalytics;
	}
	
	protected String getHelp() {
		if (helpResource == null) return "";
		InputStream in = getClass().getClassLoader().getResourceAsStream(
				String.format("ambit2/fastox/help/%s",helpResource));
		StringBuilder b = new StringBuilder();
	    BufferedReader input =  new BufferedReader(new InputStreamReader(in));
	      try {
	        String line = null; //not declared within while loop

	        while (( line = input.readLine()) != null){
	          b.append(line);
	          b.append(System.getProperty("line.separator"));
	        }
	      } catch (Exception x) {
	    	  
	      }  finally {
	        try { input.close(); } catch (Exception x) {};
	      }
	     return b.toString();

	}
	public int countEndpoints() throws Exception {
		
		return countObjects(QueryFactory.create(ModelTools.queryCountEndpoint,null,Syntax.syntaxARQ));
	}
	public int countObjects(String objectType) throws Exception {
		return countObjects(QueryFactory.create(String.format(ModelTools.queryCount,objectType),null,Syntax.syntaxARQ));
	}
	public int countObjects(Query q) throws Exception {
		QueryExecution qe = null;

		try {
			
			qe = QueryExecutionFactory.sparqlService(wizard.getService(SERVICE.ontology).toString(),q);
			
			ResultSet results = qe.execSelect();
			
			while (results.hasNext()) {
				
				QuerySolution solution = results.next();
				Iterator<String> i = solution.varNames();
				while (i.hasNext()) {
					return solution.getLiteral(i.next()).getInt();
				}
				
			}
			
		}catch (Exception x) {
			throw x;
		} finally {
			try {qe.close();} catch (Exception x) {}
		}		
		return 0;
	}		
	protected void writePageSize(String[] sizes, Writer writer, String key) throws IOException {
		writeSelectOption(sizes, sizes,writer, "max", "Number of hits", session.getPageSize())	;
		/*
		
		writer.write("<label for='max'>Number of hits</label><select name='max'>");
		for (String size:sizes)
			writer.write(String.format("<option %s value='%s'>%s</option>",
					session.getPageSize().equals(size)?"selected='yes'":"",size,size));			
		writer.write("</select>");
		*/
	}	
	protected void writeSelectOption(
			String[] options,
			Writer writer, String name, String caption, String selected) throws IOException {
		writeSelectOption(options,options, writer, name, caption, selected);
	}
	protected void writeSelectOption(
			String[] options,
			String[] optionsTitle,
			Writer writer, String name, String caption, String selected) throws IOException {
		
		writer.write(String.format("<label for='%s'>%s</label><select name='%s'>",name,caption,name));
		for (int i=0; i < options.length;i++) 
			writer.write(String.format("<option %s value='%s'>%s</option>",
					selected.equals(options[i])?"selected='yes'":"",options[i],optionsTitle[i]));			
		writer.write("</select>");
	}		
	
	public static String jsTableSorter(String tableid) {
		return String.format("<script type=\"text/javascript\">$(document).ready(function() {  $(\"#%s\").tablesorter({widgets: ['zebra'] }); } );</script>",tableid);
	}

	/*
	public String jsPager(String pagerid, String[] options) {
		Reference baseReference = getRequest()==null?null:getRequest().getRootRef();
		
		StringBuilder b = new StringBuilder();
		b.append(String.format("\n<div id=\"%s\" class=\"pager\">",pagerid));
		b.append("<form>\n");
		b.append(String.format("<img src=\"%s/images/first.png\" class=\"first\"/>\n",baseReference));
		b.append(String.format("<img src=\"%s/images/prev.png\" class=\"prev\"/>\n",baseReference));
		b.append(String.format("<input type=\"text\" class=\"pagedisplay\"/>\n",baseReference));
		b.append(String.format("<img src=\"%s/images/next.png\" class=\"next\"/>\n",baseReference));
		b.append(String.format("<img src=\"%s/images/last.png\" class=\"last\"/>\n",baseReference));
		b.append("	<select class=\"pagesize\">\n");
		String selected = "selected";
		for(String option : options) {
			b.append(String.format("<option selected=\"%s\"  value=\"%s\">%s</option>\n",
				selected,option,option));
			selected = "";
		}
		b.append("	</select>\n");
		b.append("</form>\n");
		b.append("</div>");
		return b.toString();
	}
	*/
}
