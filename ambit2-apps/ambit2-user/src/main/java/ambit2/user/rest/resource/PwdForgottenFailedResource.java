package ambit2.user.rest.resource;

public class PwdForgottenFailedResource extends AMBITRegistrationNotifyResource {

	public PwdForgottenFailedResource() {
		super();
	}

	@Override
	public String getTemplateName() {
		return "a/pwd_forgotten_failed.ftl";
	}

	
}
