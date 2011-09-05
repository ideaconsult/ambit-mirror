package ambit2.rest.comparison;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Test;
import org.opentox.aa.opensso.OpenSSOToken;
import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTDatasets;
import org.opentox.dsl.task.ClientResourceWrapper;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.rest.aa.opensso.OpenSSOServicesConfig;
import ambit2.rest.test.ProtectedResourceTest;

public class DatasetComparisonTest extends  ProtectedResourceTest  {
	
	@Test
	public void test() throws Exception {
		datasetsIntersectionHTML(
				"https://ambit.uni-plovdiv.bg:8443/ambit2/dataset?max=100",
				"https://ambit.uni-plovdiv.bg:8443/ambit2/dataset?max=100");
	}
	@Override
	protected boolean isAAEnabled() {
		return true;
	}
	@Override
	public void setUpАА() throws Exception {
		insecureConfig();
			ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
			if (ssoToken.login(
					"nina",
					"sinanica789"
					)) {
				ClientResourceWrapper.setTokenFactory(this);
			} else
				throw new Exception(String.format("Error logging to SSO (%s)",OpenSSOServicesConfig.getInstance().getTestUser()));
	}	
	protected void insecureConfig() throws Exception {

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{
		    new X509TrustManager() {
		        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		            return null;
		        }
		        public void checkClientTrusted(
		            java.security.cert.X509Certificate[] certs, String authType) {
		        }
		        public void checkServerTrusted(
		            java.security.cert.X509Certificate[] certs, String authType) {
		        }
		    }
		};

		// Install the all-trusting trust manager
		try {
		    SSLContext sc = SSLContext.getInstance("SSL");
		    sc.init(null, trustAllCerts, new java.security.SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		}
		HttpsURLConnection.setDefaultHostnameVerifier( 
				new HostnameVerifier(){
					public boolean verify(String string,SSLSession ssls) {
						return true;
					}
				});
	}
	/*
	public void datasetsIntersection(String uri1,String uri2) throws Exception {
		
		FileWriter w = new FileWriter("dataset_compare.txt");
		int count=0;
		try {
			OTDatasets datasets = new OTDatasets();
			datasets.read(uri1);
			ClientResourceWrapper.setTokenFactory(this);
			String intersection = "https://ambit.uni-plovdiv.bg:8443/ambit2/admin/stats/dataset_intersection?dataset_uri=%s&dataset_uri=%s";
			String chemnumber = "https://ambit.uni-plovdiv.bg:8443/ambit2/admin/stats/chemicals_in_dataset?dataset_uri=%s";

			w.write("Dataset");
			for (int i=0; i < datasets.size(); i++) {
				w.write(String.format(",%s",datasets.getItem(i).getUri()));
			}
			w.write("\n");
			for (int i=0; i < datasets.size(); i++) {
				OTDataset d1 = datasets.getItem(i);
				w.write(String.format("%s",d1.getUri()));
				for (int j=0; j < datasets.size(); j++) {
					String number = "";
					if (i==j) {
						String uri = String.format(chemnumber, URLEncoder.encode(d1.getUri().toString()));
						number = read(uri);
					} else {
						OTDataset d2 = datasets.getItem(j);	
					
						String uri = String.format(intersection, URLEncoder.encode(d1.getUri().toString()), URLEncoder.encode(d2.getUri().toString()));
						number = read(uri);
					}
					w.write(String.format(",%s",number));
				}
				w.write("\n");
				w.flush();
			}

		} catch (Exception x) {
			throw x;
		} finally {
			w.close();
		}
	}
	*/
    public void datasetsIntersectionHTML(String uri1,String uri2) throws Exception {
		
		FileWriter w = new FileWriter("dataset_compare.html");
		w.write("<html><head><title>Dataset comparison</title></head><body>");
		int count=0;
		try {
			OTDatasets datasets = new OTDatasets();
			datasets.read(uri1);
			ClientResourceWrapper.setTokenFactory(this);
			String intersection = "https://ambit.uni-plovdiv.bg:8443/ambit2/admin/stats/dataset_intersection?dataset_uri=%s&dataset_uri=%s";
			String chemnumber = "https://ambit.uni-plovdiv.bg:8443/ambit2/admin/stats/chemicals_in_dataset?dataset_uri=%s";

			ArrayList<String> d = new ArrayList<String>();
			for (int i=0; i < datasets.size(); i++) {
				String uri = datasets.getItem(i).getUri().toString();
				if (Integer.parseInt(uri.substring(uri.lastIndexOf("/")+1))<6520)
					d.add(uri);
			}
			Collections.sort(d,new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return
					Integer.parseInt(o1.substring(o1.lastIndexOf("/")+1))-
					Integer.parseInt(o2.substring(o2.lastIndexOf("/")+1));
				}
			});
			w.write("<table border='1'>");
			w.write("<caption>Datasets at https://ambit.uni-plovdiv.bg:8443/ambit2/</caption>");
			w.write("<tr>");
			w.write("<th>Dataset</th>");
			for (int i=0; i < d.size(); i++) {
				String uri = d.get(i);
				w.write(String.format("<th><a href='%s'>%s<a></th>",
						uri,
						uri.substring(uri.lastIndexOf("/"))
						)
						);
			}
			w.write("</tr>");
			for (int i=0; i < d.size(); i++) {
				
				w.write("<tr>");
				String uri = d.get(i);
				w.write(String.format("<th><a href='%s' title='%s' target='_blank'>%s</a></th>",
							uri,uri,uri.substring(uri.lastIndexOf("/"))));
				String clr = "#FFFFFF";
				uri = String.format(chemnumber, URLEncoder.encode(d.get(i)));
				int max = read(uri);
				for (int j=0; j < d.size(); j++) {
					int number = 0;
					if (i==j) {
						number = max;
						clr = "#FFFFFF";
					} else {
					
						uri = String.format(intersection, URLEncoder.encode(d.get(i)), 
								URLEncoder.encode(d.get(j)));
						number = read(uri);
						clr = getColor(max, number);
					}
					w.write(String.format("<td bgcolor='#%s' title='%s'><a href='%s'>%4.0f<a></td>",
							clr,
							number,
							uri,
							100.0f*(float)number/(float)max));
				}
				w.write("</tr>");
				w.flush();
			}
			w.write("</table>");
			w.write("</body></html>");
		} catch (Exception x) {
			throw x;
		} finally {
			w.close();
		}
	}
	private String getColor(int max, int number) {
	    Color color = Color.getHSBColor(((100f/255f)*(float)number/(float)max),213f/240f,240f/240f);
	    return String.format("%s%s%s",
	    		Integer.toHexString(color.getRed()),
	    				Integer.toHexString(color.getGreen()),
	    						Integer.toHexString(color.getBlue())    						
	    				);
	}
	public int readx(String uri) throws Exception {
		 return (int) (Math.random()*100);
	}	 
	 public int read(String uri) throws Exception {
		 
		 	StringBuffer b = new StringBuffer();
			HttpURLConnection uc= null;
			InputStream in= null;
			int code = 0;
			try {
				ClientResourceWrapper.setTokenFactory(this);
				uc = ClientResourceWrapper.getHttpURLConnection(uri, "GET", MediaType.TEXT_HTML.toString());
				
				code = uc.getResponseCode();
				if (HttpURLConnection.HTTP_OK == code) {
					in = uc.getInputStream();		
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line = null;
					while ((line = reader.readLine())!=null) {
						line = line.trim();
						if ("".equals(line)) continue;
						b.append(line);
					}		
				}
				return Integer.parseInt(b.toString());
			} catch (IOException x) {
				if (code > 0) throw new ResourceException(new Status(code),x);
				else throw x;
			} catch (Exception x) {
				throw x;
			} finally {
				try { if (in !=null) in.close();} catch (Exception x) {}
				try { if (uc != null) uc.disconnect();} catch (Exception x) {}
			}
			
		}
}
