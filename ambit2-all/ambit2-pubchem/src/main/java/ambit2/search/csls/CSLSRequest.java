package ambit2.search.csls;

import java.net.MalformedURLException;
import java.net.URL;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.pubchem.NCISearchProcessor;
import ambit2.search.chemidplus.AbstractSearchRequest;

public abstract class CSLSRequest<R> extends AbstractSearchRequest<R> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4821305383980518514L;
	public static final String CSLS_URL = "http://cactus.nci.nih.gov/chemical/structure";
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
				String.format("%s/%s/%s%s", CSLS_URL, target,representation.toString(),getOptions())));
		} catch (MalformedURLException x) {
			throw new AmbitException(x);
		}
	}
		
	@Override
	public String toString() {
		return CSLS_URL;
	}
}
