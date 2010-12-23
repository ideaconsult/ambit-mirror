package ambit2.rest.aa.opensso;

import org.restlet.data.ChallengeScheme;


public class OpenSSOChallengeScheme  {
	protected static final String _OpenSSO = "OpenSSO";
    public static final ChallengeScheme OpenSSO = new ChallengeScheme(_OpenSSO,_OpenSSO,_OpenSSO);
}
