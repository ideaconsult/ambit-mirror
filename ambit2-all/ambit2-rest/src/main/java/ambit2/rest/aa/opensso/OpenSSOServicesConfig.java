package ambit2.rest.aa.opensso;

import org.opentox.aa.exception.AAException;

import ambit2.rest.config.AMBITAppConfigProperties;

/**
 * Configuration for OpenSSO authn, authz and policy services
 * @author nina
 *
 */
public class OpenSSOServicesConfig {
	
	private static OpenSSOServicesConfig ref;
	protected AMBITAppConfigProperties properties;
	
	
	public static enum CONFIG {
		enabled {
			@Override
			public String getDescription() {
				return "AA enabled/disabled";
			}
		},
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
		public String getValue(AMBITAppConfigProperties properties) {
			try {
				if (properties == null) return null;
				Object o = properties.getPropertyWithDefault(name(), AMBITAppConfigProperties.configProperties,getDefaultValue());
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
	private OpenSSOServicesConfig() throws AAException {
		properties = new AMBITAppConfigProperties();
	}	

	public static synchronized OpenSSOServicesConfig getInstance() throws AAException  {
	    if (ref == null)
	        ref = new OpenSSOServicesConfig();		
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
	
	public  boolean isEnabled() throws AAException  {
	    String e =  getConfig(CONFIG.enabled);
	    return "true".equals(e.toLowerCase());
	}		
	
	public Object clone() throws CloneNotSupportedException {
	    throw new CloneNotSupportedException(); 
    }
	protected  synchronized String getConfig(CONFIG config) throws AAException  {
		return properties==null?null:config.getValue(properties);
	}	
}
