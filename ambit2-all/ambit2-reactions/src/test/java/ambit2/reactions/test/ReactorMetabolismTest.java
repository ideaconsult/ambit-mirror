package ambit2.reactions.test;

import java.io.File;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.reactions.ReactionDataBase;
import ambit2.reactions.reactor.Reactor;
import ambit2.reactions.reactor.ReactorResult;
import ambit2.reactions.reactor.ReactorStrategy;
import ambit2.smarts.SmartsHelper;


public class ReactorMetabolismTest extends TestCase
{
	public LoggingTool logger;
	protected Reactor reactor;
	
	public ReactorMetabolismTest() throws Exception
	{
		logger = new LoggingTool(this);
		setupReactor();
	}
	
	public static Test suite() {
		return new TestSuite(ReactorMetabolismTest.class);
	}
	
	
	protected void setupReactor() throws Exception
	{	
		reactor = new Reactor();
		
		//Object o = new Object();
		URL resource = reactor.getClass().getClassLoader().getResource("ambit2/reactions/metabolism-reactions.json");
		
		
		ReactionDataBase reactDB = new ReactionDataBase(new File(resource.getFile()));
				
		reactDB.configureReactions(reactor.getSMIRKSManager());
		reactor.setReactionDataBase(reactDB);		
		
		ReactorStrategy strategy = new ReactorStrategy(new File(resource.getFile()));  //strategy is in the same file
		
		strategy.maxNumOfNodes = 140000;
		strategy.FlagStoreFailedNodes = true;
		strategy.FlagStoreSuccessNodes = true;
		strategy.maxNumOfSuccessNodes = 0;  //if 0 then the reactor will stop after the first success node
		
		strategy.FlagCheckNodeDuplicationOnPush = true;
		strategy.FlagTraceReactionPath = true;
		strategy.FlagLogMainReactionFlow = false;
		strategy.FlagLogReactionPath = false;
		strategy.FlagLogNameInReactionPath = false;
		strategy.FlagLogExplicitHToImplicit = true;
		strategy.FlagLogNumberOfProcessedNodes = true;
		
		reactor.setStrategy(strategy);
		
		//Setup Smirks manager
		reactor.getSMIRKSManager().setFlagProcessResultStructures(true);
		reactor.getSMIRKSManager().setFlagClearImplicitHAtomsBeforeResultProcess(false);
		reactor.getSMIRKSManager().setFlagAddImplicitHAtomsOnResultProcess(false);
		reactor.getSMIRKSManager().setFlagConvertExplicitHToImplicitOnResultProcess(false);
	}
	
	protected boolean metabolize(String smiles) throws Exception
	{	
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smiles, true);
		ReactorResult result = reactor.react(mol);
		System.out.println(smiles + "  nodes = " + result.numReactorNodes);
		return (result.numSuccessNodes > 0);
	}
	
	
	public void testMetabolism01() throws Exception
	{
		
		String smiles = "CO";
		assertEquals("metabolize " + smiles, true, metabolize(smiles));
		
		smiles = "CCO";
		assertEquals("metabolize " + smiles, true, metabolize(smiles));
		
		//smiles = "NCCO";
		//assertEquals("metabolize " + smiles, true, metabolize(smiles));
		
		//GLYCEROL-3P
		smiles = "[H]C(O)(CO)COP(=O)(O)O";
		assertEquals("metabolize " + smiles, true, metabolize(smiles));
		
		//SUCROSE (105039 nodes)
		//smiles = "C2(O)(C(O)C(CO)OC(OC1(CO)(OC(CO)C(O)C1(O)))C2(O))";
		//assertEquals("metabolize " + smiles, true, metabolize(smiles));
				
		//MALTOSE  (24959 nodes)
		//smiles = "C2(O)(C(O)OC(CO)C(OC1(OC(CO)C(O)C(O)C1(O)))C2(O))";
		//assertEquals("metabolize " + smiles, true, metabolize(smiles));
		
		//TREHALOSE (26495 nodes)
		//smiles = "C2(O)(C(O)C(CO)OC(OC1(OC(CO)C(O)C(O)C1(O)))C2(O))";
		//assertEquals("metabolize " + smiles, true, metabolize(smiles));
		
		//LACTOSE (6592 nodes)
		//smiles = "C2(O)(OC(CO)C(OC1(OC(CO)C(O)C(O)C1(O)))C(O)C2(O))";
		//assertEquals("metabolize " + smiles, true, metabolize(smiles));
		
		//Adenosine phosphate (5465 nodes)
		//smiles = "NC3N=CN=C1C=3(N=CN1C2OC(COP(O)(O)=O)C(O)C2(O))";
		//assertEquals("metabolize " + smiles, true, metabolize(smiles));
		
		//Deoxyadenosine monophosphate (2982 nodes)
		//smiles = "NC3=NC=NC2=C3(N=CN2(C1CC(O)C(COP(O)(O)=O)O1))";
		//assertEquals("metabolize " + smiles, true, metabolize(smiles));
		
		//Guanosine monophosphate (5317 nodes)
		//smiles = "NC1NC(=O)C=2N=CN(C=2(N=1))C3OC(COP(O)(O)=O)C(O)C3(O)";
		//assertEquals("metabolize " + smiles, true, metabolize(smiles));
		
		//D-GLUCOSAMINE-6-P (2005 nodes)
		smiles = "[H]C1(N)(C([H])(O)OC([H])(COP(=O)(O)O)C([H])(O)C1([H])(O))";
		assertEquals("metabolize " + smiles, true, metabolize(smiles));
		
		
	}
	
	
}
