package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.HttpException;
import ambit2.search.csls.CSLSImageRequest;

public class DepictionReporterCSLS extends DepictionReporter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 370128685673084869L;
	protected CSLSImageRequest depict = new CSLSImageRequest();

	@Override
	public void processItem(DepictQuery q, BufferedImage output) {

		depict.setWidth(q.getW());
		depict.setHeight(q.getH());
		try {
			String smiles = (q.getSmiles() != null)
					&& (q.getSmiles().length > 0) ? q.getSmiles()[0] : null;
			setOutput(depict.process(smiles));

		} catch (Exception x) {
			try {
				setOutput(createDefaultImage(q.getW(), q.getH()));
			} catch (Exception xx) {
			}
		}
	}

}
