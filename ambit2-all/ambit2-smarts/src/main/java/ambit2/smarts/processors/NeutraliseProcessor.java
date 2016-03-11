package ambit2.smarts.processors;

import java.io.InputStream;
import java.util.logging.Logger;

import ambit2.smarts.SMIRKSManager;

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

	public NeutraliseProcessor(Logger logger) throws Exception {
		super(logger);
		smrkMan.setFlagConvertExplicitHToImplicitOnResultProcess(true);
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
	@Override
	public void configureReactions(SMIRKSManager smrkMan) throws Exception {
		smrkMan.setFlagConvertExplicitHToImplicitOnResultProcess(true);
		super.configureReactions(smrkMan);
	}
}
