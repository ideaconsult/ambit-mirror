package ambit2.rest;

import org.restlet.data.MediaType;

/**
 * http://www.ch.ic.ac.uk/chemime/
 * @author nina
 *
 */

public class ChemicalMediaType  {
			  
	public static final MediaType CHEMICAL_MDLSDF = new MediaType("chemical/x-mdl-sdfile");
	public static final MediaType CHEMICAL_MDLMOL= new MediaType("chemical/x-mdl-molfile");	
	public static final MediaType CHEMICAL_CML = new MediaType("chemical/x-cml");
	public static final MediaType CHEMICAL_SMILES = new MediaType("chemical/x-daylight-smiles");
	
	protected ChemicalMediaType() {
		
	}
}
