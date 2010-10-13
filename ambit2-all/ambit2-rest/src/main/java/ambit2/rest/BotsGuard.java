package ambit2.rest;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Filter;

public class BotsGuard extends Filter {
	public static String[] filter = {
		"googlebot",
		"msnbot",
		"yahoo-slurp",
		"teoma",
		"twiceler",
		"gigabot",
		"scrubby",
		"robozilla",
		"nutch",
		"ia_archiver",
		"baiduspider",
		"naverbot",
		"yeti",
		"googlebot-image",
		"googlebot-mobile",
		"yahoo-mmcrawler",
		"psbot",
		"asterias",
		"yahoo-blogs",
		"YandexBot",
		"MJ12bot"

	};
	
	public static void checkForBots(Request request) throws ResourceException {
		if (request.getClientInfo()==null) return;
		if (request.getClientInfo().getAgent()==null) return;
		for (int i=0;i < filter.length;i++)
			if (request.getClientInfo().getAgent().toLowerCase().indexOf(filter[i])>=0)
					throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED,request.getClientInfo().getAgentName());
		
	}
	@Override
	protected int beforeHandle(Request request, Response response) {
		if (request.getClientInfo()==null) return CONTINUE;
		if (request.getClientInfo().getAgent()==null) return CONTINUE;
		for (int i=0;i < filter.length;i++)
			if (request.getClientInfo().getAgent().toLowerCase().indexOf(filter[i])>=0) {
				response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				return STOP;
			}
		return CONTINUE;
	}
}
