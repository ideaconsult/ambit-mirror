package org.opentox.aa.opensso;

import java.io.InputStream;
import java.util.Properties;

import org.opentox.aa.exception.AAException;
import org.opentox.aa.exception.AAPropertiesException;

/**
 * Configuration for OpenSSO authn, authz and policy services
 * @author nina
 *
 */
public class AAServicesConfig {
	
	private static AAServicesConfig ref;
	protected Properties properties;
	
	
	public static enum CONFIG {
		opensso {
			@Override
			public String getDescription() {
				return "authentication";
			}
		},
		user {
			@Override
			public String getDescription() {
				return "test user";
			}
		},
		pass {
			@Override
			public String getDescription() {
				return "test user password";
			}
		},

		policy {
			@Override
			public String getDescription() {
				return "policy";
			}					
		};		

		public String getKey() {
			return String.format("aa.%s",toString());
		}
		public String getValue(Properties properties) {
			try {
				if (properties == null) return null;
				Object o = properties.get(getKey());
				return o==null?null:o.toString();
			} catch (Exception x) {
				return getDefaultValue();
			}
		}
		public String getDefaultValue() {
			return null;
		}
		public abstract String getDescription() ;

	}	
	private AAServicesConfig() throws AAException {
		properties = new Properties();
		InputStream in = getClass().getClassLoader().getResourceAsStream("org/opentox/aa/config/config.prop");
		try {
			properties.load(in);
		} catch (AAException x) {
			throw x;
		} catch (Exception x) {
			throw new AAPropertiesException(x);
		} finally {
			try {in.close();} catch (Exception x) {}
		}		
	}	

	public static synchronized AAServicesConfig getSingleton() throws AAException  {
	    if (ref == null)
	        ref = new AAServicesConfig();		
	    return ref;
	}
	
	public String getTestUser() throws AAException  {
	    return getConfig(CONFIG.user);
	}	
		
	public String getTestUserPass() throws AAException  {
	    return getConfig(CONFIG.pass);
	}	
	
	public  String getOpenSSOService() throws AAException  {
	    return getConfig(CONFIG.opensso);
	}	
	public  String getPolicyService() throws AAException  {
	    return getConfig(CONFIG.policy);
	}	
	public Object clone() throws CloneNotSupportedException {
	    throw new CloneNotSupportedException(); 
    }
	protected  synchronized String getConfig(CONFIG config) throws AAException  {
		return properties==null?null:config.getValue(properties);
	}	
}
