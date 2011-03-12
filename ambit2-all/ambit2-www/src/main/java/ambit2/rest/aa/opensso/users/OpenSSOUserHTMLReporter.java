package ambit2.rest.aa.opensso.users;

import java.io.Writer;
import java.util.Iterator;

import org.restlet.Request;
import org.restlet.data.Cookie;

import ambit2.rest.AmbitResource;
import ambit2.rest.ResourceDoc;
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
	public OpenSSOUserHTMLReporter(Request ref, ResourceDoc doc) {
		super(ref,doc);
	}
	@Override
	public void header(Writer output, Iterator<OpenSSOUser> query) {
		try {
			AmbitResource.writeHTMLHeader(output, "AMBIT", getRequest(),getDocumentation()
					);//,"<meta http-equiv=\"refresh\" content=\"10\">");

			output.write(AmbitResource.jsTableSorter("users","pager"));
			
			
			output.write("<table width='80%' class='tablesorter' id='users' border='0' cellpadding='0' cellspacing='1'>");
			output.write("<tbody>");
			output.write(String.format("<tr><td>OpenSSO service</td><td>%s</td></tr>",
						OpenSSOServicesConfig.getInstance().getOpenSSOService()));
			
		} catch (Exception x) {
			
		}
	}
	public void processItem(OpenSSOUser item, Writer output) {
		try {

			if (item.getToken()==null)  {
				output.write("<form method='post' action='?method=post'>");
				
				output.write(String.format("<tr><td>%s</td><td><input type='text' size='40' name='%s' value=''></td></tr>",
						"User name","user"));
				output.write(String.format("<tr><td>%s</td><td><input type='password' size='40' name='%s' value=''></td></tr>",
						"Password","password"));				
				output.write("<tr><td></td><td><input align='bottom' type=\"submit\" value=\"Log in\"></td></tr>");
				output.write("</form>");
			} else {
				output.write("<form method='post' action='?method=delete'>");
				output.write(String.format("<tr><td>%s</td><td>%s</td></tr>","User name",item.getUsername()));
				output.write(String.format("<tr><td>%s</td><td>%s</td></tr>","Token",item.getToken().toString()));
				
				output.write("<tr><td></td><td><input align='bottom' type=\"submit\" value=\"Log out\"></td></tr>");
				output.write("</form>");
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	};
	@Override
	public void footer(Writer output, Iterator<OpenSSOUser> query) {
		try {
			output.write("</table>");
			output.write("</tbody></table>");
			AmbitResource.writeHTMLFooter(output, "OpenSSO User", getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
	}
}
