package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Properties;

import ambit2.core.smiles.OpenBabelDepiction;

public class DepictionReporterOBabel extends DepictionReporter {
	protected static String obabel_home;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5856145841839120833L;

	@Override
	public void processItem(DepictQuery q, BufferedImage output) {
		String smiles = (q.getSmiles() != null) && (q.getSmiles().length > 0) ? q
				.getSmiles()[0] : null;
		try {
			OpenBabelDepiction ob = new OpenBabelDepiction() {
				/**
			     * 
			     */
				private static final long serialVersionUID = -7104086945548875848L;

				protected String getOBabelHome() {
					return getOpenBabelHome();
				};
			};
			ob.setSize(q.getH());
			ob.setHydrogens(false);
			ob.process(smiles);
			setOutput(ob.getImage());
		} catch (Exception x) {
			try {
				setOutput(createDefaultImage(q.getW(), q.getH()));
			} catch (Exception xx) {
			}
		}
	}

	public synchronized String getOpenBabelHome() {
		if (obabel_home == null)
			try {
				Properties properties = new Properties();
				InputStream in = this.getClass().getClassLoader()
						.getResourceAsStream("ambit2/rest/config/ambit2.pref");
				properties.load(in);
				in.close();
				obabel_home = properties
						.getProperty(OpenBabelDepiction.OBABEL_HOME);
			} catch (Exception x) {
				return null;
			}
		return obabel_home;
	}
}
