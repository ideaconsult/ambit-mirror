package ambit2.user.rest.resource;

import net.idea.restnet.aa.local.UserLoginFormResource;
import net.idea.restnet.aa.resource.AdminResource;

public class Resources {
	public static final String BASE_URL = "BASE_URL";
	public static final String dataset = "/dataset";
	public static final String algorithm = "/algorithm";
	public static final String model = "/model";
	public static final String search = "/search";
	public static final String endpoint = "/endpoint";
	public static final String project = "/project";
	public static final String organisation = "/organisation";
	public static final String user = "/user";
	public static final String role = "/role";
	public static final String restpolicy = "/restpolicy";
	public static final String versions = "/versions";
	public static final String previous = "/previous";
	public static final String authors = "/authors";
	public static final String attachment = "/attachment";
	public static final String login = String.format("/%s",UserLoginFormResource.resource);
	public static final String myaccount = "/myaccount";
	public static final String register = "/register";
	public static final String confirm = "/confirm";
	public static final String notify = "/notify";
	public static final String admin = String.format("/%s",AdminResource.resource);
	public static final String alert = "/alert";
	public static final String reset = "/reset";
	public static final String forgotten = "/forgotten";
	public static final String failed = "/failed";
	public static final String bundle = "/bundle";
	public static final String help = "/help";

	
	public final static String AMBIT_LOCAL_USER="aa.local.admin.name";
	public final static String AMBIT_LOCAL_PWD="aa.local.admin.pass";
}
