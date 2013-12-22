package ambit2.rest.substance.study;

import java.util.Map;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;

import ambit2.db.substance.study.facet.OwnerSubstanceStats;
import ambit2.rest.OpenTox;
import ambit2.rest.facet.FacetResource;

public class OwnerSubstanceFacetResource  extends FacetResource<OwnerSubstanceStats>  {
	public final static String owner = OpenTox.URI.le.getURI();
	public final static String idowner = OpenTox.URI.le.getKey();
	public final static String ownerID = OpenTox.URI.le.getResourceID();

	public final static String resource = "substance";
	
	public OwnerSubstanceFacetResource() {
		super();
		setHtmlbyTemplate(true);

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
	public void configureTemplateMap(Map<String, Object> map) {
		super.configureTemplateMap(map);
		map.put("facet_title","Substances per owner");
		map.put("facet_tooltip","Click on the UUID links to show structures");
		map.put("facet_group","UUID");
		map.put("facet_subgroup","name");
		map.put("facet_count","Number of substances");
	}
}
