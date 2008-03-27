/**
 * Created on 2005-3-22
 *
 */
package ambit2.data.experiment;



import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.data.literature.LiteratureEntry;
import ambit2.data.literature.ReferenceFactory;
import ambit2.data.molecule.CompoundFactory;
import ambit2.exceptions.AmbitException;


/**
 * Provides static functions to create several {@link Experiment} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ExperimentFactory {

	/**
	 * 
	 */
	protected ExperimentFactory() {
		super();
	}
	public static void setSalmonellaTA98(Study study) throws AmbitException {
		study.setStudyCondition("Species","Salmonella typhimurium");
		study.setStudyCondition("Species.strain","TA98+S9");		
	}
	public static void setSalmonellaTA100(Study study) throws AmbitException {
		study.setStudyCondition("Species","Salmonella typhimurium");
		study.setStudyCondition("Species.strain","TA100+S9");	    
	}	
	public static Study createAMES(String studyName,StudyTemplate template) throws AmbitException {
	    Study study = new Study(studyName,new DefaultTemplate("AMES"));
	    study.setTemplate(template);
	    study.setStudyCondition(DefaultTemplate.endpoint,studyName);	    
	    study.setStudyCondition(DefaultTemplate.units,"log rev/nmol");
	    
	    return study;
	}	
	public static Study createLC50Fish14d() throws AmbitException {
	    Study study = new Study("Acute Toxicity",new DefaultTemplate("Acute Toxicity"));
	    study.setStudyCondition(DefaultTemplate.endpoint,"LC50");	    
	    study.setStudyCondition(DefaultTemplate.units,"mg/L (ppm)");
	    study.setStudyCondition(DefaultTemplate.duration,"14-day");
	    study.setStudyCondition(DefaultTemplate.species,"Fish");
		return study;
	}
	public static Study createLC50Fish96hr()  throws AmbitException {
	    Study study = createLC50Fish14d();
	    study.setStudyCondition(DefaultTemplate.duration,"96-hr");
		return study;
	}	
	public static Study createLC50Daphnid48hr()  throws AmbitException {
	    Study study = createLC50Fish14d();
	    study.setStudyCondition(DefaultTemplate.duration,"48-hr");
	    study.setStudyCondition(DefaultTemplate.species,"Daphnid");
		return study;	    
	}	
	public static Study createLC50Daphnid16dayhr()  throws AmbitException {
	    Study study = createLC50Fish14d();
	    study.setStudyCondition(DefaultTemplate.duration,"16-day");
	    study.setStudyCondition(DefaultTemplate.species,"Daphnid");
		return study;		    
	}		

	/**TA98 , TA100
	 * 91-59-8 2-Aminonaphthalene 2-AN  Nc1ccc2ccccc2(c1)  -1.43 0.29
	 * 153-78-6 2-AMINOFLUORENE   Nc2ccc3c1ccccc1Cc3(c2)  1.54  1.03
	 * 92-67-1 4-Aminobiphenyl Nc1ccc(cc1)c2ccccc2  0.65 0.92
	 */
	public static Experiment createExperiment(IMolecule mol,
	        		Object field,
					double result,
					Study study, LiteratureEntry reference) throws AmbitException {
		Experiment e = new Experiment(mol,study,reference);
		e.setResult(field,new Double(result));
		return e;
	}
	public static ExperimentList createGlendeExperiments() throws AmbitException {
	    StudyTemplate template = new DefaultTemplate("AMES");
		LiteratureEntry reference = ReferenceFactory.createGlendeReference();
		Study studyTA98 = createAMES("TA98",template);
		setSalmonellaTA98(studyTA98);
		Study studyTA100 = createAMES("TA100",template);
		setSalmonellaTA100(studyTA100);
		
		ExperimentList e = new ExperimentList();
		IMolecule mol = CompoundFactory.create2AminofluoreneMol();
		e.addItem(new Experiment(mol,studyTA98,reference,DefaultTemplate.result,new Double(1.54)));
		e.addItem(new Experiment(mol,studyTA100,reference,DefaultTemplate.result,new Double(1.03)));

		mol = CompoundFactory.create2AminonaphthaleneMol();
		e.addItem(new Experiment(mol,studyTA98,reference,DefaultTemplate.result,new Double(-1.43)));
		e.addItem(new Experiment(mol,studyTA100,reference,DefaultTemplate.result,new Double(0.29)));

		mol = CompoundFactory.create4AminobiphenylMol();
		e.addItem(new Experiment(mol,studyTA98,reference,DefaultTemplate.result,new Double(0.65)));
		e.addItem(new Experiment(mol,studyTA100,reference,DefaultTemplate.result,new Double(0.92)));		
		
		return e;
		
	}
	public static Experiment createBenzeneLC50() throws AmbitException {
		
		LiteratureEntry reference = ReferenceFactory.createEmptyReference();
		Study lc50Fish14 = createLC50Fish14d();
		
		IMolecule mol = MoleculeFactory.makeBenzene();
		Experiment e = new Experiment(mol,lc50Fish14,reference,DefaultTemplate.result,new Double(107.014));
		
		return e;
		
	}
	
	/**
	 *  
	Example  Escosar Data Entry and Results



	Ecosar Estimation Results for Benzene
	 This estimation for benzene produces the following results:
	SMILES : c1ccccc1
	CHEM   : Benzene
	CAS Num: 
	ChemID1: 
	ChemID2: 
	ChemID3: 
	MOL FOR: C6 H6 
	MOL WT : 78.11
	Log Kow: 1.99  (KowWin estimate)
	Melt Pt:
	Wat Sol: 355.6 mg/L  (calculated)

	ECOSAR v0.99e Class(es) Found
	------------------------------
	Neutral Organics

	                                                  Predicted
	ECOSAR Class         Organism     Duration End Pt mg/L (ppm)
	===================  ===========  ======== ====== ==========
	Neutral Organic SAR: Fish         14-day   LC50    107.014
	(Baseline Toxicity)

	Neutral Organics   : Fish         96-hr    LC50     59.174
	Neutral Organics   : Fish         14-day   LC50    107.014
	Neutral Organics   : Daphnid      48-hr    LC50     63.362
	Neutral Organics   : Green Algae  96-hr    EC50     39.590
	Neutral Organics   : Fish         30-day   ChV       7.611
	Neutral Organics   : Daphnid      16-day   EC50      3.235
	Neutral Organics   : Green Algae  96-hr    ChV       3.936
	Neutral Organics   : Fish  (SW)   96-hr    LC50     13.491
	Neutral Organics   : Mysid Shrimp 96-hr    LC50     17.188
	Neutral Organics   : Earthworm    14-day   LC50    483.963
	 

	 
	 *
	 */	
}
