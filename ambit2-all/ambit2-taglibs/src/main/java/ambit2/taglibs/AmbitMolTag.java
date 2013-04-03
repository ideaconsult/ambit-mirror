package ambit2.taglibs;

import java.net.URLDecoder;

import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.data.MoleculeTools;
import ambit2.core.smiles.SmilesParserWrapper;

public abstract class AmbitMolTag extends SimpleTagSupport {
	public static final String type_mol = "mol";
	public static final String type_cml = "cml";
	public static final String type_smiles = "smiles";
	protected String  mol = null;
	protected String molType = "mol";
	
	public String getMol() {
		return mol;
	}

	public void setMol(String mol) {
		this.mol = mol;
	}

	public String getMolType() {
		return molType;
	}

	public void setMolType(String molType) {
		this.molType = molType;
	}
	
	protected IAtomContainer getMolecule(String mol) throws Exception {
		if (type_cml.equals(molType)) {
			return MoleculeTools.readCMLMolecule(mol);
		} else if (type_mol.equals(molType)) { 
			String s = URLDecoder.decode(mol,"UTF-8");
		    return  MoleculeTools.readMolfile(s);
		} else if (type_smiles.equals(molType)) {
			SmilesParserWrapper p = SmilesParserWrapper.getInstance();
			return p.parseSmiles(mol);
    
		} else throw new Exception("Invalid type "+molType);
	}	
}
