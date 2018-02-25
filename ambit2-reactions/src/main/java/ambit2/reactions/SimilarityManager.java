package ambit2.reactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.INCHI_RET;

import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.retrosynth.StartingMaterialsDataBase;

public class SimilarityManager 
{
	public static enum SimilarityTransformation {
		RING_DISCONNECT, RING_STRIP
	}
	
	public static class TransformedStartMaterialData {
		public String smiles = null;
		public int frequency = 0;
	}
	
	public SimilarityManager() throws Exception
	{
		init();
	}
	
	SimilarityTransformation transformations[] = null;
	StartingMaterialsDataBase startingMaterialsDataBase = null; 
	Map<String,TransformedStartMaterialData> transformedMaterials = 
					new HashMap<String,TransformedStartMaterialData>();
	List<INCHI_OPTION> inchiOptions = null;
	InChIGeneratorFactory inchiGeneratorFactory = null;
	
	void init() throws Exception
	{
		inchiOptions = new ArrayList<INCHI_OPTION>();
		inchiOptions.add(INCHI_OPTION.FixedH);
		inchiOptions.add(INCHI_OPTION.SAbs);
		inchiOptions.add(INCHI_OPTION.SAsXYZ);
		inchiOptions.add(INCHI_OPTION.SPXYZ);
		inchiOptions.add(INCHI_OPTION.FixSp3Bug);
		inchiOptions.add(INCHI_OPTION.AuxNone);
		
		
		inchiGeneratorFactory = InChIGeneratorFactory.getInstance();
	}
	
	public SimilarityTransformation[] getTransformations() {
		return transformations;
	}

	public void setTransformations(SimilarityTransformation[] transformations) {
		this.transformations = transformations;
	}

	public StartingMaterialsDataBase getStartingMaterialsDataBase() {
		return startingMaterialsDataBase;
	}

	public void setStartingMaterialsDataBase(
			StartingMaterialsDataBase startingMaterialsDataBase) {
		this.startingMaterialsDataBase = startingMaterialsDataBase;
	}

	public Map<String, TransformedStartMaterialData> getTransformedMaterials() {
		return transformedMaterials;
	}

	public void setTransformedMaterials(
			Map<String, TransformedStartMaterialData> transformedMaterials) {
		this.transformedMaterials = transformedMaterials;
	}
	
	double calcSimilarity(IAtomContainer mol)
	{
		//TODO
		return 0.0;
	}
	
	public List<IAtomContainer> doTransformation(IAtomContainer mol)
	{
		//TODO 
		//handle information/mapping to the original
		//molecule atoms
		
		return null;
	}
	
	String getInchiKey(IAtomContainer mol) throws Exception
	{
		InChIGenerator ig = 
				inchiGeneratorFactory.getInChIGenerator(mol, inchiOptions);
		INCHI_RET returnCode = ig.getReturnStatus();
		if (INCHI_RET.ERROR == returnCode) {
			//Error
		}
		return ig.getInchiKey();
	}
	
}
