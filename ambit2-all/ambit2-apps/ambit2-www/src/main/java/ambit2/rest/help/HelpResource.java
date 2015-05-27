package ambit2.rest.help;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CookieSetting;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.algorithm.CatalogResource;

public class HelpResource extends CatalogResource<String>  {
	protected List<String> topics = new ArrayList<String>();
	protected enum categories {
		createstruc,
		uploadstruc,
		uploadprops,
		predict,
		query,
		compound,
		dataset,
		algorithm,
		model,
		feature,
		task,
		dataprep,
		qmap,
		substance,
		users,
		login,
		register,
		myprofile,
		pwd_forgotten,
		toxtree,
		policy,
		ra,
		ontobucket
	}
	public HelpResource() {
		super();
		setHtmlbyTemplate(true);
		for (categories category: categories.values()) topics.add(category.name());
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		setFrameOptions("SAMEORIGIN");
		if (isHtmlbyTemplate()) {
			CookieSetting cS = new CookieSetting(0, "subjectid", getToken());
			cS.setPath("/");
	        this.getResponse().getCookieSettings().add(cS);
	        return getHTMLByTemplate(variant);
    	} else				
    		return super.get(variant);
	}
	
	@Override
	protected Representation getHTMLByTemplate(Variant variant) throws ResourceException {

		Map<String, Object> map = new HashMap<String, Object>();
		configureTemplateMap(map,getRequest(),(IFreeMarkerApplication)getApplication());
		return toRepresentation(map, getTemplateName(), variant.getMediaType());

	}
	
	@Override
	public String getTemplateName() {
		Object key = getRequestAttributes().get("key");
		try {
			return key==null?"help/about.ftl":
				String.format("help/%s.ftl",categories.valueOf(key.toString()));
		} catch (Exception x) {return "help/about.ftl";}
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		getVariants().clear();
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		return topics.iterator();
	}

}