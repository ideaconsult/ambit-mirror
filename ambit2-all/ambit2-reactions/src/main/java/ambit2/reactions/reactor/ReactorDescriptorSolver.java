package ambit2.reactions.reactor;

import java.util.logging.Logger;

import org.openscience.cdk.tools.LoggingTool;

import ambit2.rules.conditions.AbstractDescriptorSolver;

public class ReactorDescriptorSolver extends AbstractDescriptorSolver
{
	private static Logger logger;
	protected ReactorInfoPack rInfo = null;
	
	public ReactorDescriptorSolver()
	{
		logger = Logger.getLogger("ReactorDescriptorSolver");
		setup();
	}
	
	protected void setup()
	{
		
		//Reactant descriptors
		addDescriptor("REACTION_APPLICATIONS_PER_REACTANT");
		
	}
		
	@Override
	public Object calculateDescriptor(String descrName, Object target) 
	{
		if (target instanceof ReactorInfoPack)
			rInfo = (ReactorInfoPack)target;
		else
			return null;
		
		
		if (descrName.equals("REACTION_APPLICATIONS_PER_REACTANT"))
			return get_REACTION_APPLICATIONS_PER_REACTANT();
		
		logger.info("Uknown descriptor: " + descrName);
		return null;
	}
	
	
	private Double get_REACTION_APPLICATIONS_PER_REACTANT()
	{
		if (rInfo.reaction == null || rInfo.reagent == null)
			return null;
		
		double res = 0.0;
		Object rids = rInfo.reagent.getProperty(Reactor.PropertyReactionIds);
		if (rids == null)
			return res;
		
		int reagent_rids[] = (int[]) rids;
		int n = reagent_rids.length;
		for (int i = 0; i < n; i++)
			if (reagent_rids[i] == rInfo.reaction.getId())
				res = res + 1;
				
		//logger.info("REACTION_APPLICATIONS_PER_REACTANT = " + res);
				
		return res;
	}
	
	
	
}
