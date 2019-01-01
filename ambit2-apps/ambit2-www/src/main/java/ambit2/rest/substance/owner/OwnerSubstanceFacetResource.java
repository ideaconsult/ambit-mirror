package ambit2.rest.substance.owner;

import java.util.Map;

import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;

import ambit2.db.substance.study.facet.OwnerFacet;
import ambit2.db.substance.study.facet.OwnerSubstanceStats;
import ambit2.rest.OpenTox;
import ambit2.rest.facet.AmbitFacetResource;

public class OwnerSubstanceFacetResource  extends AmbitFacetResource<OwnerFacet,OwnerSubstanceStats>  {
	public final static String owner = OpenTox.URI.substanceowner.getURI();
	public final static String idowner = OpenTox.URI.substanceowner.getKey();
	public final static String ownerID = OpenTox.URI.substanceowner.getResourceID();

	public final static String resource = "substance";
	
	public OwnerSubstanceFacetResource() {
		super();
		setHtmlbyTemplate(true);

	}
	@Override
	public String getTemplateName() {
		return "substanceowner.ftl";
	}
	
	@Override
	protected OwnerSubstanceStats createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		Object owneruuid = request.getAttributes().get(OwnerSubstanceFacetResource.idowner);
		OwnerSubstanceStats q = new OwnerSubstanceStats(
				String.format("%s%s/%s/structure",getRootRef(),OwnerSubstanceFacetResource.owner,owneruuid)
				);
		q.setFieldname(owneruuid==null?null:owneruuid.toString());
		return q;
	}
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("facet_title","Substances per owner");
		map.put("facet_tooltip","Click on the UUID links to show structures");
		map.put("facet_group","UUID");
		map.put("facet_subgroup","name");
		map.put("facet_count","Number of substances");
	}
}
