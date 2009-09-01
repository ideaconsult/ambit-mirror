package org.ideaconsult.iuclidws;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

/**
 * Util class that contains some useful methods 
 * needed for initialization of stub classes
 */
public class StubUtil {

	/**
	 * Concatenate base target web services url with specified end point service name.
	 * <br>Example:<br> baseURL=<b>http://example.com/axis2/services</b>, endPointName=<b>SessionEngine</b>,
	 * result=<b>http://example.com/axis2/services/SessionEngine</b>
	 * 
	 * @param baseURL url address for web services
	 * @param endPointName name of axis web service
	 * 
	 * @return the concatenated address
	 */
	public static String concatEndpointAddress(String baseURL, String endPointName) {
		if (baseURL.endsWith("/"))
			return (new StringBuilder()).append(baseURL).append(endPointName).toString();
		else
			return (new StringBuilder()).append(baseURL).append("/").append(endPointName).toString();
	}
	
	/**
	 * Initialize some axis client options for given {@link ServiceClient}
	 * 
	 * @param axisClient
	 * @throws AxisFault
	 */
	public static void initializeAxisService(ServiceClient axisClient) throws AxisFault {
		Options options = axisClient.getOptions();
		options.setManageSession(true);
		options.setTimeOutInMilliSeconds(600000);
		axisClient.setOptions(options);
	}
}
