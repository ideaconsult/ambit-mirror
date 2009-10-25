package ambit2.pubchem;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;

public abstract class CSLSRequest<R> extends DefaultAmbitProcessor<String, R> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4821305383980518514L;

	protected NCISearchProcessor.METHODS representation;
	public NCISearchProcessor.METHODS getRepresentation() {
		return representation;
	}
	public void setRepresentation(NCISearchProcessor.METHODS representation) {
		this.representation = representation;
	}
	protected String getOptions() { return "";}
	public R process(String target) throws AmbitException {
		try {
			return get(new URL(
				String.format("http://cactus.nci.nih.gov/chemical/structure/%s/%s%s", target,representation.toString(),getOptions())));
		} catch (MalformedURLException x) {
			throw new AmbitException(x);
		}
	}
	protected abstract R read(InputStream in) throws Exception;
	public R get(URL url) throws AmbitException {

		InputStream in = null;
		HttpURLConnection uc = null;
		try {
			uc =(HttpURLConnection) url.openConnection();
			uc.setDoOutput(true);
			uc.setRequestMethod("GET");
			in= uc.getInputStream();
			return read(uc.getInputStream());
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			try {
				if (in != null) in.close();
			} catch (Exception x) { 
			}
		}
	}
	
}
