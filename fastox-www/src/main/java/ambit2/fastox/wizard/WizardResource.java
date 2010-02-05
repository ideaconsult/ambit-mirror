package ambit2.fastox.wizard;

import java.io.IOException;
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

public abstract class WizardResource extends ServerResource {
	public static final String tab = "tab";
	public static final String resource = "/";
	protected String nextStep;
	protected String prevStep;
	protected String title;
	protected Hashtable<String,Form> forms;
	protected String tabIndex = null;
	protected String dataset_service = "http://ambit.uni-plovdiv.bg:8080/ambit2/dataset";
	protected String compound_service = "http://ambit.uni-plovdiv.bg:8080/ambit2/compound";
	protected String ontology_service = "http://ambit.uni-plovdiv.bg:8080/ontology";
	protected String meta;
	//"http://localhost:8081";
	
	public WizardResource(String title,String prevStep,String nextStep) {
		super();
		this.title = title;
		this.nextStep = nextStep;
		this.prevStep = prevStep;
		forms = createForms();
		meta = "";

	}
	
	protected Hashtable<String, Form> createForms() {
		Hashtable<String, Form> forms = new Hashtable<String, Form>();
		forms.put(title,new Form());
		forms.put("Errors",new Form());
		forms.put("Help",new Form());
		return forms;
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		getVariants().add(new Variant(MediaType.TEXT_HTML));
		Object t = getRequestAttributes().get(tab);
		tabIndex = t==null?getDefaultTab():Reference.decode(t.toString());
		
	}
	protected String getDefaultTab() {
		return "Help";
	}
	public void header(Writer w,String meta) throws IOException {
		
		Reference baseReference = getRequest()==null?null:getRequest().getRootRef();
		w.write(
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
			);
		w.write(String.format("<html><head><title>%s</title>%s\n",title,meta));
		w.write(String.format("<link href=\"%s/style/ambit.css\" rel=\"stylesheet\" type=\"text/css\">",baseReference));
		w.write("<meta name=\"robots\" content=\"index,follow\"><META NAME=\"GOOGLEBOT\" CONTENT=\"index,FOLLOW\">");
		w.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		//w.write(String.format("<script type=\"text/javascript\" src=\"%s/js/dojo.js.uncompressed\" djConfig=\"parseOnLoad:true, isDebug:true\"></script>\n",baseReference));
		w.write(String.format("<script type=\"text/javascript\" src=\"%s/jme/jme.js\"></script>\n",baseReference));
//		w.write("<script language=\"JavaScript\">\nvar smiles = \"\";\n var jme = \"0 0\"></script>\n");

		w.write("</head>\n");
		w.write("<body>");		
	}
	public void navigator(Writer writer) throws IOException {
	//	writer.write(String.format("<h3>%s</h3>",toString()));
		writer.write(String.format("<a href='%s'>%s</a>&nbsp;",getRootRef(),"Home"));		
		if (prevStep!=null)
		writer.write(String.format("<a href='%s%s'>%s</a>",getRootRef(),prevStep,"Back"));
	}
	public void renderErrorsTab(Writer writer, String key)  throws IOException {
		writer.write(String.format("<FIELDSET><LEGEND>%s</LEGEND>",key));
		writer.write("<TEXTAREA name=\"errors\" value=\"\" tabindex=\"1\"></TEXTAREA>");
		writer.write("</FIELDSET>");
	}
	public void renderHelpTab(Writer writer, String key)  throws IOException {
		writer.write(String.format("<FIELDSET><LEGEND>%s</LEGEND>",key));
		writer.write("<TEXTAREA name=\"help\" value=\"\" tabindex=\"1\"></TEXTAREA>");
		writer.write("</FIELDSET>");		
	}	
	public void renderFormHeader(Writer writer, String key)  throws IOException {
		writer.write(String.format("<form name='%s' method='POST' action='%s%s'>","form",getRootRef(),nextStep));
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
			
			Reference tab = new Reference(String.format("%s%s/%s",getRootRef(),getTopRef(),Reference.encode(key)));
			tab.setQuery(getRequest().getResourceRef().getQuery());
			writer.write(String.format("<a href='%s'>%s</a>&nbsp;",tab,key));	
			if (key.equals(tabIndex)) writer.write("]");
			form = (form==null)?forms.get(key):form;
		}
		writer.write("</h4>");

		if ("Errors".equals(tabIndex)) renderErrorsTab(writer, tabIndex);
		else if ("Help".equals(tabIndex)) renderHelpTab(writer, tabIndex);
		else {
			renderFormHeader(writer,tabIndex);
			renderFormContent(writer,tabIndex);
			renderResults(writer,tabIndex);
			renderFormFooter(writer,tabIndex);
		}
	}		
	public void footer(Writer writer)  throws IOException  {
		writer.write("</body>");
		writer.write("</html>");
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
	protected Representation processForm(Representation entity, Variant variant) throws ResourceException {
		return get(variant);
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
		return String.format("%s&nbsp;%s",getApplication().getName(),title);
	}
	protected String getTopRef() {
		return resource;
	}
}
