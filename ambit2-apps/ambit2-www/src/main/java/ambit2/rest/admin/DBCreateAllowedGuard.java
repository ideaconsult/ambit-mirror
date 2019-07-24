package ambit2.rest.admin;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.Authorizer;

import ambit2.base.config.AMBITConfigProperties;

public class DBCreateAllowedGuard extends Authorizer {
	protected static AMBITConfigProperties properties = new AMBITConfigProperties();

	@Override
	protected boolean authorize(Request arg0, Response arg1) {
		return properties.getBooleanPropertyWithDefault("database.create", AMBITConfigProperties.ambitProperties,
				false);

	}
}
