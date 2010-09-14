package ambit2.rest.task.dsl;

import org.restlet.data.Reference;

public class OTCompounds extends OTContainers<OTCompound> {

	@Override
	public OTCompound createItem(Reference uri) throws Exception {
		return OTCompound.compound(uri);
	}

	@Override
	public OTCompound createItem(String uri) throws Exception {
		return OTCompound.compound(uri);
	}
	@Override
	public OTContainers<OTCompound> delete(OTCompound item) throws Exception {
		return null;
	}

	@Override
	protected String getParamName() throws Exception {
		return null;
	}

}
