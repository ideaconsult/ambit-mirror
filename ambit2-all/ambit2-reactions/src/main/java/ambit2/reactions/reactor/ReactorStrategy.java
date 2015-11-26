package ambit2.reactions.reactor;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.jniinchi.INCHI_OPTION;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.StructureRecord;
import ambit2.rules.json.JSONParsingUtils;
import ambit2.rules.json.JSONParsingUtils.STRUCTURE_RECORD_INPUT_INFO;
import ambit2.smarts.SmartsHelper;

public class ReactorStrategy 
{
	//Strategy flow flags
	public int maxNumOfReactions = -1;
	public int maxLevel = -1;
	public boolean FlagStopOnMaxLevel = false;
	public int maxNumOfNodes = 10000;
	public int maxNumOfFailedNodes = -1;
	public int maxNumOfSuccessNodes = -1;
	protected boolean FlagCalcProductInchiKey = true;  //Typically this flag should be true
	public boolean FlagCheckReactionConditions = true;
	
	//Tracing flags
	public boolean FlagStoreSuccessNodes = false;
	public boolean FlagStoreFailedNodes = false;
	public boolean FlagTraceParentNodes = false;
	public boolean FlagStoreProducts = true;
	public boolean FlagTraceReactionPath = false;
	public boolean FlagCheckNodeDuplicationOnPush = true;
	
	//Reactor Node flags	
	public List<StructureRecord> allowedProducts = null;
	public List<StructureRecord> forbiddenProducts = null;
	public boolean FlagSuccessNodeOnReachingAllowedProducts = true;
	public boolean FlagSuccessNodeOnZeroForbiddenProducts = false;
	public boolean FlagFailedNodeOnOneForbiddenProduct = true;
	public boolean FlagReactOneReagentOnly = true;
	public boolean FlagProcessRemainingStackNodes = false;	
	public boolean FlagProcessSingleReagentInNode = true; 
	
	//if true this means that allowed product will not continue reacting
	public boolean FlagRemoveReagentIfAllowedProduct = true;
	
	public boolean FlagRemoveReagentIfForbiddenProduct = false;
	
	//Logging
	public boolean FlagLogMainReactionFlow = false;
	public boolean FlagLogReactionPath = false;
	public boolean FlagLogNameInReactionPath = false;
	public boolean FlagLogExplicitHToImplicit = false;
	public boolean FlagLogNumberOfProcessedNodes = false;
	public int NodeLogingFrequency = 1000;
	
	public ReactorStrategy(){
	}
	
	public ReactorStrategy(File jsonFile) throws Exception
	{	
		readReactorStrategyFromJSONFile(jsonFile, this);
	}
	
	
	public static ReactorStrategy readReactorStrategyFromJSON(File jsonFile) throws Exception
	{
		return readReactorStrategyFromJSONFile(jsonFile, null);
	}
	
	public static ReactorStrategy readReactorStrategyFromJSONFile(File jsonFile, ReactorStrategy reactorStrategy) throws Exception
	{
		FileInputStream fin = new FileInputStream(jsonFile); 
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;
		
		try {
			root = mapper.readTree(fin);
		} catch (Exception x) {
			throw x;
		} finally {
			try {fin.close();} catch (Exception x) {}	
		}
		
		JsonNode strategyNode = root.path("REACTOR_STRATEGY");
		if (strategyNode.isMissingNode())
			throw new Exception ("REACTOR_STRATEGY section is missing!");
		
		return readReactorStrategyFromJsonNode(strategyNode, reactorStrategy);
	}
	
	
	public static ReactorStrategy readReactorStrategyFromJsonNode(JsonNode node) throws Exception
	{
		return readReactorStrategyFromJsonNode(node, null);
	}
	
	
	public static ReactorStrategy readReactorStrategyFromJsonNode(JsonNode node, ReactorStrategy reactorStrategy) throws Exception
	{
		ReactorStrategy strategy = reactorStrategy;
		if (strategy == null)		
			strategy = new ReactorStrategy();
		
		strategy.maxNumOfReactions = JSONParsingUtils.extractIntKeyword(node, "MAX_NUM_OF_REACTIONS", true);
		strategy.maxLevel = JSONParsingUtils.extractIntKeyword(node, "MAX_LEVEL", true);
		
		strategy.allowedProducts =  JSONParsingUtils.getStructureRecords(node, 
							"ALLOWED_PRODUCTS_SMILES", STRUCTURE_RECORD_INPUT_INFO.smiles, false);
		
		strategy.forbiddenProducts =  JSONParsingUtils.getStructureRecords(node, 
				"FORBIDDEN_PRODUCTS_SMILES", STRUCTURE_RECORD_INPUT_INFO.smiles, false);
		
		
		List<INCHI_OPTION> options = new ArrayList<INCHI_OPTION>();
		options.add(INCHI_OPTION.FixedH);
		options.add(INCHI_OPTION.SAbs);
		options.add(INCHI_OPTION.SAsXYZ);
		options.add(INCHI_OPTION.SPXYZ);
		options.add(INCHI_OPTION.FixSp3Bug);
		InChIGeneratorFactory igf = InChIGeneratorFactory.getInstance();
		
		//Configure products
		if (strategy.allowedProducts != null)
			for (StructureRecord sr : strategy.allowedProducts)
				configureStructureRecord (sr, igf, options);
		
		if (strategy.forbiddenProducts != null)
			for (StructureRecord sr : strategy.forbiddenProducts)
				configureStructureRecord (sr, igf, options);
		
		return strategy;
	}
	
	public static void configureStructureRecord(StructureRecord strRecord, 
												InChIGeneratorFactory igf, 
												List<INCHI_OPTION> options) throws Exception
	{
		if (strRecord.getSmiles() == null)
			return;
		
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(strRecord.getSmiles());
		InChIGenerator ig = igf.getInChIGenerator(mol, options);
		//String inchi = ig.getInchi();
		strRecord.setInchiKey(ig.getInchiKey());
	}
	
	
	
	public String toJSONString(String offset)
	{
		StringBuffer sb = new StringBuffer();
		int nFields = 0;
		sb.append(offset + "\"REACTOR_STRATEGY\":\n");		
		sb.append(offset + "{\n");
		
		sb.append(offset + "\t\"MAX_NUM_OF_REACTIONS\" : " + JSONParsingUtils.objectToJsonField(maxNumOfReactions));
		nFields++;
		
		if (nFields > 0)
			sb.append(",\n");
		sb.append(offset + "\t\"MAX_LEVEL\" : " + JSONParsingUtils.objectToJsonField(maxLevel));
		nFields++;
		
		if (nFields > 0)
			sb.append("\n");
		sb.append(offset + "}\n");
		return sb.toString();
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		if (allowedProducts!= null)
		{	
			sb.append("allowedProducts:\n");
			for (int i = 0; i < allowedProducts.size(); i++)
			{	
				StructureRecord sr = allowedProducts.get(i);
				sb.append("  #" + (i+1) + "  " + sr.getSmiles() + "  inchiKey=" + sr.getInchiKey() + "\n");
			}	
		}
		
		if (forbiddenProducts!= null)
		{	
			sb.append("forbiddenProducts:\n");
			for (int i = 0; i < forbiddenProducts.size(); i++)
			{	
				StructureRecord sr = forbiddenProducts.get(i);
				sb.append("  #" + (i+1) + "  " + sr.getSmiles() + "  inchiKey=" + sr.getInchiKey() + "\n");
			}	
		}	
		
		return sb.toString();
	}
	
	
}
