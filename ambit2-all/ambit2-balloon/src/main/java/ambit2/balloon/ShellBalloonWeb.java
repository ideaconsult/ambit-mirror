package ambit2.balloon;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import ambit2.base.external.ShellException;

/**
 * Same as {@link ShellBalloon}, but gets the BALOON_HOME var from config file
 * Used in web services.
 * @author nina
 *
 */
public class ShellBalloonWeb extends ShellBalloon {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2707542734736846L;


	public ShellBalloonWeb() throws ShellException {
		super();
	}

	
	@Override
	protected String getBalloonHome() throws ShellException{
		try {
			Properties properties = new Properties();
			URL uri = ShellBalloonWeb.class.getClassLoader().getResource("ambit2/rest/config/ambit2.pref");
			
			InputStream in = uri.openStream();
			if (in==null) throw new ShellException(null,String.format("Can't find %s",uri.toString()));
			properties.load(in);
			in.close();	
			String wheredragonlives = properties.getProperty(ShellBalloon.BALLOON_HOME);
			if (wheredragonlives==null) 
				throw new ShellException(null,String.format("Can't find where Balloon is located. No property %s in %s",ShellBalloon.BALLOON_HOME,uri.toString()));
			return wheredragonlives;
		} catch (ShellException x) {
			throw x;
		} catch (Exception x) {
			throw new ShellException(null,x);
		}
	}		
}
