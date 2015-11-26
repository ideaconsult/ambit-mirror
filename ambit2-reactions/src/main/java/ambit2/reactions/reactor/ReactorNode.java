package ambit2.reactions.reactor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;

import ambit2.smarts.SmartsHelper;

public class ReactorNode 
{
	public static enum State {
		FAILED, SUCCESS, FINISHED, UNFINISHED
	}
	
	public ReactorNode parentNode = null;
	public IAtomContainerSet finalizedProducts = new AtomContainerSet();
	public IAtomContainerSet allowedProducts = new AtomContainerSet();
	public IAtomContainerSet forbiddenProducts = new AtomContainerSet();
	public IAtomContainerSet reagents = new AtomContainerSet();
	
	public int numFinilizedProducts = 0;
	public int numForbiddenProducts = 0;
	public int numAllowedProducts = 0;
	
	public int level = 0;
	
	@Override
	public ReactorNode clone()
	{
		ReactorNode c_node = new ReactorNode();
		
		for (int i = 0; i < finalizedProducts.getAtomContainerCount(); i++)
		{	
			try{
				IAtomContainer ac = finalizedProducts.getAtomContainer(i).clone();
				c_node.finalizedProducts.addAtomContainer(ac);
			}
			catch(Exception x){}
		}
		
		for (int i = 0; i < allowedProducts.getAtomContainerCount(); i++)
		{	
			try{
				IAtomContainer ac = allowedProducts.getAtomContainer(i).clone();
				c_node.allowedProducts.addAtomContainer(ac);
			}
			catch(Exception x){}
		}
		
		for (int i = 0; i < forbiddenProducts.getAtomContainerCount(); i++)
		{	
			try{
				IAtomContainer ac = forbiddenProducts.getAtomContainer(i).clone();
				c_node.forbiddenProducts.addAtomContainer(ac);
			}
			catch(Exception x){}
		}
		
		for (int i = 0; i < reagents.getAtomContainerCount(); i++)
		{	
			try{
				IAtomContainer ac = reagents.getAtomContainer(i).clone();
				c_node.reagents.addAtomContainer(ac);
			}
			catch(Exception x){}
		}
		
		c_node.numForbiddenProducts = numForbiddenProducts;
		c_node.numAllowedProducts = numAllowedProducts;
		c_node.numFinilizedProducts = numFinilizedProducts; 
		
		return c_node;
	}
	
	public String calcNodeHash()
	{
		StringBuffer sb = new StringBuffer();
		List<String> list = new ArrayList<String>();
		
		//Hashing finalizedProducts
		for (int i = 0; i < finalizedProducts.getAtomContainerCount(); i++)
		{
			Object inchiKey = finalizedProducts.getAtomContainer(i).getProperty(Reactor.PropertyInchiKey);
			list.add(inchiKey.toString());
		}
		Collections.sort(list);		
		for (int i = 0; i < list.size(); i++)
			sb.append(list.get(i));
		
		//Hashing allowedProducts
		list.clear();
		for (int i = 0; i < allowedProducts.getAtomContainerCount(); i++)
		{
			Object inchiKey = allowedProducts.getAtomContainer(i).getProperty(Reactor.PropertyInchiKey);
			list.add(inchiKey.toString());
		}
		Collections.sort(list);		
		for (int i = 0; i < list.size(); i++)
			sb.append(list.get(i));
		
		//Hashing forbiddenProducts
		list.clear();
		for (int i = 0; i < forbiddenProducts.getAtomContainerCount(); i++)
		{
			Object inchiKey = forbiddenProducts.getAtomContainer(i).getProperty(Reactor.PropertyInchiKey);
			list.add(inchiKey.toString());
		}
		Collections.sort(list);		
		for (int i = 0; i < list.size(); i++)
			sb.append(list.get(i));
		
		//Hashing reagents
		list.clear();
		for (int i = 0; i < reagents.getAtomContainerCount(); i++)
		{
			Object inchiKey = reagents.getAtomContainer(i).getProperty(Reactor.PropertyInchiKey);
			list.add(inchiKey.toString());
		}
		Collections.sort(list);		
		for (int i = 0; i < list.size(); i++)
			sb.append(list.get(i));
		
		return sb.toString();
	}
	
	public String toString(Reactor reactor)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Node\n");		
		
		sb.append("finalizedProducts:");
		for (int i = 0; i < finalizedProducts.getAtomContainerCount(); i++)
		{	
			String smi = null;
			
			smi = reactor.molToSmiles(finalizedProducts.getAtomContainer(i));
			//try {smi = SmartsHelper.moleculeToSMILES(finalizedProducts.getAtomContainer(i), true);}
			//catch(Exception x){};			
			sb.append("  " + smi 
					/*+ " ("+ finalizedProducts.getAtomContainer(i).getProperty("INCHI_KEY") + ")"*/ );
			
			if (reactor.strategy.FlagLogReactionPath)
				sb.append("<" + reactor.reactionPathToString(
						finalizedProducts.getAtomContainer(i), 
						reactor.strategy.FlagLogNameInReactionPath) + ">");
			
		}
		sb.append("\n");
		
		sb.append("allowedProducts:");
		for (int i = 0; i < allowedProducts.getAtomContainerCount(); i++)
		{	
			String smi = null;
			smi = reactor.molToSmiles(allowedProducts.getAtomContainer(i));
			//try {smi = SmartsHelper.moleculeToSMILES(allowedProducts.getAtomContainer(i), true);}
			//catch(Exception x){};			
			sb.append("  " + smi);
			
			if (reactor.strategy.FlagLogReactionPath)
				sb.append("<" + reactor.reactionPathToString(
						allowedProducts.getAtomContainer(i), 
						reactor.strategy.FlagLogNameInReactionPath) + ">");
		}
		sb.append("\n");
		
		sb.append("forbiddenProducts:");
		for (int i = 0; i < forbiddenProducts.getAtomContainerCount(); i++)
		{	
			String smi = null;
			smi = reactor.molToSmiles(forbiddenProducts.getAtomContainer(i));
			//try {smi = SmartsHelper.moleculeToSMILES(forbiddenProducts.getAtomContainer(i), true);}
			//catch(Exception x){};			
			sb.append("  " + smi);
			
			if (reactor.strategy.FlagLogReactionPath)
				sb.append("<" + reactor.reactionPathToString(
						forbiddenProducts.getAtomContainer(i), 
						reactor.strategy.FlagLogNameInReactionPath) + ">");
		}
		sb.append("\n");
		
		sb.append("reagents:");
		for (int i = 0; i < reagents.getAtomContainerCount(); i++)
		{	
			String smi = null;
			smi = reactor.molToSmiles(reagents.getAtomContainer(i));
			//try {smi = SmartsHelper.moleculeToSMILES(reagents.getAtomContainer(i), true);}
			//catch(Exception x){};			
			sb.append("  " + smi);
			
			if (reactor.strategy.FlagLogReactionPath)
				sb.append("<" + reactor.reactionPathToString(
						reagents.getAtomContainer(i), 
						reactor.strategy.FlagLogNameInReactionPath) + ">");
			
		}
		sb.append("\n");
		
		return sb.toString();
	}
	
}
