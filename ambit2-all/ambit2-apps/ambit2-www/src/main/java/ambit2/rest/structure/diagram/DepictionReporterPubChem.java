package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;

import ambit2.pubchem.rest.PUGRestImageRequest;
import ambit2.pubchem.rest.PUGRestImageRequest.IMAGE_SIZE;
import ambit2.pubchem.rest.PUGRestImageRequest.RECORD_TYPE;
import ambit2.pubchem.rest.PUGRestRequest.COMPOUND_DOMAIN_INPUT;

public class DepictionReporterPubChem extends DepictionReporter {
	protected PUGRestImageRequest depict = new PUGRestImageRequest();
	/**
	 * 
	 */
	private static final long serialVersionUID = -1445084713042556519L;

	@Override
	public void processItem(DepictQuery q, BufferedImage output) {
		String smiles = (q.getSmiles() != null) && (q.getSmiles().length > 0) ? q
				.getSmiles()[0] : null;
		depict.setWidth(q.getW());
		depict.setHeight(q.getH());
		depict.setImageSize(IMAGE_SIZE.dimension);
		depict.setInput(COMPOUND_DOMAIN_INPUT.smiles);
		try {
			depict.setRecordType(RECORD_TYPE.D3.toString().equals(
					q.getRecordType()) ? RECORD_TYPE.D3 : RECORD_TYPE.D2);
		} catch (Exception x) {
		}
		if (smiles.startsWith("InChI"))
			depict.setInput(COMPOUND_DOMAIN_INPUT.inchi);
		else
			try { // CID
				Integer.parseInt(smiles);
				depict.setInput(COMPOUND_DOMAIN_INPUT.cid);
			} catch (Exception x) {
			}
		try {
			if (smiles != null)
				setOutput(depict.process(smiles));
		} catch (Exception x) {
			try {
				setOutput(createDefaultImage(q.getW(), q.getH()));
			} catch (Exception xx) {
			}
		}

	}

}
