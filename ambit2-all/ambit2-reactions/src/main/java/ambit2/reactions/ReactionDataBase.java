package ambit2.reactions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.smarts.SMIRKSManager;


public class ReactionDataBase 
{
	private static Logger logger = Logger.getLogger(ReactionDataBase.class.getName());
	
	//public List<Reaction> reactions = null;
	public List<GenericReaction> genericReactions = null;
	
	
	public ReactionDataBase()
	{	
	}
	
	public ReactionDataBase(File jsonFile) throws Exception
	{	
		loadReactionsFromJSON(jsonFile, true);
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
		if (genericReactions == null)
			return;
		
		for (GenericReaction reaction : genericReactions)
			reaction.configure(smrkMan);
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
