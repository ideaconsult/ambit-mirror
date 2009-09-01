package org.ideaconsult.iuclidws;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.session.SessionEngine;

/**
 * <code>PropertiesUtil</code> is helper class for 
 *  properties values located in iuclid5client.properties file.
 */
public class PropertiesUtil {

	private static Properties properties;
	private static Logger logger = Logger.getLogger(PropertiesUtil.class);

	/** properties file name **/
	private final static String PROPERTIES_FILE = "iuclid5client.properties";
	
	/** property key **/
	private final static String USERNAME_KEY = "user";
	/** property key **/
	private final static String PASSWORD_KEY = "pass";
	/** property key **/
	private final static String TARGET_KEY = "iuclid5_services_url";

	/**
	 * Initialize properties
	 */
	static {
		properties = new Properties();
		properties.setProperty(USERNAME_KEY, "webservice");
		properties.setProperty(PASSWORD_KEY, "webservice");
		properties.setProperty(TARGET_KEY, "http://ideaconsult.dyndns.org:8080/i5wsruntime/services");
		try {
			logger.debug("Loading properties from configuration file");
			InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			
			if (inputStream == null) {
				logger.info("Configuration file iuclid5client.properties not found.");
				StringBuilder sb = new StringBuilder();
				sb.append("\nUsing default properties:\n");
				Set<Object> prpetiesKeys = properties.keySet();
				for (Object key : prpetiesKeys) {
					sb.append("          ");
					sb.append(key);
					sb.append("=");
					sb.append(properties.getProperty(key.toString()));
					sb.append("\n");
				}
				logger.info(sb);
			} else {
				properties.load(inputStream);
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * 
	 * @return the username for iuclid5 {@link SessionEngine} service
	 */
	public static String getUsername() {
		return properties.getProperty(USERNAME_KEY);
	}

	/**
	 * 
	 * @return the password for iuclid5 {@link SessionEngine} service
	 */
	public static String getPassword() {
		return properties.getProperty(PASSWORD_KEY);
	}

	/**
	 * 
	 * @return the target url for iuclid5 services 
	 */
	public static String getTarget() {
		return properties.getProperty(TARGET_KEY);
	}

}
