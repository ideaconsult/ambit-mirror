package ambit2.rest.structure.diagram;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rendering.CompoundImageTools.Mode2D;

public class CDKDepictVariants extends CDKDepict {

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		this.getVariants().clear();
		this.getVariants().add(new Variant(MediaType.IMAGE_PNG));
	}	
	@Override
	public Representation get(Variant variant) {
		setFrameOptions("SAMEORIGIN");
		Object mode = getRequest().getAttributes().get("mode");
			try {
				displayMode = Mode2D.valueOf(mode.toString());
			} catch (Exception x) {displayMode = Mode2D.any;}
		return process(variant);
	}

	
}
