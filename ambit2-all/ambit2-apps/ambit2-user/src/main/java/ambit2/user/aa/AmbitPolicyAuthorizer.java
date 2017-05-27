package ambit2.user.aa;

import java.util.List;

import net.idea.restnet.db.aalocal.policy.PolicyAuthorizer;
import net.idea.restnet.db.aalocal.policy.PolicyQuery;
import net.idea.restnet.i.aa.RESTPolicy;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;

import ambit2.user.policy.AmbitRESTPolicy;

public class AmbitPolicyAuthorizer extends PolicyAuthorizer<PolicyQuery> {
	protected int HOMEPAGE_DEPTH = 1;

	public AmbitPolicyAuthorizer(Context context, String configfile,
			String dbName, int HOMEPAGE_DEPTH) {
		super(context, configfile, null, dbName);
		this.HOMEPAGE_DEPTH = HOMEPAGE_DEPTH;
	}

	/**
	 * the resources not in this list are publicly readable (GET) and require
	 * user login for POST
	 */
	enum _resources {
		style {
			@Override
			boolean isProtected(Method method) {
				return false;
			}

			@Override
			boolean isStatic() {
				return true;
			}
		},
		jquery {
			@Override
			boolean isProtected(Method method) {
				return false;
			}

			@Override
			boolean isStatic() {
				return true;
			}
		},
		scripts {
			@Override
			boolean isProtected(Method method) {
				return false;
			}

			@Override
			boolean isStatic() {
				return true;
			}
		},
		report {
			@Override
			boolean isProtected(Method method) {
				return false;
			}

			@Override
			boolean isStatic() {
				return true;
			}
		},
		editor {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		},
		images {
			@Override
			boolean isProtected(Method method) {
				return false;
			}

			@Override
			boolean isStatic() {
				return true;
			}
		},
		chelp {
			@Override
			boolean isProtected(Method method) {
				return false;
			}

			@Override
			boolean isStatic() {
				return true;
			}
		},
		task {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		},
		ui {
			@Override
			boolean isProtected(Method method) {
				return !method.equals(Method.GET);
			}

			@Override
			boolean expectPolicies4IndividualResource() {
				return true;
			}
		},
		feature {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		},
		query {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		},
		compound {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		},
		depict {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		},
		demo {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		},
		qmap {
			@Override
			boolean isProtected(Method method) {
				return false;
			}
		},
		algorithm, dataset {
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
		proxy {
			@Override
			int getMaxLevel() {
				return 3;
			}
			@Override
			boolean expectPolicies4IndividualResource() {
				return true;
			}
			@Override
			boolean isProtected(Method method) {
				return true;
			}
			
		},
		model, substance, dir {
			@Override
			int getMaxLevel() {
				return 3;
			}

			@Override
			boolean expectPolicies4IndividualResource() {
				return true;
			}
		},
		bundle {
			@Override
			boolean isProtected(Method method) {
				return true;
			}

			// we'll get special filter for these
			@Override
			boolean expectPolicies4IndividualResource() {
				return false;
			}

			@Override
			int getMaxLevel() {
				return 1;
			}
		},
		// substanceowner,
		/*
		 * bundle {
		 * 
		 * @Override boolean isProtected(Method method) { return true; }
		 * 
		 * @Override int getMaxLevel() { return 3; }
		 * 
		 * @Override boolean expectPolicies4IndividualResource() { return true;
		 * } },
		 */
		admin, user, myaccount {
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

		boolean isStatic() {
			return false;
		}
	}

	@Override
	protected RESTPolicy initRESTPolicy() {
		return new AmbitRESTPolicy(HOMEPAGE_DEPTH);
	}

	@Override
	public boolean authorizeSpecialCases(Request request, Response response,
			List<String> uri) {

		/*
		 * try { if (Method.GET.equals(request.getMethod())) { Form headers =
		 * (Form) request.getAttributes().get("org.restlet.http.headers"); if
		 * (headers!=null) { String ac = headers.getFirstValue("accept"); if (ac
		 * != null && "application/x-java-serialized-object".equals(ac)) return
		 * true; } } } catch (Exception x) {}
		 */

		if ("riap".equals(request.getResourceRef().getScheme()))
			return true;
		int depth = request.getResourceRef().getSegments().size();
		if (depth == HOMEPAGE_DEPTH)
			return true; // home page
		if (depth > (HOMEPAGE_DEPTH + 1))
			depth = HOMEPAGE_DEPTH + 2;
		StringBuilder resource = new StringBuilder();
		int maxdepths_resource = depth;
		for (int i = 0; i < depth; i++) {
			String segment = request.getResourceRef().getSegments().get(i);
			resource.append("/");
			resource.append(segment);
			if (i >= HOMEPAGE_DEPTH) {
				if (i<=maxdepths_resource)
					uri.add(resource.toString());
			}
			if (i == HOMEPAGE_DEPTH)
				try {
					_resources s = _resources.valueOf(segment);
					maxdepths_resource = s.getMaxLevel();
					if (!s.isProtected(request.getMethod())) {
						return true;
					}
					if (!s.expectPolicies4IndividualResource()) {
						break;
					}
				} catch (Exception x) {
					if (Method.GET.equals(request.getMethod()))
						return true;
				}
		}
		return false;
	}

}
