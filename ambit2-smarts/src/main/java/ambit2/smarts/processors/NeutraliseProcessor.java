package ambit2.smarts.processors;

import java.io.InputStream;

/**
 * Chemical structure neutralisation via set of predefined SMIRKS {@link ambit2
 * /smirks/smirks.json}. Uses {@link SMIRKSProcessor}
 * 
 * @author nina
 * 
 */
public class NeutraliseProcessor extends SMIRKSProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8550598539258019431L;

	public NeutraliseProcessor() throws Exception {
		super();
		setLoadExamples(false);
		InputStream in = null;

		try {
			in = getClass().getClassLoader().getResourceAsStream(
					"ambit2/smirks/smirks.json");
			loadReactionsFromJSON(in);
		} catch (Exception x) {
			throw x;
		} finally {
			if (in!=null) in.close();
		}
	}
}
