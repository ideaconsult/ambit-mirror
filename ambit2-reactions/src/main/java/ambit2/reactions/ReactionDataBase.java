package ambit2.reactions;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import ambit2.smarts.SMIRKSManager;


public class ReactionDataBase 
{
	private static Logger logger = Logger.getLogger(ReactionDataBase.class.getName());
	
	public List<Reaction> reactions = null;
	
	
	public ReactionDataBase()
	{	
	}
	
	public ReactionDataBase(File jsonFile) throws Exception
	{	
		loadReactionsFromJSON(jsonFile, true);
	}
	
	public void loadReactionsFromJSON(File jsonFile, boolean FlagCleanDB) throws Exception
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
		
		if (FlagCleanDB)
			reactions = new ArrayList<Reaction>();
		else
			if (reactions == null)
				reactions = new ArrayList<Reaction>();
		
		JsonNode reactionsNode = root.path("REACTIONS");
		if (reactionsNode.isMissingNode())
			throw new Exception ("REACTIONS section is missing!");
		
		if (!reactionsNode.isArray())
			throw new Exception ("REACTIONS section is not array!");
		
		for (int i = 0; i < reactionsNode.size(); i++)
		{
			try{
				Reaction reaction = Reaction.getReactionFromJsonNode(reactionsNode.get(i));
				reaction.setId(i+1);
				if (reaction.isFlagUse())
					reactions.add(reaction);
			}
			catch(Exception e)
			{
				logger.info("Error on reading REACTIONS element #"+ (i+1) + ": " + e.getMessage());
			}
		}
	}
	
	public void configureReactions(SMIRKSManager smrkMan) throws Exception
	{
		if (reactions == null)
			return;
		
		for (Reaction reaction : reactions)
			reaction.configure(smrkMan);
	}
	
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
	
}
