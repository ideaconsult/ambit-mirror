package ambit2.reactions.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;

import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SmartsConst;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsConst.HandleHAtoms;



public class AmbitSmirksCli 
{
	enum ReactionApplication {
		Target, CombinedOverlappedPos, SingleCopyForEachPos
	}
	
	private static final String title = "Ambit SMIRKS CLI";

	Options options = null;

	public String inputArg = null;
	public String smirksArg = null;
	public String modeArg = null;
	public String applyArg = null;

	SMIRKSManager smrkMan = null;
	ReactionApplication FlagReactionApplication = ReactionApplication.Target;
	SmartsConst.SSM_MODE FlagSSMode = SmartsConst.SSM_MODE.SSM_NON_OVERLAPPING;
	//SmartsConst.SSM_MODE FlagSSModeForSingleCopyForEachPos = SmartsConst.SSM_MODE.SSM_NON_IDENTICAL;

	boolean FlagClearAromaticityBeforePreProcess = true;
	boolean FlagCheckAromaticityOnTargetPreProcess = true;
	boolean FlagTargetPreprocessing = false;
	boolean FlagExplicitHAtoms = false;
	boolean FlagPrintAtomAttributes = false;
	boolean FlagPrintTransformationData = false;

	boolean FlagProductPreprocessing = true;
	boolean FlagClearImplicitHAtomsBeforeProductPreProcess = false;
	boolean FlagClearHybridizationOnProductPreProcess = true;
	boolean FlagAddImplicitHAtomsOnProductPreProcess = false;
	boolean FlagImplicitHToExplicitOnProductPreProcess = false;
	boolean FlagExplicitHToImplicitOnProductPreProcess = false;

	boolean FlagSingleBondAromaticityNotSpecified = false;
	boolean FlagDoubleBondAromaticityNotSpecified = false;

	boolean FlagApplyStereoTransformation = false;
	boolean FlagHAtomsTransformation = false;
	HandleHAtoms FlagHAtomsTransformationMode = HandleHAtoms.IMPLICIT; 
	boolean FlagAromaticityTransformation = false;




	public static void main(String[] args) 
	{
		AmbitSmirksCli cli = new AmbitSmirksCli();
		cli.run(args);
	}

	enum _option {

		smirks {
			@Override
			public String getArgName() {
				return "smirk";
			}
			@Override
			public String getDescription() {
				return "SMIRKS notation";
			}
			@Override
			public String getShortName() {
				return "s";
			}
		},

		input {
			@Override
			public String getArgName() {
				return "smiles";
			}
			@Override
			public String getDescription() {
				return "Input target molecule smiles";
			}
			@Override
			public String getShortName() {
				return "i";
			}
		},

		mode {
			@Override
			public String getArgName() {
				return "mode";
			}
			@Override
			public String getDescription() {
				return "Match mode: all, non-overlapping, non-identical, single";
			}
			@Override
			public String getShortName() {
				return "m";
			}
		},

		apply {
			@Override
			public String getArgName() {
				return "result molecule";
			}
			@Override
			public String getDescription() {
				return "Determines how raction transformation is applied: "
						+ "'target' - transformations for all positions are applied on the target molecule (default); "
						+ "'copy' - single copy each reaction position; "
						+ "'comb' - combinations are generated for all overlapped positions.";
			}
			@Override
			public String getShortName() {
				return "a";
			}
		},

		help {
			@Override
			public String getArgName() {
				return null;
			}
			@Override
			public String getDescription() {
				return title;
			}
			@Override
			public String getShortName() {
				return "h";
			}
			@Override
			public String getDefaultValue() {
				return null;
			}
			public Option createOption() {
				Option option   = OptionBuilder.withLongOpt(name())
						.withDescription(getDescription())
						.create(getShortName());
				return option;
			}
		};		

		public abstract String getArgName();
		public abstract String getDescription();
		public abstract String getShortName();
		public String getDefaultValue() { return null; }

		public Option createOption() {
			String defaultValue = getDefaultValue();
			Option option   = OptionBuilder.withLongOpt(name())
					.hasArg()
					.withArgName(getArgName())
					.withDescription(String.format("%s %s %s",getDescription(),defaultValue==null?"":"Default value: ",defaultValue==null?"":defaultValue))
					.create(getShortName());

			return option;
		}
	}

	protected static Options createOptions() {
		Options options = new Options();
		for (_option o: _option.values()) {
			options.addOption(o.createOption());
		}
		return options;
	}

	protected static void printHelp(Options options,String message) {
		if (message!=null) System.out.println(message);
		System.out.println(title);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( AmbitSmirksCli.class.getName(), options );
	}

	public void setOption(_option option, String argument) throws Exception {
		if (argument != null)
			argument = argument.trim();
		switch (option) {
		case input: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			inputArg = argument;
			break;
		}
		case smirks: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			smirksArg = argument;
			break;
		}
		case mode: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			modeArg = argument;
			break;
		}
		case apply: {
			if ((argument == null) || "".equals(argument.trim()))
				return;
			applyArg = argument;
			break;
		}
		}
	}

	public int run(String[] args) 
	{
		options = createOptions();

		final CommandLineParser parser = new PosixParser();
		try {
			CommandLine line = parser.parse( options, args,false );
			if (line.hasOption(_option.help.name())) {
				printHelp(options, null);
				return -1;
			}

			for (_option o: _option.values()) 
				if (line.hasOption(o.getShortName())) try {
					setOption(o,line.getOptionValue(o.getShortName()));
				} catch (Exception x) {
					printHelp(options,x.getMessage());
					return -1;
				}

			int res = runSmirks();
			if (res != 0)
			{
				System.out.println();
			}	
			return res;

		} catch (Exception x ) {
			System.out.println(x.getMessage());
			x.printStackTrace();
			//printHelp(options,x.getMessage());
			return -1;
		} finally {
			try { 
				//run whatever cleanup is needed
			} catch (Exception xx) {
				printHelp(options,xx.getMessage());
			}
		}
	}

	protected int runSmirks() throws Exception
	{	
		smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
		
		// Product processing flags
		smrkMan.getSmartsParser().mSupportSingleBondAromaticityNotSpecified = FlagSingleBondAromaticityNotSpecified;
		smrkMan.getSmartsParser().mSupportDoubleBondAromaticityNotSpecified = FlagDoubleBondAromaticityNotSpecified;
				
		
		if (smirksArg == null)
		{
			System.out.println("Smirks arument is not set!\n");
			printHelp(options, null);
			return -1;
		}

		SMIRKSReaction reaction = smrkMan.parse(smirksArg);
		if (!smrkMan.getErrors().equals("")) {
			System.out.println("Smirks parsing errors for smirks: " + smirksArg +"\n" +  smrkMan.getErrors());
			return -2;
		}

		if (FlagPrintTransformationData)
			System.out.println(reaction.transformationDataToString());

		if (inputArg == null)
		{	
			System.out.println("Input arument is not set!\n");
			printHelp(options, null);
			return -3;
		}	

		IAtomContainer target = null;
		try
		{
			target = getMoleculeFromInput(inputArg);
		}
		catch (Exception x)
		{
			System.out.println("Input structure error: " + x.getMessage());
			return -4;
		}
		
		if (target == null)
		{
			System.out.println("Input structure error: null structure");
			return -4;
		}
		
		if (setMode() != 0)
		{
			System.out.println("Incorrect mode: " + modeArg + "\n");
			printHelp(options, null);
			return -5;
		}
		
		applyReaction(reaction, target);
		


		//TODO
		return 0;
	}
	
	IAtomContainer getMoleculeFromInput(String inputStr) throws Exception
	{	
		//Handle input as SMILES
		IAtomContainer target = SmartsHelper.getMoleculeFromSmiles(inputArg);
		
		return target;
	}
	
	int setMode()
	{
		if (modeArg == null)
			return 0;
		
		if (modeArg.equalsIgnoreCase("all"))
		{	
			FlagSSMode = SmartsConst.SSM_MODE.SSM_ALL;
			return 0;
		}	
					
		if (modeArg.equalsIgnoreCase("non-overlapping"))
		{	
			FlagSSMode = SmartsConst.SSM_MODE.SSM_NON_OVERLAPPING;
			return 0;
		}	
		
		if (modeArg.equalsIgnoreCase("non-identical"))
		{	
			FlagSSMode = SmartsConst.SSM_MODE.SSM_NON_IDENTICAL;
			return 0;
		}	
		
		if (modeArg.equalsIgnoreCase("single"))
		{	
			FlagSSMode = SmartsConst.SSM_MODE.SSM_NON_IDENTICAL_FIRST;
			return 0;
		}	
		
		return -1;
	}
	
	void applyReaction(SMIRKSReaction reaction, IAtomContainer target) throws Exception
	{
		smrkMan.setFlagSSMode(FlagSSMode);
		smrkMan.setFlagProcessResultStructures(FlagProductPreprocessing);
		smrkMan.setFlagClearHybridizationBeforeResultProcess(FlagClearHybridizationOnProductPreProcess);
		smrkMan.setFlagClearImplicitHAtomsBeforeResultProcess(FlagClearImplicitHAtomsBeforeProductPreProcess);
		smrkMan.setFlagClearAromaticityBeforeResultProcess(FlagClearAromaticityBeforePreProcess);
		smrkMan.setFlagAddImplicitHAtomsOnResultProcess(FlagAddImplicitHAtomsOnProductPreProcess);
		smrkMan.setFlagConvertAddedImplicitHToExplicitOnResultProcess(FlagImplicitHToExplicitOnProductPreProcess);
		smrkMan.setFlagConvertExplicitHToImplicitOnResultProcess(FlagExplicitHToImplicitOnProductPreProcess);
		smrkMan.setFlagApplyStereoTransformation(FlagApplyStereoTransformation);
		smrkMan.setFlagHAtomsTransformation(FlagHAtomsTransformation);
		smrkMan.setFlagHAtomsTransformationMode(FlagHAtomsTransformationMode);
		smrkMan.setFlagAromaticityTransformation(FlagAromaticityTransformation);
		
		/*
		if (FlagTargetPreprocessing)
			preProcess(target);
		 */	

		if (FlagPrintAtomAttributes) {
			System.out.println("Target (reactant):");
			System.out.println("Reactant atom attributes:\n"
					+ SmartsHelper.getAtomsAttributes(target));
			System.out.println("Reactant bond attributes:\n"
					+ SmartsHelper.getBondAttributes(target));
		}

		System.out.println("    "
				+ SmartsHelper.moleculeToSMILES(target, true));

		switch (FlagReactionApplication) {
		case Target:
			boolean res = smrkMan.applyTransformation(target, reaction);

			String transformedSmiles = SmartsHelper.moleculeToSMILES(target,
					true);

			if (res)
			{	
				System.out.println("Reaction application: " + inputArg
						+ "  -->  " + transformedSmiles + "    abs. smiles res " + 
						SmilesGenerator.absolute().create(target));
			}	
			else
				System.out.println("Reaction not appicable!");
			break;

		case CombinedOverlappedPos:
			IAtomContainerSet resSet = smrkMan
			.applyTransformationWithCombinedOverlappedPos(target, null,
					reaction);
			if (resSet == null)
				System.out.println("Reaction not appicable!");
			else {
				System.out
				.println("Reaction application With Combined Overlapped Positions: ");
				for (int i = 0; i < resSet.getAtomContainerCount(); i++)
					System.out.println(SmartsHelper.moleculeToSMILES(
							resSet.getAtomContainer(i), true));
			}
			break;

		case SingleCopyForEachPos:
			IAtomContainerSet resSet2 = 
				smrkMan.applyTransformationWithSingleCopyForEachPos(target, null, reaction, FlagSSMode);
			if (resSet2 == null)
				System.out.println("Reaction not appicable!");
			else {
				System.out
				.println("Reaction application With Single Copy For Each Position: ");
				for (int i = 0; i < resSet2.getAtomContainerCount(); i++)
					System.out.println(SmartsHelper.moleculeToSMILES(
							resSet2.getAtomContainer(i), true));
			}
			break;
		}

		System.out.println();
	}



}
