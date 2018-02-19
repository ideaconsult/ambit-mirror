package ambit2.reactions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.reactions.GenericReaction.ReactionConfigStatus;
import ambit2.reactions.io.ReactionWriteUtils;
import ambit2.smarts.SMIRKSManager;


public class ReactionDataBase 
{
	/*
	public static class RDBFileConfig {
		public int flagUseCol = -1;
		public int idCol = -1;
		public int externIdCol = -1;
		public int nameCol = -1;
		public int smirksCol = -1;
		public int smirksFlagsCol = -1;
		public int smilesCol = -1;
		public int classCol = -1;
		public int reactionCenterCol = -1;
		public int reactionTypeCol = -1;
		public int transformTypeCol = -1;
		public int basicScoreCol = -1;
		public int experimentalConditionsScoreCol = -1;
		public int reliabilityScoreCol = -1;
		public int yieldIntervalidCol = -1;
		public int conditionsCol = -1;
		public int experimentalConditionsInfoCol = -1;
		public int infoCol = -1;
	}
	*/
	
	public static final String DEFAULT_TXT_FILE_COLUMN_NAMES [] = 
	{
		"ExternId",
		"Name",
		"SMIRKS",
		"SMIRKSflags",
		"SMILES",
		"Class",
		"ReactionCenter",
		"ReactionType",
		"TransformType",
		"BasicScore",
		"ExperimentalConditions",
		"ReliabilityScore",
		"YieldInterval",
		"Conditions",
		"ExperimentalConditionsInfo",
		"Info",
		"Priority",
		"FunctionalGroup"
	};
	
	private static Logger logger = Logger.getLogger(ReactionDataBase.class.getName());
	
	//public List<Reaction> reactions = null;
	public List<GenericReaction> genericReactions = null;
	public List<GenericReaction> excludedGenericReactions = null;
	public List<String> errors = new ArrayList<String>();
	
	public ReactionDataBase()
	{	
	}
	
	public ReactionDataBase(String fileName) throws Exception
	{	
		String extension = ReactionWriteUtils.getFileExtension(fileName);
		if (extension.equalsIgnoreCase("json"))
			loadReactionsFromJSON(new File(fileName), true);
		else if (extension.equalsIgnoreCase("txt"))
			loadReactionsFromTabDelimitedFile(new File(fileName), true);
		else
		{
			//Unsupported file format
		}
	}
	
	public ReactionDataBase(String fileName, Map<String,Integer> columnIndices) throws Exception
	{
		loadReactionsFromTabDelimitedFile(new File(fileName), true, columnIndices, 1);
	}
	
	public ReactionDataBase(String fileName, Map<String,Integer> columnIndices, int headerLines) throws Exception
	{
		loadReactionsFromTabDelimitedFile(new File(fileName), true, columnIndices, headerLines);
	}
	
	public ReactionDataBase(List<String> smirksList)
	{	
		if (smirksList == null)
			return;
		genericReactions = new ArrayList<GenericReaction>(); 
		
		for (int i = 0; i < smirksList.size(); i++)
		{
			String smirks = smirksList.get(i);
			GenericReaction gr = new GenericReaction();
			gr.setId(i+1);
			gr.setName("Reaction " + (i+1));
			gr.setFlagUse(true);
			gr.setSmirks(smirks);
			gr.setReactionClass("default");
			genericReactions.add(gr);
		}
	}
	
	public void loadReactionsFromJSON(File jsonFile, boolean FlagCleanDB) throws Exception
	{
		InputStream fin = new FileInputStream(jsonFile); 
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;
		
		try {
			
			root = mapper.readTree(fin);
		} catch (Exception x) {
			throw x;
		} finally {
			try {fin.close();} catch (Exception x) {}	
		}
		
		if (FlagCleanDB)
		{	
			//reactions = new ArrayList<Reaction>();
			genericReactions = new ArrayList<GenericReaction>(); 
		}	
		else
		{	
			//if (reactions == null)
			//	reactions = new ArrayList<Reaction>();
			if (genericReactions == null)
				genericReactions = new ArrayList<GenericReaction>(); 
		}	
		
		JsonNode reactionsNode = root.path("REACTIONS");
		if (reactionsNode.isMissingNode())
			throw new Exception ("REACTIONS section is missing!");
		
		if (!reactionsNode.isArray())
			throw new Exception ("REACTIONS section is not array!");
		
		for (int i = 0; i < reactionsNode.size(); i++)
		{
			try{
				
				/*
				Reaction reaction = Reaction.getReactionFromJsonNode(reactionsNode.get(i));
				reaction.setId(i+1);
				if (reaction.isFlagUse())
					reactions.add(reaction);
				*/
				
				GenericReaction genReact = GenericReaction.getReactionFromJsonNode(reactionsNode.get(i));
				genReact.setId(i+1);
				if (genReact.isFlagUse())
					genericReactions.add(genReact);
			}
			catch(Exception e)
			{
				logger.info("Error on reading REACTIONS element #"+ (i+1) + ": " + e.getMessage());
			}
		}
		
		fin.close();
	}
	
	public void loadReactionsFromTabDelimitedFile(File txtFile, boolean FlagCleanDB) throws Exception
	{	
		loadReactionsFromTabDelimitedFile(txtFile, FlagCleanDB, null, 1);
	}
	
	public void loadReactionsFromTabDelimitedFile(File txtFile, boolean FlagCleanDB, 
			Map<String,Integer> columnIndices, int numHeaderLines) throws Exception
	{
		int maxNeededColumnIndex;
		Map<String,Integer> indices = columnIndices;
		if (indices == null)
		{
			//temporary code
			indices  = getDefaultGenericReacitonTextFileColumnsIndices();
			maxNeededColumnIndex = indices.size();  //checkColumnIndices(indices) is not needed
		}
		else
		{
			maxNeededColumnIndex = checkColumnIndices(indices);
			if (maxNeededColumnIndex == -1)
			{	
				errors.add("Incorrect column indices for reading reactions form text file!");
				return; 
			}	
		}
		
		if (FlagCleanDB)
			genericReactions = new ArrayList<GenericReaction>();
		else
		{	
			if (genericReactions == null)
				genericReactions = new ArrayList<GenericReaction>(); 
		}	
		
		RandomAccessFile reader = ReactionWriteUtils.createReader(txtFile);
		//Iterate lines
		String splitter = "\t";			
		long length = reader.length();
		int lineNum = 0;
		try
		{	
			//Header lines are skipped
			List<String> headerLines = new ArrayList<String>();
			while ((lineNum < numHeaderLines) && (reader.getFilePointer() < length))
			{
				String line = reader.readLine();
				headerLines.add(line);
				lineNum++;
			}
			
			//Handle indices from header line
			if (indices == null)
				if (!headerLines.isEmpty())
				{	
					System.out.println("***** + Handle indices from header line");
					indices  = recognizeColumnIndices(headerLines.get(0));
					maxNeededColumnIndex = checkColumnIndices(indices);
					if (maxNeededColumnIndex == -1)
						throw new Exception("Incorrect column indices for reading reactions form text file!");
					
					System.out.println("Missing Columns: " + getMissingColumns(indices));
				}
			
			while (reader.getFilePointer() < length)
			{	
				String line = reader.readLine();
				lineNum++;
				line = line.trim();
				if (line.equals(""))
					continue; //empty line is skipped
				if (line.startsWith("#"))
					continue; //comment line is omitted (line starting with '#')
				
				String tokens[] = line.split(splitter);
				
				//Clean up tokens
				for (int i = 0; i < tokens.length; i++)
					tokens[i] = cleanUpToken (tokens[i]);
				
				/*
				if (tokens.length <= maxNeededColumnIndex)
				{
					errors.add("Insufficient column for reading reaction at line " + lineNum);
					continue;
				}
				*/
				
				try	{
					GenericReaction r = GenericReaction.getReactionFromTokens(tokens, indices);
					genericReactions.add(r);
				}
				catch (Exception x)	{
					errors.add("Error on reading reaction at line " + lineNum + "  :" + x.getMessage());
				}
				
			}
		}
		catch (Exception e) {
			errors.add("Error while iterating file lines: " + e.getMessage());
		}

		ReactionWriteUtils.closeReader(reader);
	}
	
	Map<String,Integer> recognizeColumnIndices(String line)
	{
		String tokens[] = line.split("\t");
		Map<String,Integer> columnIndices = new HashMap<String,Integer>();
		for (int i = 0; i < tokens.length; i++)
		{
			for (int k = 0; k < DEFAULT_TXT_FILE_COLUMN_NAMES.length; k++)
				if (DEFAULT_TXT_FILE_COLUMN_NAMES[k].equalsIgnoreCase(tokens[i]))
				{
					columnIndices.put(DEFAULT_TXT_FILE_COLUMN_NAMES[k], i);
					break;
				}
		}	
		return columnIndices;
	}
	
	String getMissingColumns(Map<String,Integer> columnIndices)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < DEFAULT_TXT_FILE_COLUMN_NAMES.length; i++)
		{	
			if (columnIndices.get(DEFAULT_TXT_FILE_COLUMN_NAMES[i]) == null)
			{	
				sb.append(DEFAULT_TXT_FILE_COLUMN_NAMES[i]);
				sb.append(" ");
			}
		}	
		
		return sb.toString();
	}
	
	String cleanUpToken(String token)
	{
		String tok1 = token.trim();
		if (tok1.length() <2)
			return tok1;
		
		if (token.charAt(0) == '"')
			if (token.charAt(token.length()-1) == '"')
				return token.substring(1, token.length()-1).trim();
		return tok1;
	}
	
	/**
	 * 
	 * @param columnIndices
	 * @return the maximal needed column index or 
	 * negative value if error is present
	 */
	public int checkColumnIndices(Map<String,Integer> columnIndices)
	{
		int maxIndex = -1;
		Set<String> keys = columnIndices.keySet();
		for (String key: keys)
		{
			Integer ind = columnIndices.get(key);
			if (ind != null)
				if (maxIndex < ind)
					maxIndex = ind;
		}
		//Check critically needed columns:
		if (columnIndices.get("Name") == null)
			return -1;
		if (columnIndices.get("SMIRKS") == null)
			return -2;
		return maxIndex;
	}
	
	public static Map<String,Integer> getDefaultGenericReacitonTextFileColumnsIndices()
	{
		Map<String,Integer> indices = new HashMap<String,Integer>();
		for (int i = 0; i < DEFAULT_TXT_FILE_COLUMN_NAMES.length; i++)
			indices.put(DEFAULT_TXT_FILE_COLUMN_NAMES[i], i);
		return indices;
	}
	
	
			
	/*
	public void configureReactions(SMIRKSManager smrkMan) throws Exception
	{
		if (reactions == null)
			return;
		
		for (Reaction reaction : reactions)
			reaction.configure(smrkMan);
	}
	*/
	
	
	public void configureGenericReactions(SMIRKSManager smrkMan) throws Exception
	{
		errors.clear();
		if (genericReactions == null)
			return;
		for (GenericReaction reaction : genericReactions)
		{	
			try {
				reaction.configure(smrkMan);
			} catch (Exception e) {
				errors.add("Config errors for reaction exterId=" + reaction.externId + "  " + e.getMessage());
			}
		}	
	}
	
	
	/*
	public Reaction getReactionByID(int id)
	{
		if (reactions != null)
			for (Reaction r : reactions)
			{	
				if (r.getId() == id)
					return r;
			}	
		
		return null;
	}
	*/
	
	
	public GenericReaction getGenericReactionByID(int id)
	{
		if (genericReactions != null)
			for (GenericReaction r : genericReactions)
			{	
				if (r.getId() == id)
					return r;
			}	
		
		return null;
	}
	
	public String getErrorsAsString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < errors.size(); i++)
			sb.append(errors.get(i) + "\n");
		return sb.toString();
	}
	
	public void excludeReactionsWithConfigErrors()
	{	
		if (genericReactions != null)
		{	
			List<GenericReaction> newGRList = new ArrayList<GenericReaction>();
			List<GenericReaction> excludedGenericReactions = new ArrayList<GenericReaction>();
			for (int i = 0; i < genericReactions.size(); i++)
			{	
				GenericReaction r = genericReactions.get(i);
				if (r.getConfigStatus() == ReactionConfigStatus.ERROR)
					excludedGenericReactions.add(r);
				else
					newGRList.add(r);
			}
			genericReactions = newGRList;
		}	
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		if (genericReactions != null)
			for (GenericReaction r : genericReactions)
			{	
				sb.append(r.toString()+"\n");
			}	
		return sb.toString();
	}
	
}
