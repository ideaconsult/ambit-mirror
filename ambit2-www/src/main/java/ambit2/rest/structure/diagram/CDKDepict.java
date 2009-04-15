package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.io.CompoundImageTools;

/**
 * 2D depiction based on CDK
 * @author nina
 *
 */
public class CDKDepict extends AbstractDepict {

	protected String smiles = null;
	protected CompoundImageTools depict = new CompoundImageTools();
	public CDKDepict(Context context, Request request, Response response) {
		super(context,request,response);
	}
	@Override
	protected BufferedImage getImage(String smiles) throws AmbitException {
		return depict.getImage(smiles);
	}

}
