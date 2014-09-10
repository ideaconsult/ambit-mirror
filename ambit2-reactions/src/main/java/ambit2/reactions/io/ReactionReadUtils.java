package ambit2.reactions.io;


import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

import ambit2.reactions.GenericParserUtils;
import ambit2.reactions.GenericRuleMetaInfo;
import ambit2.reactions.sets.ReactionSet;

public class ReactionReadUtils 
{
	private static GenericRuleMetaInfo reactionMetaInfo = null;
	
	public class ReactionDataObject
	{		
		public String set = null;
		public String name = null;
		public String group = null;
		public String smirks = null;
		public String info = null;
		public String type = null;
		
		public String toString()
		{
			StringBuffer sb = new StringBuffer();
			if (set != null)
				sb.append(" set = " + set);
			if (name != null)
				sb.append(" name = " + name);
			if (group != null)
				sb.append(" group = " + group);
			if (smirks != null)
				sb.append(" smirks = " + smirks);
			if (info != null)
				sb.append(" info = " + info);
			if (type != null)
				sb.append(" type = " + type);
			return sb.toString();
		}
	}
	
	ArrayList<String> errors = new ArrayList<String>();
	HashMap<String, ReactionSet> workSets = new  HashMap<String, ReactionSet>();
	
	
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
			mi.keyWordRequired.add(new Boolean(false));
			
			mi.keyWord.add("TYPE");
			mi.objectFieldName.add("type");
			mi.keyWordRequired.add(new Boolean(false));

			mi.keyWord.add("SMIRKS");
			mi.objectFieldName.add("smirks");
			mi.keyWordRequired.add(new Boolean(false));		

			mi.keyWord.add("INFO");
			mi.objectFieldName.add("info");
			mi.keyWordRequired.add(new Boolean(false));
			
			mi.keyWord.add("SET");
			mi.objectFieldName.add("set");
			mi.keyWordRequired.add(new Boolean(false));
						
			reactionMetaInfo = mi;
		}
		
		return(reactionMetaInfo);
	}
	
	
	public ArrayList<ReactionSet> loadReactionsFromRuleFormat(String fileName) throws Exception
	{
		errors.clear();
		workSets.clear();
		GenericParserUtils parser = new GenericParserUtils();
		parser.setMetaInfo(getReactionsMetaInfo());
		
		File file = new File(fileName);
		RandomAccessFile f = new RandomAccessFile(file,"r");			
		long length = f.length();
		
		ArrayList<ReactionSet> reactionSets = new ArrayList<ReactionSet>(); 
		
		while (f.getFilePointer() < length)
		{	
			String line = f.readLine();
			String ruleString = line.trim();
			if (!ruleString.isEmpty())
			{	
				ReactionDataObject rdo = new ReactionDataObject();
				parser.parseRule(ruleString, rdo);
				if (parser.getErrors().size() > 0)
					errors.addAll(parser.getErrors());
				else
					addReaction(reactionSets, rdo);
			}	
		}
		f.close();
		
		if (!errors.isEmpty())
			throw new Exception("There are reaction errors: \n" + getAllErrors());
		
		return reactionSets;
	}
	
	public ArrayList<ReactionSet> loadReactionsFromTabulatedText(String fileName) throws Exception
	{
		//TODO
		return null;
	}
	
	private void addReaction(ArrayList<ReactionSet> reactionSets, ReactionDataObject rdo)
	{
		System.out.println(rdo.toString());
		
		if (rdo.smirks == null) //it is info for a set or group with a set
		{
			
		}
		
		//TODO
	}
	
	private String getAllErrors()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < errors.size(); i++)
			sb.append(errors.get(i) + "\n");
		return sb.toString();
	}
	
}
