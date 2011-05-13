package ambit2.base.interfaces;

import java.io.Serializable;

public interface IChemical extends Serializable {
	String getFormula();
	void setFormula(String formula);	
	String getSmiles();
	void setSmiles(String smiles);
	String getInchi();
	void setInchi(String inchi);
	String getInchiKey();
	void setInchiKey(String key);
    int getIdchemical();
    void setIdchemical(int idchemical);
	public boolean usePreferedStructure();
	public void setUsePreferedStructure(boolean value);
}
