package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.base.io.DownloadTool;
import ambit2.rest.wrapper.WrappedService;
import net.idea.modbcum.p.DefaultAmbitProcessor;

public class RemoteStreamConvertor extends DefaultAmbitProcessor<List<NameValuePair>, Representation> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4450496053693119759L;
	private WrappedService<UsernamePasswordCredentials> query;
	private MediaType media = MediaType.APPLICATION_JSON;
	protected List<NameValuePair> form = null;
	protected Method method = Method.GET;

	public RemoteStreamConvertor(WrappedService<UsernamePasswordCredentials> query, List<NameValuePair> form,
			Method method, MediaType media) {
		super();
		this.query = query;
		this.media = media;
		this.method = method;
		this.form = form;
	}

	@Override
	public Representation process(final List<NameValuePair> form) throws Exception {
		// we don't want to proxy everything ;)

		OutputRepresentation rep = new OutputRepresentation(media) {
			@Override
			public void write(OutputStream stream) throws IOException {

				final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				credentialsProvider.setCredentials(AuthScope.ANY, query.getCredentials());
				CloseableHttpClient httpclient = HttpClientBuilder.create()
						.setDefaultCredentialsProvider(credentialsProvider).build();

				HttpRequestBase httprequest;
				// try {
				if (Method.GET.equals(method)) {
					try {
						URIBuilder ub = new URIBuilder(query.getService()).addParameters(form);
						httprequest = new HttpGet(ub.build());
					} catch (URISyntaxException x) {
						throw new IOException(x);
					}

				} else if (Method.POST.equals(method)) {
					httprequest = new HttpPost(query.getService());
					((HttpPost) httprequest).setEntity(new UrlEncodedFormEntity(form));
				} else
					throw new IOException("Unsupported method");

				try (CloseableHttpResponse response1 = httpclient.execute(httprequest)) {
					if (200 == response1.getStatusLine().getStatusCode())
						DownloadTool.download(response1.getEntity().getContent(), stream);
					else
						throw new ResourceException(response1.getStatusLine().getStatusCode());

				} catch (Exception x) {
					Throwable ex = x;
					while (ex != null) {
						if (ex instanceof IOException)
							throw (IOException) ex;
						ex = ex.getCause();
					}
					Context.getCurrentLogger().warning(x.getMessage() == null ? x.toString() : x.getMessage());

				} finally {

					try {
						if (stream != null)
							stream.flush();
					} catch (Exception x) {
					}

				}
			}
		};
		// setDisposition(rep);
		return rep;
	}

}
