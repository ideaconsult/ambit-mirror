package ambit2.search.opentox;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.index.CASNumber;

import ambit2.core.data.EINECS;
import ambit2.search.chemidplus.AbstractSearchRequest;

public class OpenToxRequest extends AbstractSearchRequest<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4186581307201678996L;
	protected String opentoxservice;
	public OpenToxRequest(String opentoxservice) {
		this.opentoxservice = opentoxservice;
	}

	@Override
	public String process(String target) throws AmbitException {
		try {
			if (CASNumber.isValid(target) || EINECS.isValid(target))  {
				return get(new URL(String.format("%s/query/compound/%s/reach?media=chemical%%2Fx-mdl-sdfile&max=1", 
										opentoxservice, URLEncoder.encode(target))));
			}	
			else return null;
		} catch (MalformedURLException x) {
			throw new AmbitException(x);
		}
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
