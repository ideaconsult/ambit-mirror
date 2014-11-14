package ambit2.rest.aa.opensso.users;

import java.io.Writer;
import java.util.Iterator;
import java.util.logging.Level;

import net.idea.restnet.c.ResourceDoc;

import org.restlet.Request;

import ambit2.rest.AmbitResource;
import ambit2.rest.aa.opensso.OpenSSOServicesConfig;
import ambit2.rest.aa.opensso.OpenSSOUser;
import ambit2.rest.algorithm.AllAlgorithmsResource;

/**
 * Generates HTML output for {@link AllAlgorithmsResource}
 * @author nina
 *
 */
public class OpenSSOUserHTMLReporter extends OpenSSOUsersURIReporter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7544605965468875232L;
	protected boolean collapsed = false;
	protected final static String securecookie_warning = "Secure cookie is used to transfer the OpenSSO token within a web browser.<br>You will only be able to access protected resources via web browser, if accessing via https://";
	protected final static String cookie_warning = "HTTP cookie is used to transfer the OpenSSO token between web browser and the server.";

	public OpenSSOUserHTMLReporter(Request ref, ResourceDoc doc) {
		super(ref,doc);
	}
	@Override
	public void header(Writer output, Iterator<OpenSSOUser> query) {
		try {
			AmbitResource.writeHTMLHeader(output, "AMBIT", getRequest(),getResourceRef(),getDocumentation()
					);//,"<meta http-equiv=\"refresh\" content=\"10\">");
			
			
			
		} catch (Exception x) {
			
		}
	}
	public void processItem(OpenSSOUser item, Writer output) {
		try {
			StringBuilder content = new StringBuilder();
			content.append("<table width='80%' id='users' border='0' cellpadding='0' cellspacing='1'>");
			content.append("<tbody>");
			
			content.append(String.format("<tr><th align='right' title='OpenTox Authentication and Authorisation is managed by OpenAM http://forgerock.com/openam.html'><a href='http://forgerock.com/openam.html' target=_blank>OpenAM</a> service:&nbsp;</th><td>%s</td></tr>",
						OpenSSOServicesConfig.getInstance().getOpenSSOService()));

			if (item.getToken()==null)  {
				content.append("<form method='post' action='?method=post'>");
				
				content.append(String.format("<tr><th align='right'>%s</th><td align='left'><input type='text' size='40' name='%s' value=''></td></tr>",
						"OpenTox User name:&nbsp;","user"));
				content.append(String.format("<tr><th align='right'>%s</th><td><input type='password' size='40' name='%s' value=''></td></tr>",
						"Password:&nbsp;","password"));
				content.append(String.format("<tr><td title=''></td><td><input type=CHECKBOX name='subjectid_secure' %s>Use secure cookie for the OpenSSO token</option></td></tr>",
						item.isUseSecureCookie()?"SELECTED CHECKED":""));
				content.append("<tr><td></td><td><input align='bottom' type=\"submit\" value=\"Log in\"></td></tr>");
				
				content.append("</form>");

				content.append("</tbody></table>");
			} else {
				content.append("<form method='post' action='?method=delete'>");
				content.append(String.format("<tr><th align='right'>%s</th><td>%s</td></tr>","User name:&nbsp;",item.getUsername()));
				content.append(String.format("<tr><th align='right'>%s</th><td>%s</td></tr>","Token:&nbsp;",item.getToken().toString()));
				

				String warning = String.format("<h5><font color='red'>%s</font></h5>",item.isUseSecureCookie()?securecookie_warning:cookie_warning);
				content.append(String.format("<tr><td></td><td>%s</td></tr>",warning));

				
				content.append("<tr><td></td><td><input align='bottom' type=\"submit\" value=\"Log out\"></td></tr>");
				content.append("</form>");
				
				content.append("</tbody></table><hr/>");
				content.append(String.format("<a href='%s/bookmark/%s'>My workspace</a>",getRequest().getRootRef(),item.getUsername(),item.getUsername()));
				content.append(String.format("&nbsp;&nbsp;<a href='%s/admin/policy'>View/Define access rights</a>",getRequest().getRootRef()));
				content.append(String.format("&nbsp;&nbsp;<a href='%s/admin'>Admin</a>",getRequest().getRootRef()));
			}
			
			output.write(AmbitResource.printWidget(
			"OpenTox login&nbsp;<a href='http://opentox.org/join_form' title='If you do not have an OpenTox user, please sign in at opentox.org' target='_blank'>Sign In</a>",
			content.toString()));
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
	};
	@Override
	public void footer(Writer output, Iterator<OpenSSOUser> query) {
		try {
			
			AmbitResource.writeHTMLFooter(output, "OpenSSO User", getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
	}
}
