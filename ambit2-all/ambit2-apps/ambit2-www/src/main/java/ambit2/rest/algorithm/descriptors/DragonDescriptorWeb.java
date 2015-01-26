package ambit2.rest.algorithm.descriptors;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import ambit2.base.external.ShellException;
import ambit2.dragon.DescriptorDragonShell;
import ambit2.dragon.DragonShell;


public class DragonDescriptorWeb extends DescriptorDragonShell {

	public DragonDescriptorWeb() throws ShellException {
		super();
	}
	
	@Override
    protected DragonShell createDragonShell() throws ShellException  {
	return new DragonShell() {
    		/**
	     * 
	     */
	    private static final long serialVersionUID = 9099725609913728923L;

		@Override
    		protected String getDragonHome() throws ShellException  {
    			return getDragonHomeFromConfig();
    		}
    	};
    }
	
	public synchronized String getDragonHomeFromConfig() throws ShellException   {
		try {
			Properties properties = new Properties();
			URL uri = DragonDescriptorWeb.class.getClassLoader().getResource("ambit2/rest/config/ambit2.pref");
			
			InputStream in = uri.openStream();
			if (in==null) throw new ShellException(null,String.format("Can't find %s",uri.toString()));
			properties.load(in);
			in.close();	
			String wheredragonlives = properties.getProperty(DragonShell.DRAGON_HOME);
			if (wheredragonlives==null) 
				throw new ShellException(null,String.format("Can't find where Dragon6 is located. No property %s in %s",DragonShell.DRAGON_HOME,uri.toString()));
			return wheredragonlives;
		} catch (ShellException x) {
			throw x;
		} catch (Exception x) {
			throw new ShellException(null,x);
		}
	}	
}
