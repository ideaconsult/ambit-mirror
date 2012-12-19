package ambit2.rest.admin;

import java.util.Arrays;
import java.util.logging.Logger;

import org.restlet.security.Authorizer;

public abstract class SimpleGuard extends Authorizer {
	public enum SimpleGuards {
		ip {
			@Override
			public SimpleGuard getGuard(String[] allowed, Logger logger) {
				return new SameIPGuard(allowed,logger);
			}
		},
		referer {
			@Override
			public SimpleGuard getGuard(String[] allowed, Logger logger) {
				return new SameRefererGuard(allowed,logger);
			}
		};
		public abstract SimpleGuard getGuard(String[] allowed, Logger logger);
	};
	String[] allowed = null;
	protected Logger logger;
	
	public SimpleGuard(Logger logger) {
		this(null,logger);
	}
	public SimpleGuard(String[] allowed, Logger logger) {
		super();
		if (allowed==null)
			this.allowed = new String[] {};
		else
			this.allowed = allowed;
		Arrays.sort(allowed);
		this.logger = logger;
	}

}
