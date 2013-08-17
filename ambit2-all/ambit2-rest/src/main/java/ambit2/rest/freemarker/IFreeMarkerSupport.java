package ambit2.rest.freemarker;

import java.util.Map;

import org.restlet.Request;

public interface IFreeMarkerSupport {

	  public java.lang.String getTemplateName();
	  public boolean isHtmlbyTemplate();
	  public void setHtmlbyTemplate(boolean arg0);
	  public void configureTemplateMap(Map<String, Object> map, Request request, FreeMarkerApplication app) ;

}
