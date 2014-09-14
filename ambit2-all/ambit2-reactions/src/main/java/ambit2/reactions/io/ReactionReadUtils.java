package ambit2.reactions.io;


import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

import ambit2.reactions.GenericParserUtils;
import ambit2.reactions.GenericRuleMetaInfo;
import ambit2.reactions.sets.ReactionData;
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
	
	
	String curLine = "";
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
			
			mi.keyWord.add("GROUP");
			mi.objectFieldName.add("group");
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
			curLine = line.trim();
			if (!curLine.isEmpty())
			{	
				ReactionDataObject rdo = new ReactionDataObject();
				parser.parseRule(curLine, rdo);
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
		//System.out.println(rdo.toString());
		
		if (rdo.set == null)
		{	
			errors.add("Missing keyword SET in line: " + curLine);
			return;
		}
		
		//Create and register new reaction set if this set is handled for the first time
		ReactionSet s = workSets.get(rdo.set);
		if (s==null)
		{	
			s = new ReactionSet();
			s.setName(rdo.set);
			reactionSets.add(s);
			workSets.put(rdo.set, s);
		}
		
		//Adding new reaction / set info / group info	
		s.addNewReaction(getReactionData(rdo));
		
	}
	
	ReactionData getReactionData(ReactionDataObject rdo)
	{
		ReactionData rd = new ReactionData();
		rd.setName(rdo.name);
		rd.setGroup(rdo.group);
		rd.setSmirks(rdo.smirks);
		rd.setType(rdo.type);
		rd.setInfo(rdo.info);
		return rd;
	}
	
	private String getAllErrors()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < errors.size(); i++)
			sb.append(errors.get(i) + "\n");
		return sb.toString();
	}
	
}
