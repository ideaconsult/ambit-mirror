package ambit2.search.chemidplus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.index.CASNumber;

public class ChemIdPlusRequest extends AbstractSearchRequest<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4023235628557946550L;
	//http://chem.sis.nlm.nih.gov/molfiles/0000050000.mol
	public static final String CHEMIDPLUS_URL = "http://chem.sis.nlm.nih.gov/molfiles/%s.mol";
	
	public String process(String target) throws AmbitException {
		try {
			return get(new URL(String.format(CHEMIDPLUS_URL, formatCAS(target))));
		} catch (MalformedURLException x) {
			throw new AmbitException(x);
		}
	}
	
	protected String formatCAS(String cas) throws AmbitException {
		if (CASNumber.isValid(cas)) {
			cas = cas.replace("-","");
			StringBuilder b = new StringBuilder();
			for (int i=0; i<10-cas.length();i++)
				b.append("0");
			b.append(cas);
			return b.toString();
		} else throw new AmbitException("Invalid CAS");
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
	
	@Override
	public String toString() {
		return "http://chem.sis.nlm.nih.gov";
	}

}
