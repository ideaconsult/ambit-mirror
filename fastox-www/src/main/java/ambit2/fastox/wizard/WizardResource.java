package ambit2.fastox.wizard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ambit2.fastox.steps.StepProcessor;
import ambit2.fastox.steps.FastoxStepResource.params;
import ambit2.fastox.wizard.Wizard.WizardMode;
	
public abstract class WizardResource extends ServerResource {
	protected static String jsGoogleAnalytics = null;
	protected WizardStep step;
	protected Hashtable<String,Form> forms;
	protected String tabIndex = null;
	protected WizardMode mode;
	protected String meta;
	protected Wizard wizard;
	public static final String key = "mode";
	protected int stepIndex;
	
	public WizardResource(int stepIndex) throws ResourceException {
		super();
		this.stepIndex = stepIndex;
		setMode(WizardMode.A);
	}
	public void setMode(WizardMode mode) {
		this.mode = mode;
	}
	protected Hashtable<String, Form> createForms() {
		Hashtable<String, Form> forms = new Hashtable<String, Form>();
		forms.put(step.getTitle(),new Form());
		forms.put("Errors",new Form());
		return forms;
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		getVariants().add(new Variant(MediaType.TEXT_HTML));
		Object t = getRequestAttributes().get(WizardStep.tab);
		tabIndex = t==null?getDefaultTab():Reference.decode(t.toString());
		
		try {
			setMode(WizardMode.valueOf(getRequest().getAttributes().get(key).toString()));
		} catch (Exception x) { setMode(WizardMode.A);}

		wizard = Wizard.getInstance(mode);
		this.step = wizard.getStep(stepIndex);
		forms = createForms();
		meta = "";
		
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
		
		w.write("<meta name=\"mssmarttagspreventparsing\" content=\"true\" />");
		w.write("<meta http-equiv=\"imagetoolbar\" content=\"false\" />");

		w.write(String.format("<style type=\"text/css\" media=\"all\">\n@import \"%s/style/global.css\";\n</style>",baseReference));

		w.write("<meta name=\"robots\" content=\"index,follow\"><META NAME=\"GOOGLEBOT\" CONTENT=\"index,FOLLOW\">");
		w.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/jme/jme.js\"></script>\n",baseReference));

		
		

		w.write("</head>\n");
		w.write("<body>");		
	}
	public void navigator(Writer writer) throws IOException {
		
		writer.write("<table width='99%' border='0'>");
		writer.write("<tr>");
		writer.write("<td align='left'>");
		writer.write(String.format("<a href='%s' title='ToxPredict, OpenTox Demo application'><img src='%s%s' alt='ToxPredict' title='%s'></a>",
				getRootRef(),
				getRootRef(),
				"/images/ToxPredict_rgb_72.png",
				"ToxPredict",
				"ToxPredict, OpenTox Demo application"));	
		writer.write("</td>");
		writer.write("<td align='right'>");
		writer.write(String.format("<a href='%s/help' target=_blank title='Help'>Help</a>",
				getRequest().getOriginalRef(),
				"Help"));		
		writer.write("</td>");
		writer.write("<tr>");
		
		writer.write("</table>");
		writer.write("<ul id=\"mainNav\" class=\"wizardStep\">\n");
		
		Form query = getRequest().getResourceRef().getQueryAsForm();
		
		if (step.getIndex()>0)
		for (int i=1; i < wizard.size(); i++) {
			WizardStep thestep = wizard.getStep(i) ;
			Reference stepRef = new Reference(getRequest().getRootRef().toString()+"/"+mode+thestep.getResource());
			stepRef.setQuery(query.getQueryString());
			
			if (i < step.getIndex()) {
				writer.write(String.format(
						"<li class=\"%s\"><a href=\"%s\" title=\"\"><em>Step %d: %s</em><span>%s</span></a></li>\n",
						(i+1)==step.getIndex()?"lastDone":"done",
						stepRef,
						i,
						thestep.getTitle(),
						thestep.getDescription()
						));			
			} else 
			if (i == step.getIndex()) {
				writer.write(String.format(
						"<li class=\"current %s\"><a title=\"\"><em>Step %d: %s</em><span>%s</span></a></li>\n",
						(i==(wizard.size()-1))?"mainNavNoBg\"":"",
						i,
						step.getTitle(),
						step.getDescription()
						));				
			} else if (i > step.getIndex()) {
				writer.write(String.format(
						"<li %s><a title=\"\"><em>Step %d: %s</em><span>%s</span></a></li>\n",
						(i==(wizard.size()-1))?"class=\"mainNavNoBg\"":"",
						i,
						thestep.getTitle(),
						thestep.getDescription()						
						));					
			}
			


		}
		/*
		writer.write(
		"<li class=\"lastDone\"><a href=\"/\" title=\"\"><em>Step 1: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>\n"+
		"<li class=\"current\"><a title=\"\"><em>Step 2: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>\n"+
		"<li><a title=\"\"><em>Step 3: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>\n"+
		"<li><a title=\"\"><em>Step 4: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>\n"+
		"<li><a title=\"\"><em>Step 5: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>\n"+
		"<li class=\"mainNavNoBg\"><a title=\"\"><em>Step 6: XXXXXXXX</em> <span>Et nequ a quam turpis duisi</span></a></li>\n"+
		*/
		writer.write("</ul>\n");
		writer.write("<div class=\"clearfloat\">&nbsp;</div>");

	
		//<div class="clearfloat">&nbsp;</div>
	}
	public void renderErrorsTab(Writer writer, String key)  throws IOException {
		writer.write(String.format("<FIELDSET><LEGEND>%s</LEGEND>",key));
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String[] errors = form.getValuesArray(params.errors.toString());
		if (errors.length>0) 
			for (String error:errors)
				writer.write(String.format("<div class='errors'>%s</div>",error));
		else 
			writer.write(String.format("<div class='errors'>%s</div>","None"));
		writer.write("</FIELDSET>");
	}
	public void renderHelpTab(Writer writer, String key)  throws IOException {
		writer.write(String.format("<FIELDSET><LEGEND>%s</LEGEND>",key));
		writer.write("<TEXTAREA name=\"help\" value=\"\" tabindex=\"1\"></TEXTAREA>");
		writer.write("</FIELDSET>");		
	}	
	public void renderFormHeader(Writer writer, String key)  throws IOException {
		writer.write(String.format("<form name='%s' method='POST' action='%s/%s%s'>","form",getRootRef(),mode,wizard.nextStep(step)));
	}
	public void renderFormContent(Writer writer, String key)  throws IOException {
		//writer.write(String.format("<FIELDSET><LEGEND>%s</LEGEND>",key));
		writer.write("<INPUT name=\"next\" type=\"submit\" value=\"Next\" tabindex=\"1\">");
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
		
		writer.write("<h4>");
		Form form = null;
		Enumeration<String> keys = forms.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (key.equals(tabIndex)) writer.write("[");
			
			Reference tab = new Reference(String.format("%s/%s%s/%s",getRootRef(),mode,step.getResource(),Reference.encode(key)));
			tab.setQuery(getRequest().getResourceRef().getQuery());
			writer.write(String.format("<a href='%s'>%s</a>&nbsp;",tab,key));	
			if (key.equals(tabIndex)) writer.write("]");
			form = (form==null)?forms.get(key):form;
		}
		renderFormHeader(writer,tabIndex);
		writer.write("<INPUT name=\"next\" type=\"submit\" value=\"Next\" tabindex=\"1\">");
		writer.write("</h4>");

		if ("Errors".equals(tabIndex)) renderErrorsTab(writer, tabIndex);
		else if ("Help".equals(tabIndex)) renderHelpTab(writer, tabIndex);
		else {
			
			renderFormContent(writer,tabIndex);
			renderResults(writer,tabIndex);
			
		}
		renderFormFooter(writer,tabIndex);
	}		
	public void footer(Writer output)  throws IOException  {
	
		Reference baseReference = getRequest()==null?null:getRequest().getRootRef();
		output.write("<div class=\"footer\">");

		output.write("<span class=\"right\">");
		output.write(String.format("<a href='http://www.opentox.org'><img src=%s/images/logo.png border='0' width='115' height='60'></a>",baseReference));
		output.write(String.format("<a href='http://ambit.sourceforge.net'><img src=%s/images/ambit-logo.png border='0' width='115' height='50'></a>&nbsp;",baseReference));
		//output.write(String.format("<a href='http://www.cefic.be'><img src=%s/images/logocefic.png border='0' width='115' height='60'></a>&nbsp;",baseReference));
		//output.write(String.format("<a href='http://www.cefic-lri.org'><img src=%s/images/logolri.png border='0' width='115' height='60'></a>&nbsp;",baseReference));
		
		output.write("<br>Developed by Ideaconsult Ltd. (2005-2010)"); 
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
					navigator(writer);
					if (forms.size()>1) renderTabs(writer);
					else {
						String key = forms.keys().nextElement();
						if ("Errors".equals(key)) renderErrorsTab(writer, key);
						else if ("Help".equals(key)) renderHelpTab(writer, key);
						else {
							renderFormHeader(writer,key);
							renderFormContent(writer,key);
							renderResults(writer,key);
							renderFormFooter(writer,key);
						}
					}
				} catch (Exception x) {
					x.printStackTrace();
				} finally {
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
			Form form = p.process(entity);
			getRequest().getResourceRef().setQuery(form.getQueryString());
			return get(variant);	
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
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
		if (!entity.isAvailable())
			return processError(entity,variant,Status.CLIENT_ERROR_BAD_REQUEST);
		if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType()))
			return processForm(entity, variant);
		else if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType()))
			return processMultipartForm(entity, variant);
		else return processError(entity,variant,Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
	}
	@Override
	public String toString() {
		return String.format("%s&nbsp;%s",getApplication().getName(),step.getTitle());
	}

	/*
	protected void breadcrumbs() {
		<h2>5 step (showing each state of the step menu)</h2>	
		<ul id="mainNav" class="fiveStep">
			<li class="current"><a title=""><em>Step 1: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li><a title=""><em>Step 2: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li><a title=""><em>Step 3: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>

			<li><a title=""><em>Step 4: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li class="mainNavNoBg"><a title=""><em>Step 5: XXXXXXXX</em> <span>Et nequ a quam turpis duisi</span></a></li>
		</ul>
		
		<div class="clearfloat">&nbsp;</div>
		
		<ul id="mainNav" class="fiveStep">
			<li class="lastDone"><a href="/" title=""><em>Step 1: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>

			<li class="current"><a title=""><em>Step 2: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li><a title=""><em>Step 3: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li><a title=""><em>Step 4: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li class="mainNavNoBg"><a title=""><em>Step 5: XXXXXXXX</em> <span>Et nequ a quam turpis duisi</span></a></li>

		</ul>
		
		<div class="clearfloat">&nbsp;</div>
		
		<ul id="mainNav" class="fiveStep">
			<li class="done"><a href="/" title=""><em>Step 1: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li class="lastDone"><a href="/" title=""><em>Step 2: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li class="current"><a title=""><em>Step 3: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>

			<li><a title=""><em>Step 4: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li class="mainNavNoBg"><a title=""><em>Step 5: XXXXXXXX</em> <span>Et nequ a quam turpis duisi</span></a></li>
		</ul>
		
		<div class="clearfloat">&nbsp;</div>
		
		<ul id="mainNav" class="fiveStep">
			<li class="done"><a href="/" title=""><em>Step 1: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>

			<li class="done"><a href="/" title=""><em>Step 2: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li class="lastDone"><a href="/" title=""><em>Step 3: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li class="current"><a title=""><em>Step 4: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li class="mainNavNoBg"><a title=""><em>Step 5: XXXXXXXX</em> <span>Et nequ a quam turpis duisi</span></a></li>

		</ul>
		
		<div class="clearfloat">&nbsp;</div>
		
		<ul id="mainNav" class="fiveStep">
			<li class="done"><a href="/" title=""><em>Step 1: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li class="done"><a href="/" title=""><em>Step 2: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li class="done"><a href="/" title=""><em>Step 3: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>

			<li class="lastDone"><a href="/" title=""><em>Step 4: XXXXXXXX</em><span>Et nequ a quam turpis duisi</span></a></li>
			<li class="mainNavNoBg current"><a title=""><em>Step 5: XXXXXXXX</em> <span>Et nequ a quam turpis duisi</span></a></li>
		</ul>
		
		<div class="clearfloat">&nbsp;</div>

	}
	*/
	
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
}
