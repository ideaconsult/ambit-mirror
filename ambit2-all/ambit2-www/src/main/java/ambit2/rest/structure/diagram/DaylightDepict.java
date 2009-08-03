package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.base.exceptions.AmbitException;
import ambit2.pubchem.DepictRequest;

/**
 * 2D depiction based on daylight depict service
 * @author nina
 *
 */
public class DaylightDepict extends AbstractDepict {
	protected DepictRequest depict = new DepictRequest();
	
	public DaylightDepict(Context context, Request request, Response response) {
		super(context,request,response);

	}
	@Override
	protected BufferedImage getImage(String smiles,int w, int h) throws AmbitException {
		return depict.process(smiles);
	}
	protected String getTitle(Reference ref, String smiles) {
		return String.format("SMILES: %s<br><img src='%s' alt='%s' title='%s'>", smiles,ref,smiles,smiles);
	}
}
