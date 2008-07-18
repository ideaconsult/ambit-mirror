package ambit.taglibs;

import java.net.URLDecoder;

import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.openscience.cdk.interfaces.IMolecule;

import ambit.data.molecule.Compound;
import ambit.data.molecule.MoleculeTools;
import ambit.data.molecule.SmilesParserWithTimeout;

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
	protected IMolecule getMolecule(String mol) throws Exception {
		return getMolecule(mol,molType);
	}	
	
	protected IMolecule getMolecule(String mol, String type) throws Exception {
		if (type_cml.equals(type)) {
			return Compound.readMolecule(mol);
		} else if (type_mol.equals(type)) { 
			String s = URLDecoder.decode(mol,"UTF-8");
		    return  MoleculeTools.readMolfile(s);
		} else if (type_smiles.equals(type)) {
			SmilesParserWithTimeout p = new SmilesParserWithTimeout();
			return p.parseSmiles(mol, 30000);
    
		} else throw new Exception("Invalid type "+type);
	}	
}
