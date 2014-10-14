package ambit2.rest.routers.misc;

import org.restlet.Context;

import ambit2.rest.dataEntry.DataEntryResource;
import ambit2.rest.routers.MyRouter;

/**
 *  Data entries (i.e. dataset rows)
 *  /dataset/{id}/dataEntry , as in opentox.owl
 *  
 *  /compound/{id}/dataEntry
 */	
public class DataEntryRouter extends MyRouter {
	
	public DataEntryRouter(Context context) {
		super(context);
		attachDefault(DataEntryResource.class);
		attach(String.format("/{%s}", DataEntryResource.resourceKey),DataEntryResource.class);
	}

}
