package ambit2.base.interfaces;

import java.io.Serializable;

public interface IChemical extends Serializable {
	String getFormula();
	void setFormula(String formula);	
	String getSmiles();
	void setSmiles(String smiles);
	String getInchi();
	void setInchi(String inchi);
	long getHash();
	void setHash(long hash);
    int getIdchemical();
    void setIdchemical(int idchemical);
    
}
