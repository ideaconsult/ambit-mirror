package ambit2.reactions.io;


import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import ambit2.reactions.GenericParserUtils;
import ambit2.reactions.GenericRuleMetaInfo;
import ambit2.reactions.sets.ReactionSet;

public class ReactionReadUtils 
{
	private static GenericRuleMetaInfo reactionMetaInfo = null;
	
	private class ReactionDataObject{
		String name = null;
		String set = null;
		String info = null;
		String type = null;
	}
	
	ArrayList<String> errors = new ArrayList<String>();
	
	
	/*
	 * Defining the meta-info to read reactions
	 */
	public static GenericRuleMetaInfo getReactionsMetaInfo()
	{
		if (reactionMetaInfo == null)
		{
			GenericRuleMetaInfo mi = new GenericRuleMetaInfo();
			mi.keyWord.add("NAME");
			mi.objectFieldName.add("name");
			mi.keyWordRequired.add(new Boolean(true));

			mi.keyWord.add("TYPE");
			mi.objectFieldName.add("type");
			mi.keyWordRequired.add(new Boolean(false));

			mi.keyWord.add("SMIRKS");
			mi.objectFieldName.add("smirks");
			mi.keyWordRequired.add(new Boolean(true));		

			mi.keyWord.add("INFO");
			mi.objectFieldName.add("info");
			mi.keyWordRequired.add(new Boolean(false));
						
			reactionMetaInfo = mi;
		}
		
		return(reactionMetaInfo);
	}
	
	
	public ReactionSet loadReactionsFromRuleFormat(String fileName) throws Exception
	{
		errors.clear();
		GenericParserUtils genericParserUtils = new GenericParserUtils(); 
		
		File file = new File(fileName);
		RandomAccessFile f = new RandomAccessFile(file,"r");			
		long length = f.length();
		
		ReactionSet reactionSet = new ReactionSet(); 
		
		int lineNum = 0;
		while (f.getFilePointer() < length)
		{	
			lineNum++;
			String line = f.readLine();
			String ruleString = line.trim();
			if (!ruleString.isEmpty())
			{	
				ReactionDataObject rdo = new ReactionDataObject();
				genericParserUtils.parseRule(ruleString, rdo);
				if (genericParserUtils.getErrors().size() > 0)
					errors.addAll(genericParserUtils.getErrors());
				else
					addReaction(reactionSet, rdo);
			}	
		}
		f.close();
		
		return reactionSet;
	}
	
	private void addReaction(ReactionSet reactionSet, ReactionDataObject rdo)
	{
		//TODO
	}
	
	
	
}
