package ambit2.rest;

import org.restlet.data.MediaType;

/**
 * Chemical Media type
 * http://www.ch.ic.ac.uk/chemime/
 * @author nina
 *
 */

public class ChemicalMediaType  {
			  
	public static final MediaType CHEMICAL_MDLSDF = new MediaType("chemical/x-mdl-sdfile");
	public static final MediaType CHEMICAL_MDLMOL= new MediaType("chemical/x-mdl-molfile");	
	public static final MediaType CHEMICAL_CML = new MediaType("chemical/x-cml");
	public static final MediaType CHEMICAL_SMILES = new MediaType("chemical/x-daylight-smiles");
	public static final MediaType CHEMICAL_INCHI = new MediaType("chemical/x-inchi");
	public static final MediaType WEKA_ARFF = new MediaType("text/x-arff");
	public static final MediaType TEXT_YAML = new MediaType("text/x-yaml");
	public static final MediaType APPLICATION_YAML = new MediaType("application/x-yaml");
	public static final MediaType THREECOL_ARFF = new MediaType("text/x-arff-3col");
	public static final MediaType ATOM_AREA_MAP = new MediaType("text/map");
	
	protected ChemicalMediaType() {
		
	}
}
