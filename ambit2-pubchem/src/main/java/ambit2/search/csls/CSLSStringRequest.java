package ambit2.search.csls;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import ambit2.pubchem.NCISearchProcessor.METHODS;

public class CSLSStringRequest extends CSLSRequest<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6645197141747700467L;

	public CSLSStringRequest() {
		this(METHODS.sdf);
	}
	public CSLSStringRequest(METHODS method) {
		super();
		this.setRepresentation(method);
	}
	@Override
	protected String read(InputStream in) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringWriter w = new StringWriter();
		String line = null;
		while ((line = reader.readLine())!=null) {
			w.write(line);
			w.write("\n");
		}
		return w.toString();
	}
}
