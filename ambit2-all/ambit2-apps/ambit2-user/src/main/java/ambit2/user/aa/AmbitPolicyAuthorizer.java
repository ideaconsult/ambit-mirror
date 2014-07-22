package ambit2.user.aa;

import java.util.List;

import net.idea.restnet.db.aalocal.policy.PolicyAuthorizer;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class AmbitPolicyAuthorizer extends PolicyAuthorizer {

	public AmbitPolicyAuthorizer(Context context, String configfile,
			String dbName) {
		super(context, configfile, dbName);
	}
	
	/**
	 * the resources not in this list are publicly readable (GET) and require user login for POST 
	 */
	enum _resources {
		feature {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		},
		algorithm,
		dataset {
			@Override
			boolean isProtected(Method method) {
				return true;
			}
			@Override
			int getMaxLevel() {
				return 3;
			}
			@Override
			boolean expectPolicies4IndividualResource() {
				return true;
			}
		},
		model,
		substance {
			@Override
			int getMaxLevel() {
				return 3;
			}
			@Override
			boolean expectPolicies4IndividualResource() {
				return true;
			}
		},
		//substanceowner,
		admin,
		user,
		myaccount {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		},
		login {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		},
		signin {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		},
		register {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		},
		forgotten {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		},		
		provider {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		};
		boolean isProtected(Method method) {
			return true;
		}
		boolean expectPolicies4IndividualResource() {
			return false;
		}
		int getMaxLevel() {
			return 2;
		}
	}
	
	
	@Override
	public boolean authorizeSpecialCases(Request request, Response response,List<String> uri) {
		try {
			if (Method.GET.equals(request.getMethod())) {
				Form headers = (Form) request.getAttributes().get("org.restlet.http.headers");
				if (headers!=null) {
					String ac = headers.getFirstValue("accept"); 
					if (ac != null && "application/x-java-serialized-object".equals(ac))
						return true;
				}
			}
		} catch (Exception x) {}
		
		if ("riap".equals(request.getResourceRef().getScheme())) return true;
		int depth = request.getResourceRef().getSegments().size();
		if (depth==1) return true; //home page
		if (depth > 2) depth = 3;
		StringBuilder resource = new StringBuilder();
		for (int i=0; i < depth; i++) {
			String segment = request.getResourceRef().getSegments().get(i);
			resource.append("/");resource.append(segment);
			if (i>0) {
				uri.add(resource.toString());	
			}
			if (i==1) try {
				_resources s = _resources.valueOf(segment);
				if (!s.isProtected(request.getMethod())) {
					return true;
				}
				if (!s.expectPolicies4IndividualResource()) {
					break;
				}
			} catch (Exception x) {
				if (Method.GET.equals(request.getMethod())) return true;
			}
		}
		return false;
	}

}
