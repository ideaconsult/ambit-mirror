package ambit2.user.aa;

import java.util.List;

import net.idea.restnet.db.aalocal.policy.PolicyAuthorizer;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
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
