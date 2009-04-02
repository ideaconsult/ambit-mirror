package ambit2.base.interfaces;

public interface IChemical {
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
