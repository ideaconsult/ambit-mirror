package ambit2.rest.aa;

import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.security.RoleAuthorizer;

import ambit2.rendering.StructureEditorProcessor;

/**
 * Modified to allow GET always
 * 
 * @author nina
 * 
 */
public class UpdateAuthorizer extends RoleAuthorizer {

	public UpdateAuthorizer() {
		super();
	}

	@Override
	public boolean authorize(Request request, Response response) {
		if (Method.GET.equals(request.getMethod()))
			return true;
		try {
			String segment = request.getResourceRef().getLastSegment();
			StructureEditorProcessor._commands.valueOf(segment);
			List<String> s = request.getResourceRef().getSegments();
			// enable /ui/layout , /ui/aromatize /ui/dearomatize
			if ("ui".equals(s.get(s.size() - 2)))
				return true;
		} catch (Exception x) {
		}

		return super.authorize(request, response);
	}

}