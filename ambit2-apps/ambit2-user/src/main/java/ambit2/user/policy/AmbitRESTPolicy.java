package ambit2.user.policy;

import java.net.URI;

import net.idea.restnet.i.aa.RESTPolicy;

public class AmbitRESTPolicy extends RESTPolicy {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7197547504927875074L;
	protected int HOMEPAGE_DEPTH = 1;
	public AmbitRESTPolicy(int HOMEPAGE_DEPTH ) {
		super();
		this.HOMEPAGE_DEPTH= HOMEPAGE_DEPTH;
	}
	@Override
	public String[] splitURI(String href) throws Exception {
		if (HOMEPAGE_DEPTH == 0) {
			URI uri = new URI(href);
			return new String[] { "", uri.getPath()};
		} else
			return super.splitURI(href);
	}
}
