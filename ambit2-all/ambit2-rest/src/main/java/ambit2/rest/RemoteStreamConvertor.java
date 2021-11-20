package ambit2.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
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

public class RemoteStreamConvertor extends DefaultAmbitProcessor<ByteArrayOutputStream, Representation> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4450496053693119759L;
	private WrappedService<UsernamePasswordCredentials> query;
	private MediaType media = MediaType.APPLICATION_JSON;
	private MediaType entityType;
	protected ByteArrayOutputStream entity = null;
	protected Method method = Method.GET;

	public RemoteStreamConvertor(WrappedService<UsernamePasswordCredentials> query, ByteArrayOutputStream entity,
			MediaType entityType, Method method, MediaType media) {
		super();
		this.query = query;
		this.media = media;
		this.method = method;
		this.entity = entity;
		this.entityType = entityType;

		if (MediaType.TEXT_XML.equals(media))
			setFileExtension("xml");
		else if (MediaType.TEXT_TSV.equals(media))
			setFileExtension("txt");
		else
			setFileExtension(null);
		setFileNamePrefix(String.format("%s_%s", query.getName(),UUID.randomUUID()));
	}

	protected void processStream(InputStream in, OutputStream stream) throws IOException {
		DownloadTool.download(in, stream);
	}

	@Override 
	public Representation process(final ByteArrayOutputStream form) throws Exception {
		// we don't want to proxy everything ;)

		OutputRepresentation rep = new OutputRepresentation(media) {
			@Override
			public void write(OutputStream stream) throws IOException {


				CloseableHttpClient httpclient = HttpClientBuilder.create().build();

				HttpRequestBase httprequest;
				// try {
				if (Method.GET.equals(method)) {
					try {
						URIBuilder ub = new URIBuilder(query.getService());
						httprequest = new HttpGet(ub.build());
					} catch (URISyntaxException x) {
						throw new IOException(x);
					}
				} else {
					httprequest = new HttpPost(query.getService());
					httprequest.setHeader("Content-Type", String.format("%s; charset=utf-8",entityType.getName()));
					((HttpPost) httprequest).setEntity(new ByteArrayEntity(entity.toByteArray()));
				}

				httprequest.setHeader("Accept", media.getName());
				
				String auth = String.format("%s:%s",query.getCredentials().getUserName(),query.getCredentials().getPassword());
				byte[] encodedAuth = Base64.encodeBase64(
				  auth.getBytes(StandardCharsets.ISO_8859_1));
				String authHeader = "Basic " + new String(encodedAuth);
				httprequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);		
				
				try (CloseableHttpResponse response1 = httpclient.execute(httprequest)) {
					if (200 == response1.getStatusLine().getStatusCode())
						processStream(response1.getEntity().getContent(), stream);
					else
						throw new ResourceException(response1.getStatusLine().getStatusCode());
				} catch (ResourceException x) {
					throw x;
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
		setDisposition(rep);
		return rep;
	}

	protected void setDisposition(Representation rep) {
		if (getFileExtension() != null) {
			rep.setDownloadable(true);
			rep.setDownloadName(
					String.format("%s.%s", fileNamePrefix == null ? "download" : fileNamePrefix, getFileExtension()));
		}
	}

	protected String fileNamePrefix = null;
	protected String fileExtension = null;

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getFileNamePrefix() {
		return fileNamePrefix;
	}

	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}
}
