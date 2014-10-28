package ambit2.smarts.test;

import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.HydrogenAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.LogicalOperatorAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.RecursiveSmartsAtom;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.io.MyIteratingMDLReader;
import ambit2.smarts.ChemObjectFactory;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.Screening;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsManager;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.StructInfo;

//This class provides utilities for automatic testing of Substructure Searching algorithms
public class AutomaticTestUtilities {

	class CmdOption {
		String option = null;
		String value = null;
	};

	class StatisticsData {
		boolean firstPass = true;
		String info = "";
		int numObjects = 0;
		double sum = 0.0;
		double sum_std = 0.0;
		double average = 0.0;
		double std = 0.0;

		public void addValue(double value) {
			if (firstPass) {
				sum += value;
				numObjects++;
			} else
				sum_std += (value - average) * (value - average);
		}

		public double getAverage() {
			if (numObjects == 0)
				average = 0;
			else
				average = sum / numObjects;

			return (average);
		}

		public double getStdDev() {
			return (Math.sqrt(sum_std / (numObjects - 1)));
		}

		public double getRSD() {
			return (100.0 * Math.sqrt(sum_std / (numObjects - 1)) / average);
		}

	};

	public static final int LPM_SSS_AMBIT = 0;
	public static final int LPM_SSS_CDK = 1;
	public static final int LPM_SSS_AMBIT_CDK = 2;
	public static final int LPM_SSS_CDK_AMBIT = 3;
	public static final int LPM_SSS_AMBIT_SMSD = 4;
	public static final int LPM_SSS_CDK_SMSD = 5;
	public static final int LPM_SSS_ALL = 100;
	public static final int LPM_PARSERS_ALL = 101;
	public static final int LPM_CALC_STAT = 102;
	public static final int LPM_SIZE_STAT = 103;
	public static final int LPM_FULL_SMARTS = 110;

	public static final int STAT_SINGLE_DBSTR = 1;
	public static final int STAT_ENTIRE_DB = 2;
	public static final int STAT_OCCURENCES = 10;

	String endLine = "\r\n";
	String outFileName = "";
	String inFileName = "";
	String inFileName2 = "";
	String inFileName3 = "";
	String inFileName4 = "";
	String inFileName5 = "";

	String dbFileName = "";
	String command = "";
	String configFileName = "";

	RandomAccessFile outFile = null;

	// start at 1248 for the garbage collector tests
	boolean FlagTargetLargerThanQuery = false;
	boolean FlagGarbCollector = false;
	int maxNumSeqSteps = 6;
	int maxStrSize = 50;
	int minGenStrSize = 5;
	int maxStatLines = 10000000;
	int nDBStr = 1000;
	int nInputStr = 10;
	int nOutputStr = 0;
	int nBits = 0;
	int portionSize = 100;
	int lineProcessMode = 0;
	int statisticsType = STAT_SINGLE_DBSTR;

	boolean FlagStat_SingleDBStr_Ambit = false; // ambit parser and isomorphims
	boolean FlagStat_SingleDBStr_CDK = false; // cdk parser and isomorphims
	boolean FlagStat_SingleDBStr_Ambit_CDK = false; // ambit parser + cdk
													// isomorphims
	boolean FlagStat_SingleDBStr_CDK_Ambit = false; // cdk parser + ambit
													// isomorphism
	// boolean FlagStat_SingleDBStr_Ambit_SMSD = false; //ambit parser + SMSD
	// isomorphims
	// boolean FlagStat_SingleDBStr_CDK_SMSD = false; //cdk parser + SMSD
	// isomorphism

	SmartsManager man = new SmartsManager(SilentChemObjectBuilder.getInstance());
	SmartsParser spAmbit = new SmartsParser();
	IsomorphismTester isoTester = new IsomorphismTester();

	// Statistics variables
	String statLinePrefix = "###";
	String[] statMethods;
	StatisticsData statAllObjs[];
	StatisticsData statAllObjs_MapTrue[];
	StatisticsData statAllObjs_MapFalse[];
	StatisticsData statAllObjs_MapFalse_[];
	int binsStrSize[] = { 5, 10, 20, 30, 40, 50 };
	Vector<StatisticsData[]> statBins = new Vector<StatisticsData[]>(); // according
																		// to
																		// the
																		// query
																		// size
	Vector<StatisticsData[]> statBins2 = new Vector<StatisticsData[]>(); // according
																			// to
																			// the
																			// db
																			// str.
																			// size

	int statCurSrtSize = 0;
	int statCurBinIndex = 0;

	public static void main(String[] args) {
		AutomaticTestUtilities atu = new AutomaticTestUtilities();
		// exhaustive
		try {
			atu.handleArguments(new String[] {
					"-db",
					"D:/nina/IdeaConsult/Projects/CADASTER/Data for 2011 models - TAZ&BTAZ/TAZ_BTAZ_FishToxicity/fragments/6.sdf",
					"-c",
					"exhaustive-str",
					"-o",
					"D:/nina/IdeaConsult/Projects/CADASTER/Data for 2011 models - TAZ&BTAZ/TAZ_BTAZ_FishToxicity/fragments/6strucs_big.txt",
			// "-nDBStr","1"
			});
		} catch (Exception x) {
			x.printStackTrace();
		}

		// atu.handleArguments(new String[]
		// {"-db","/gen-str-seq40-db-after-40000.txt", "-i","/keys-eff80.txt",
		/*
		 * atu.handleArguments(new String[]
		 * {"-db","/einecs_structures_V13Apr07.sdf",
		 * "-i2","/query04-mutag.txt","-i","/sss-tests-query04.txt",
		 * "-o","/stat-tests-query04__.txt", "-nDBStr", "2000", "-maxSeqStep",
		 * "40", "-c", "calc-stat", "-nBits", "32"});
		 */
		// atu.produceRandomStructures();
	}

	public int handleArguments(String[] args) throws Exception {
		if (args.length == 0) {
			printHelp();
			return (0);
		}

		Vector<CmdOption> options = extractOptions(args);
		// for (int i = 0; i < v.size(); i++)
		// System.out.println(v.get(i).option + "  " +v.get(i).value);

		CmdOption opt;

		opt = getOption("i", options);
		if (opt != null) {
			if (opt.value != null)
				inFileName = opt.value;
		}

		opt = getOption("i2", options);
		if (opt != null) {
			if (opt.value != null)
				inFileName2 = opt.value;
		}

		opt = getOption("i3", options);
		if (opt != null) {
			if (opt.value != null)
				inFileName3 = opt.value;
		}

		opt = getOption("4", options);
		if (opt != null) {
			if (opt.value != null)
				inFileName4 = opt.value;
		}

		opt = getOption("i5", options);
		if (opt != null) {
			if (opt.value != null)
				inFileName5 = opt.value;
		}

		opt = getOption("o", options);
		if (opt != null) {
			if (opt.value != null)
				outFileName = opt.value;
		}

		opt = getOption("db", options);
		if (opt != null) {
			if (opt.value != null)
				dbFileName = opt.value;
		}

		opt = getOption("c", options);
		if (opt != null) {
			if (opt.value != null)
				command = opt.value;
		}

		opt = getOption("nDBStr", options);
		if (opt != null) {
			if (opt.value != null) {
				try {
					Integer intObj = new Integer(0);
					nDBStr = Integer.parseInt(opt.value);
				} catch (Exception e) {
					System.out.println("Incorrect nDBStr value: "
							+ e.toString());
				}
			}
		}

		opt = getOption("nInpStr", options);
		if (opt != null) {
			if (opt.value != null) {
				try {
					Integer intObj = new Integer(0);
					nInputStr = Integer.parseInt(opt.value);
				} catch (Exception e) {
					System.out.println("Incorrect nInpStr value: "
							+ e.toString());
				}
			}
		}

		opt = getOption("nOutStr", options);
		if (opt != null) {
			if (opt.value != null) {
				try {
					Integer intObj = new Integer(0);
					nOutputStr = Integer.parseInt(opt.value);
				} catch (Exception e) {
					System.out.println("Incorrect nOutStr value: "
							+ e.toString());
				}
			}
		}

		opt = getOption("nBits", options);
		if (opt != null) {
			if (opt.value != null) {
				try {
					Integer intObj = new Integer(0);
					nBits = Integer.parseInt(opt.value);
				} catch (Exception e) {
					System.out
							.println("Incorrect nBits value: " + e.toString());
				}
			}
		}

		opt = getOption("maxSeqStep", options);
		if (opt != null) {
			if (opt.value != null) {
				try {
					Integer intObj = new Integer(0);
					this.maxNumSeqSteps = Integer.parseInt(opt.value);
				} catch (Exception e) {
					System.out.println("Incorrect maxSeqStep value: "
							+ e.toString());
				}
			}
		}

		opt = getOption("statType", options);
		if (opt != null) {
			if (opt.value != null) {
				int stT = -1;
				if (opt.value.equals("single-str"))
					stT = STAT_SINGLE_DBSTR;

				if (opt.value.equals("entire-db"))
					stT = STAT_ENTIRE_DB;

				if (stT == -1)
					System.out.println("Incorrect statistics type: "
							+ opt.value);
				else
					statisticsType = stT;
			}
		}

		handleCommand();

		return (0);
	}

	public int handleCommand() throws Exception {
		if (command == null)
			return (0);

		if (command.equals("random-str")) {
			System.out.println("Running generation of random structures:");
			ChemObjectFactory cof = new ChemObjectFactory(
					SilentChemObjectBuilder.getInstance());
			Vector<StructInfo> vStr = new Vector<StructInfo>();
			cof.produceRandomStructsFromMDL(dbFileName, maxNumSeqSteps,
					minGenStrSize, nDBStr, vStr, outFileName);
			return (0);
		}

		if (command.equals("exhaustive-str")) {
			System.out.println("Running exhaustive generation structures:");
			ChemObjectFactory cof = new ChemObjectFactory(
					SilentChemObjectBuilder.getInstance());
			Vector<StructInfo> vStr = new Vector<StructInfo>();
			cof.produceStructsFromMDL(dbFileName, maxNumSeqSteps, nDBStr,
					maxStrSize, vStr, outFileName);
			return (0);
		}

		if (command.equals("sss-ambit")) {
			System.out
					.println("Running sss with Ambit Parser and Isomorphims algorithm:");
			openOutputFile();
			lineProcessMode = LPM_SSS_AMBIT;
			iterateInputFile();
			closeOutputFile();
			return (0);
		}

		if (command.equals("sss-cdk")) {
			System.out
					.println("Running sss with CDK Parser and Isomprphims algorithm:");
			openOutputFile();
			lineProcessMode = LPM_SSS_CDK;
			iterateInputFile();
			closeOutputFile();
			return (0);
		}

		if (command.equals("sss-ambit-cdk")) {
			System.out
					.println("Running sss with Ambit Parser and CDK isomprphims algorithm:");
			openOutputFile();
			lineProcessMode = LPM_SSS_AMBIT_CDK;
			iterateInputFile();
			closeOutputFile();
			return (0);
		}

		if (command.equals("sss-cdk-ambit")) {
			System.out
					.println("Running sss with CDK Parser and Ambit isomprphims algorithm:");
			openOutputFile();
			lineProcessMode = LPM_SSS_CDK_AMBIT;
			iterateInputFile();
			closeOutputFile();
			return (0);
		}

		if (command.equals("sss-all")) {
			System.out.println("Running sss with all isomprphims algorithm:");
			openOutputFile();
			lineProcessMode = LPM_SSS_ALL;
			iterateInputFile();
			closeOutputFile();
			return (0);
		}

		if (command.equals("full-smarts")) {
			System.out.println("Running sss with all isomprphims algorithm:");
			openOutputFile();
			lineProcessMode = LPM_FULL_SMARTS;
			iterateInputFile();
			closeOutputFile();
			return (0);
		}

		if (command.equals("parsers-all")) {
			System.out
					.println("Running comparison of the SMARTS parsers: Ambit, CDK");
			openOutputFile();
			lineProcessMode = LPM_PARSERS_ALL;

			output("smiles  timeAmbit   timeCDK" + endLine);
			iterateInputFile();
			closeOutputFile();
			return (0);
		}

		if (command.equals("calc-stat")) {
			System.out.println("Calculates statistics");
			openOutputFile();
			lineProcessMode = LPM_CALC_STAT;
			// iterateInputFile();
			makeStatistics(inFileName);
			closeOutputFile();
			return (0);
		}

		if (command.equals("size-stat")) {
			System.out.println("Calculates statistics");
			openOutputFile();
			lineProcessMode = LPM_SIZE_STAT;
			iterateInputFile();
			closeOutputFile();
			return (0);
		}

		if (command.equals("calc-bitsets")) {
			System.out.println("Calculates bit sets for each db structure");
			openOutputFile();
			// lineProcessMode =
			calcBitVectorStrings();
			closeOutputFile();
			return (0);
		}

		if (command.equals("calc-screen-ratio")) {
			System.out.println("Calculates screenign ratio for each query");
			openOutputFile();
			// lineProcessMode =
			calcScreeningRatio();
			closeOutputFile();
			return (0);
		}

		System.out.println("Unknown command: " + command);

		return (-1);
	}

	public void printHelp() {
		System.out
				.println("Program for automatic testing of structure searching algorithms");
		System.out.println("-h            print help info");
		System.out.println("-i            input file");
		System.out.println("-i<n>         additional input files n = 2,3,4,5");
		System.out.println("-o            output file");
		System.out.println("-db           db file");
		System.out.println("-cfg          config file");
		System.out
				.println("-nDBStr       the number of used structures from DB");
		System.out.println("-nInpStr      the number of used input structures");
		System.out
				.println("-nOutStr      the number of used output structures");
		System.out.println("-maxSeqStep   maximal number of sequence steps");
		System.out
				.println("-maxStrSize   maximal size of the generated structures");
		System.out
				.println("-statType     the type of statistics saved in the output file");
		System.out.println("                 single-str       ");
		System.out.println("                 entire-db        ");
		System.out.println("-c            command: ");
		System.out
				.println("                 random-str       generates random structures");
		System.out
				.println("                 exhaustive-str   generates structures exhaustively");
		System.out
				.println("                 sss-ambit        substructure searching with Ambit Aprser and Algorithm");
		System.out
				.println("                 sss-cdk          substructure searching with CDK Parser and Algorithm");
		System.out
				.println("                 sss-ambit-cdk    substructure searching with Ambit Parser and CDK Algorithm");
		System.out
				.println("                 sss-cdk-ambit    substructure searching with CDK Parser and Ambit Algorithm");
		System.out
				.println("                 sss-all          substructure searching with all algorithms");
		System.out
				.println("                 full-smarts      substructure searching with full smarts queries");
		System.out
				.println("                 parsers-all      comparison of the parsing time");
		System.out
				.println("                 cals-stat        calculates statistics from previous tests");
		System.out
				.println("                 size-stat        calculates the sizes of input structures");
		System.out
				.println("                 calc-bitsets     calculates bitset for each db structure");
		System.out
				.println("                 calc-screen-ratio  calculates scrrenig ration for a set of queries");
		System.out
				.println("                                    -i queries-bits; -i2 db-bits");

	}

	public Vector<CmdOption> extractOptions(String[] args) {
		CmdOption curOption = null;
		Vector<CmdOption> v = new Vector<CmdOption>();
		if (args.length == 0)
			return (v);

		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("--")) {
				if (curOption != null)
					v.add(curOption);

				curOption = new CmdOption();
				curOption.option = args[i].substring(2);
			} else if (args[i].startsWith("-")) {
				if (curOption != null)
					v.add(curOption);

				curOption = new CmdOption();
				curOption.option = args[i].substring(1);
			} else {
				if (curOption == null) // This is the first argument and it is
										// not an option
				{
					curOption = new CmdOption();
					curOption.option = "";
					curOption.value = args[i];
				} else {
					if (curOption.value == null)
						curOption.value = args[i];
					else
						curOption.value += " " + args[i];
				}
			}
		}

		v.add(curOption);

		return (v);
	}

	CmdOption getOption(String opt, Vector<CmdOption> options) {
		for (int i = 0; i < options.size(); i++) {
			if (options.get(i).option.equals(opt))
				return (options.get(i));
		}

		return null;
	}

	// Input - Output handling

	int openOutputFile() {
		try {
			File file = new File(outFileName);
			outFile = new RandomAccessFile(file, "rw");
			outFile.setLength(0);
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		if (outFile == null)
			System.out.println("Incorrect outFile");

		return (0);
	}

	int closeOutputFile() {
		try {
			if (outFile != null)
				outFile.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return (0);
	}

	int output(String data) {
		try {
			outFile.write(data.getBytes());
		} catch (Exception e) {
			System.out.println("output error: " + e.toString());
			return (-1);
		}
		return (0);
	}

	void iterateInputFile() {
		try {
			File file = new File(inFileName);
			RandomAccessFile f = new RandomAccessFile(file, "r");
			long length = f.length();

			int n = 0;
			while (f.getFilePointer() < length) {
				n++;
				String line = f.readLine();
				System.out.println("line " + n + "  " + line);
				processLine(line.trim());
			}

			f.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// Substructure search comparison and statistics utilities

	int processLine(String line) {
		if (lineProcessMode == LPM_PARSERS_ALL) {
			compareParsers(line);
			return (0);
		}

		if (statisticsType == STAT_SINGLE_DBSTR) {
			switch (this.lineProcessMode) {
			case LPM_SSS_AMBIT:
				FlagStat_SingleDBStr_Ambit = true;
				sss_SingleDBStrStat(line);
				break;
			case LPM_SSS_CDK:
				FlagStat_SingleDBStr_CDK = true;
				sss_SingleDBStrStat(line);
				break;
			case LPM_SSS_AMBIT_CDK:
				FlagStat_SingleDBStr_Ambit_CDK = true;
				sss_SingleDBStrStat(line);
				break;
			case LPM_SSS_CDK_AMBIT:
				FlagStat_SingleDBStr_CDK_Ambit = true;
				sss_SingleDBStrStat(line);
				break;

			case LPM_SSS_ALL:
				FlagStat_SingleDBStr_Ambit = true;
				FlagStat_SingleDBStr_CDK = true;
				FlagStat_SingleDBStr_Ambit_CDK = true;
				FlagStat_SingleDBStr_CDK_Ambit = true;
				//FlagStat_SingleDBStr_Ambit_SMSD = true;
				//FlagStat_SingleDBStr_CDK_SMSD = true;
				sss_SingleDBStrStat(line);
				break;

			case LPM_FULL_SMARTS:
				FlagStat_SingleDBStr_Ambit = true;
				FlagStat_SingleDBStr_CDK = true;
				sss_SingleDBStrStat_FullSmarts(line);
				break;

			case LPM_CALC_STAT:
				// It is not done here.
				// A separate input file iteration function is used.
				break;

			case LPM_SIZE_STAT:
				processLineForSizeStatistics(line);
				break;
			}

			return (0);
		}

		if (statisticsType == STAT_ENTIRE_DB) {
			switch (this.lineProcessMode) {
			case LPM_SSS_AMBIT:
				sss_Ambit(line);
				break;
			case LPM_SSS_CDK:
				sss_CDK(line);
				break;
			case LPM_SSS_AMBIT_CDK:
				sss_Ambit_CDK(line);
				break;
			}
		}

		return (0);
	}

	int sss_Ambit(String line) {
		return (0);
	}

	int sss_CDK(String line) {
		return (0);
	}

	int sss_Ambit_CDK(String line) {
		return (0);
	}

	// ----------Processing of full smarts standard queries

	int sss_SingleDBStrStat_FullSmarts(String line) {
		// Performs statistics for each structure from the DB
		// It is applied for several algorithms simultaneously

		long startTime, endTime;
		long timeAmbit = 0;
		long timeCDK = 0;

		// Ambit parser
		// QueryAtomContainer query_ambit = spAmbit.parse(line);
		// spAmbit.setNeededDataFlags();
		// String errorMsg = spAmbit.getErrorMessages();

		man.setQuery(line);
		String errorMsg = man.getErrors();
		if (!errorMsg.equals("")) {
			System.out.println("Smarts Parser errors:\n" + errorMsg);
			return -1;
		}

		// CDK parser
		QueryAtomContainer query_CDK = null;
		try {
			query_CDK = SMARTSParser.parse(line);
		} catch (Exception e) {
			System.out.println("CDK parsing error: " + e.toString());
			return -1;
		}

		// Initial line in the output
		output("###test  Ambit  CDK  " + endLine);
		output("###  " + query_CDK.getAtomCount() + "   " + line + endLine);

		try {
			IChemObjectBuilder b = SilentChemObjectBuilder.getInstance();
			MyIteratingMDLReader reader = new MyIteratingMDLReader(
					new FileReader(dbFileName), b);
			int record = 0;

			while (reader.hasNext()) {
				record++;
				if (record > this.nDBStr)
					break;

				if (record % 100 == 0)
					System.out.println("db-rec " + record);

				Object o = reader.next();
				if (o instanceof IAtomContainer) {
					IAtomContainer mol = (IAtomContainer) o;
					if (mol.getAtomCount() == 0)
						continue;

					initializeMolecule(mol);
					// AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
					// CDKHueckelAromaticityDetector.detectAromaticity(mol);

					int sssResultCDK = 0;
					int sssResultAmbit = 0;

					// Ambit test
					man.calcSmartsDataForTarget(mol);
					man.setUseCDKIsomorphismTester(false);
					man.setSmartsDataForTarget(false);

					startTime = System.nanoTime();
					boolean res0 = man.searchIn(mol);
					endTime = System.nanoTime();
					timeAmbit = endTime - startTime;
					if (res0)
						sssResultAmbit = 1;

					// CDK test
					startTime = System.nanoTime();
					initializeRecursiveSmarts(query_CDK, mol);
					boolean res = UniversalIsomorphismTester.isSubgraph(mol,
							query_CDK);
					endTime = System.nanoTime();
					timeCDK = endTime - startTime;

					if (res)
						sssResultCDK = 1;

					output("db-" + record + "  " + mol.getAtomCount() + "  "
							+ sssResultAmbit + sssResultCDK + "       "
							+ timeAmbit + "  " + timeCDK + endLine);

				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			return (-1);
		}

		// TODO
		return (0);
	}

	private void initializeRecursiveSmarts(QueryAtomContainer query,
			IAtomContainer atomContainer) throws CDKException {
		for (IAtom atom : query.atoms()) {
			initializeRecursiveSmartsAtom(atom, atomContainer);
		}
	}

	private void initializeRecursiveSmartsAtom(IAtom atom,
			IAtomContainer atomContainer) throws CDKException {
		if (atom instanceof LogicalOperatorAtom) {
			initializeRecursiveSmartsAtom(
					((LogicalOperatorAtom) atom).getLeft(), atomContainer);
			if (((LogicalOperatorAtom) atom).getRight() != null) {
				initializeRecursiveSmartsAtom(
						((LogicalOperatorAtom) atom).getRight(), atomContainer);
			}
		} else if (atom instanceof RecursiveSmartsAtom) {
			((RecursiveSmartsAtom) atom).setAtomContainer(atomContainer);
		} else if (atom instanceof HydrogenAtom) {
			((HydrogenAtom) atom).setAtomContainer(atomContainer);
		}
	}

	private void initializeMolecule(IAtomContainer atomContainer)
			throws CDKException {
		// Code copied from
		// org.openscience.cdk.qsar.descriptors.atomic.AtomValenceDescriptor;
		Map<String, Integer> valencesTable = new HashMap<String, Integer>();
		valencesTable.put("H", 1);
		valencesTable.put("Li", 1);
		valencesTable.put("Be", 2);
		valencesTable.put("B", 3);
		valencesTable.put("C", 4);
		valencesTable.put("N", 5);
		valencesTable.put("O", 6);
		valencesTable.put("F", 7);
		valencesTable.put("Na", 1);
		valencesTable.put("Mg", 2);
		valencesTable.put("Al", 3);
		valencesTable.put("Si", 4);
		valencesTable.put("P", 5);
		valencesTable.put("S", 6);
		valencesTable.put("Cl", 7);
		valencesTable.put("K", 1);
		valencesTable.put("Ca", 2);
		valencesTable.put("Ga", 3);
		valencesTable.put("Ge", 4);
		valencesTable.put("As", 5);
		valencesTable.put("Se", 6);
		valencesTable.put("Br", 7);
		valencesTable.put("Rb", 1);
		valencesTable.put("Sr", 2);
		valencesTable.put("In", 3);
		valencesTable.put("Sn", 4);
		valencesTable.put("Sb", 5);
		valencesTable.put("Te", 6);
		valencesTable.put("I", 7);
		valencesTable.put("Cs", 1);
		valencesTable.put("Ba", 2);
		valencesTable.put("Tl", 3);
		valencesTable.put("Pb", 4);
		valencesTable.put("Bi", 5);
		valencesTable.put("Po", 6);
		valencesTable.put("At", 7);
		valencesTable.put("Fr", 1);
		valencesTable.put("Ra", 2);
		valencesTable.put("Cu", 2);
		valencesTable.put("Mn", 2);
		valencesTable.put("Co", 2);

		// do all ring perception
		AllRingsFinder arf = new AllRingsFinder();
		IRingSet allRings;
		try {
			allRings = arf.findAllRings(atomContainer);
		} catch (CDKException e) {
			// logger.debug(e.toString());
			throw new CDKException(e.toString(), e);
		}

		// sets SSSR information
		SSSRFinder finder = new SSSRFinder(atomContainer);
		IRingSet sssr = finder.findEssentialRings();

		for (IAtom atom : atomContainer.atoms()) {

			// add a property to each ring atom that will be an array of
			// Integers, indicating what size ring the given atom belongs to
			// Add SSSR ring counts
			if (allRings.contains(atom)) { // it's in a ring
				atom.setFlag(CDKConstants.ISINRING, true);
				// lets find which ring sets it is a part of
				List<Integer> ringsizes = new ArrayList<Integer>();
				IRingSet currentRings = allRings.getRings(atom);
				int min = 0;
				for (int i = 0; i < currentRings.getAtomContainerCount(); i++) {
					int size = currentRings.getAtomContainer(i).getAtomCount();
					if (min > size)
						min = size;
					ringsizes.add(size);
				}
				atom.setProperty(CDKConstants.RING_SIZES, ringsizes);
				atom.setProperty(CDKConstants.SMALLEST_RINGS,
						sssr.getRings(atom));
			} else {
				atom.setFlag(CDKConstants.ISINRING, false);
			}

			// determine how many rings bonds each atom is a part of
			int hCount;
			if (atom.getImplicitHydrogenCount() == CDKConstants.UNSET)
				hCount = 0;
			else
				hCount = atom.getImplicitHydrogenCount();

			List<IAtom> connectedAtoms = atomContainer
					.getConnectedAtomsList(atom);
			int total = hCount + connectedAtoms.size();
			for (IAtom connectedAtom : connectedAtoms) {
				if (connectedAtom.getSymbol().equals("H")) {
					hCount++;
				}
			}
			atom.setProperty(CDKConstants.TOTAL_CONNECTIONS, total);
			atom.setProperty(CDKConstants.TOTAL_H_COUNT, hCount);

			if (valencesTable.get(atom.getSymbol()) != null) {
				int formalCharge = atom.getFormalCharge() == CDKConstants.UNSET ? 0
						: atom.getFormalCharge();
				atom.setValency(valencesTable.get(atom.getSymbol())
						- formalCharge);
			}
		}

		for (IBond bond : atomContainer.bonds()) {
			if (allRings.getRings(bond).getAtomContainerCount() > 0) {
				bond.setFlag(CDKConstants.ISINRING, true);
			}
		}

		for (IAtom atom : atomContainer.atoms()) {
			List<IAtom> connectedAtoms = atomContainer
					.getConnectedAtomsList(atom);

			int counter = 0;
			IAtom any;
			for (IAtom connectedAtom : connectedAtoms) {
				any = connectedAtom;
				if (any.getFlag(CDKConstants.ISINRING)) {
					counter++;
				}
			}
			atom.setProperty(CDKConstants.RING_CONNECTIONS, counter);
		}

		// check for atomaticity
		try {
			AtomContainerManipulator
					.percieveAtomTypesAndConfigureAtoms(atomContainer);
			CDKHueckelAromaticityDetector.detectAromaticity(atomContainer);
		} catch (CDKException e) {
			// logger.debug(e.toString());
			throw new CDKException(e.toString(), e);
		}
	}

	// -----------------------------------------------------

	int sss_SingleDBStrStat(String line) {
		// Performs statistics for each structure from the DB
		// It is applied for several algorithms simultaneously

		long startTime, endTime;
		long timeAmbit = 0;
		long timeCDK = 0;
		long timeAmbitCDK = 0;
		long timeCDKAmbit = 0;
		// long timeAmbitSMSD = 0;
		// long timeCDKSMSD = 0;

		// Ambit parser
		IQueryAtomContainer query_ambit = spAmbit.parse(line);
		spAmbit.setNeededDataFlags();
		String errorMsg = spAmbit.getErrorMessages();
		if (!errorMsg.equals("")) {
			System.out.println("Smarts Parser errors:\n" + errorMsg);
			return -1;
		}

		// CDK parser
		QueryAtomContainer query_CDK = null;
		try {
			query_CDK = SMARTSParser.parse(line);
		} catch (Exception e) {
			System.out.println("CDK parsing error: " + e.toString());
			return -1;
		}

		// Initial line in the output
		output("###test  Ambit  CDK  AmbitCDK  CDKAmbit  AmbitSMSD CDKSMSD "
				+ endLine);

		int sssResult = 0;
		boolean bondSensitive = true;
		boolean removeHydrogen = true;
		// Isomorphism comparisonSMSD = new Isomorphism(Algorithm.SubStructure,
		// bondSensitive);

		output("###  " + query_ambit.getAtomCount() + "   " + line + endLine);

		try {
			IChemObjectBuilder b = SilentChemObjectBuilder.getInstance();
			MyIteratingMDLReader reader = new MyIteratingMDLReader(
					new FileReader(dbFileName), b);
			int record = 0;

			while (reader.hasNext()) {
				record++;
				if (record > this.nDBStr)
					break;

				if (record % 100 == 0)
					System.out.println("db-rec " + record);

				Object o = reader.next();
				if (o instanceof IAtomContainer) {
					IAtomContainer mol = (IAtomContainer) o;
					if (mol.getAtomCount() == 0)
						continue;
					AtomContainerManipulator
							.percieveAtomTypesAndConfigureAtoms(mol);
					CDKHueckelAromaticityDetector.detectAromaticity(mol);

					if (FlagStat_SingleDBStr_Ambit) {
						isoTester.setQuery(query_ambit);

						if (FlagGarbCollector)
							callGarbCollector();
						startTime = System.nanoTime();
						spAmbit.setSMARTSData(mol);
						boolean hasIso = isoTester.hasIsomorphism(mol);
						endTime = System.nanoTime();
						timeAmbit = endTime - startTime;
						if (hasIso)
							sssResult = 1;
						else
							sssResult = 0;
						// System.out.println("record " + record+ "  " + hasIso
						// + "   " + startTime + "  " + endTime);
					}

					if (FlagStat_SingleDBStr_CDK) {
						if (FlagGarbCollector)
							callGarbCollector();
						startTime = System.nanoTime();
						boolean res = UniversalIsomorphismTester.isSubgraph(
								mol, query_CDK);
						endTime = System.nanoTime();
						timeCDK = endTime - startTime;
					}

					if (FlagStat_SingleDBStr_Ambit_CDK) {
						if (FlagGarbCollector)
							callGarbCollector();
						startTime = System.nanoTime();
						spAmbit.setSMARTSData(mol);
						boolean res = UniversalIsomorphismTester.isSubgraph(
								mol, query_ambit);
						endTime = System.nanoTime();
						timeAmbitCDK = endTime - startTime;
					}

					if (FlagStat_SingleDBStr_CDK_Ambit) {
						if (FlagGarbCollector)
							callGarbCollector();
						isoTester.setQuery(query_CDK);
						startTime = System.nanoTime();
						boolean hasIso = isoTester.hasIsomorphism(mol);
						endTime = System.nanoTime();
						timeCDKAmbit = endTime - startTime;
					}
					/*
					 * if (FlagStat_SingleDBStr_Ambit_SMSD) { try { if
					 * (FlagGarbCollector) callGarbCollector(); startTime =
					 * System.nanoTime(); spAmbit.setSMARTSData(mol);
					 * comparisonSMSD.init(query_ambit,mol,
					 * removeHydrogen,true);
					 * //comparisonSMSD.setChemFilters(false, false, false);
					 * //boolean res = comparisonSMSD.isSubgraph(); endTime =
					 * System.nanoTime(); timeAmbitSMSD = endTime - startTime;
					 * //System.out.println("record " + record+ "  " + res +
					 * "   " + startTime + "  " + endTime); } catch(Exception e)
					 * { System.out.println("SMSD error: " );
					 * //e.printStackTrace(); continue; } }
					 * 
					 * if (FlagStat_SingleDBStr_CDK_SMSD) { try { startTime =
					 * System.nanoTime(); comparisonSMSD.init(query_CDK,mol,
					 * removeHydrogen,true); endTime = System.nanoTime();
					 * timeCDKSMSD = endTime - startTime;
					 * //System.out.println("record " + record+ "  " + res +
					 * "   " + startTime + "  " + endTime); } catch(Exception e)
					 * { System.out.println("SMSD error: " ); continue; } }
					 */
					output("db-" + record + "  " + mol.getAtomCount() + "  "
							+ sssResult + "       " + timeAmbit + "  "
							+ timeCDK + "  " + timeAmbitCDK + "  "
							+ timeCDKAmbit + "  " + 
							//timeAmbitSMSD + "  "	+ timeCDKSMSD + 
							endLine);

					// System.out.println("record " + record+ "  " +
					// mol.getAtomCount());
				}
			}
		}

		catch (Exception e) {
			System.out.println(e.toString());
			return (-1);
		}

		return (0);

	}

	public void callGarbCollector() {
		System.gc();
		for (int i = 0; i < 100; i++) {
		}
	}

	int compareParsers(String line) {
		long timeAmbit, timeCDK;
		long startTime, endTime;
		boolean Flag_OK = true;

		// Ambit parser
		startTime = System.nanoTime();
		IQueryAtomContainer query_ambit = spAmbit.parse(line);
		spAmbit.setNeededDataFlags();
		endTime = System.nanoTime();
		timeAmbit = endTime - startTime;

		// CDK parser
		try {
			startTime = System.nanoTime();
			QueryAtomContainer query_CDK = SMARTSParser.parse(line);
			endTime = System.nanoTime();
			timeCDK = endTime - startTime;
		} catch (Exception e) {
			System.out.println("CDK parsing error: " + e.toString());
			Flag_OK = false;
			timeCDK = 0;
		}

		if (Flag_OK) {
			String out_line = line + "  " + timeAmbit + "  " + timeCDK;
			output(out_line + endLine);
			System.out.println(out_line);
		}

		return (0);
	}

	public int makeStatistics(String fname) {
		// maxStatLines = 5000;
		try {
			File file = new File(fname);
			RandomAccessFile f = new RandomAccessFile(file, "r");

			// First line
			String line = f.readLine();
			int res = processFirstLineStatistics(line);
			if (res != 0) {
				System.out.println("Error at the initializing line " + 1 + "  "
						+ line);
				return (-1);
			}

			processFile(f);

			/*
			 * //Setting for the second pass for (int k = 0; k <
			 * statMethods.length; k++) statAllObjs[k].firstPass = false;
			 * 
			 * for (int i = 0; i < binsStrSize.length ; i++) { for (int k = 0; k
			 * < statMethods.length; k++) statBins.get(i)[k].firstPass = false;
			 * }
			 * 
			 * //Second pass f.seek(0); line = f.readLine(); //first line
			 * processFile(f);
			 */

			f.close();

			// Print statistics
			output("StrSize" + "\t");
			output("nObj" + "\t");
			for (int k = 0; k < statMethods.length; k++)
				output(statMethods[k] + "\t"); // +"RSD%" + "\t");
			output(endLine);

			output("all" + "\t");
			output(statAllObjs[0].numObjects + "\t");
			for (int k = 0; k < statMethods.length; k++)
				output(statAllObjs[k].getAverage() + "\t"); // +
															// statAllObjs[k].getRSD()
															// + "\t");
			output(endLine);

			output("Map1" + "\t");
			output(statAllObjs_MapTrue[0].numObjects + "\t");
			for (int k = 0; k < statMethods.length; k++)
				output(statAllObjs_MapTrue[k].getAverage() + "\t"); // +
																	// statAllObjs_MapTrue[k].getRSD()
																	// + "\t");
			output(endLine);

			output("Map0" + "\t");
			output(statAllObjs_MapFalse[0].numObjects + "\t");
			for (int k = 0; k < statMethods.length; k++)
				output(statAllObjs_MapFalse[k].getAverage() + "\t"); // +
																		// statAllObjs_MapTrue[k].getRSD()
																		// +
																		// "\t");
			output(endLine);

			// This is for hose case when query.size() < target.size()
			output("Map0_" + "\t");
			output(statAllObjs_MapFalse_[0].numObjects + "\t");
			for (int k = 0; k < statMethods.length; k++)
				output(statAllObjs_MapFalse_[k].getAverage() + "\t"); // +
																		// statAllObjs_MapTrue_[k].getRSD()
																		// +
																		// "\t");
			output(endLine);

			for (int i = 0; i < binsStrSize.length; i++) {
				output("Q" + binsStrSize[i] + "\t");
				output(statBins.get(i)[0].numObjects + "\t");
				for (int k = 0; k < statMethods.length; k++)
					output(statBins.get(i)[k].getAverage() + "\t"); // +
																	// statBins.get(i)[k].getRSD()+"\t");
				output(endLine);
			}

			for (int i = 0; i < binsStrSize.length; i++) {
				output("DB" + binsStrSize[i] + "\t");
				output(statBins2.get(i)[0].numObjects + "\t");
				for (int k = 0; k < statMethods.length; k++)
					output(statBins2.get(i)[k].getAverage() + "\t"); // +
																		// statBins2.get(i)[k].getRSD()+"\t");
				output(endLine);
			}

		} catch (Exception e) {
			System.out.println("Statistic file error: " + e.toString());
			e.printStackTrace();
		}

		return (0);
	}

	public void processFile(RandomAccessFile f) throws Exception {
		String line;
		int curStatLine = 1;
		long length = f.length();

		while (f.getFilePointer() < length) {
			curStatLine++;

			if (curStatLine > maxStatLines)
				break;

			line = f.readLine();
			int res = processLineStatistics(line);

			if (res < 0) {
				// Errors >= 1 do not stop the statistics process
				System.out
						.println("Error at line " + curStatLine + "  " + line);
				break;
			}

			if ((curStatLine % 1000) == 0)
				System.out.println("line " + curStatLine);
		}
	}

	public int processFirstLineStatistics(String line) {
		// The statistics process is initialized by the first line data (methods
		// info/names)

		Vector<String> tokens = filterTokens(line.split(" "));

		int n = tokens.size() - 1;
		if (n <= 0)
			return -1;

		statMethods = new String[n];
		for (int k = 0; k < n; k++) {
			statMethods[k] = tokens.get(k + 1);
			System.out.println(statMethods[k]);
		}

		statAllObjs = new StatisticsData[n];
		for (int k = 0; k < n; k++)
			statAllObjs[k] = new StatisticsData();

		statAllObjs_MapTrue = new StatisticsData[n];
		for (int k = 0; k < n; k++)
			statAllObjs_MapTrue[k] = new StatisticsData();

		statAllObjs_MapFalse = new StatisticsData[n];
		for (int k = 0; k < n; k++)
			statAllObjs_MapFalse[k] = new StatisticsData();

		statAllObjs_MapFalse_ = new StatisticsData[n];
		for (int k = 0; k < n; k++)
			statAllObjs_MapFalse_[k] = new StatisticsData();

		for (int i = 1; i <= binsStrSize.length + 1; i++) {
			StatisticsData bStat[] = new StatisticsData[n];
			for (int k = 0; k < n; k++)
				bStat[k] = new StatisticsData();

			statBins.add(bStat);

			bStat = new StatisticsData[n];
			for (int k = 0; k < n; k++)
				bStat[k] = new StatisticsData();

			statBins2.add(bStat);
		}

		return (0);
	}

	public int processLineStatistics(String line) {
		// System.out.println("Processing line: " + line);

		Vector<String> tokens = filterTokens(line.split(" "));

		try {
			if (line.startsWith("###")) {
				if (!tokens.get(0).equals("###")) // This is needed since inside
													// the test file initilizin
													// line is repeted
					return (0);

				statCurSrtSize = Integer.parseInt(tokens.get(1));
				statCurBinIndex = getBinIndex(statCurSrtSize);
			} else {
				if (tokens.size() != (3 + statMethods.length)) {
					System.out
							.println("Wrong number of tokens at line " + line);
					return (1);
				}

				int dbStrSize = Integer.parseInt(tokens.get(1));
				int sssRes = Integer.parseInt(tokens.get(2));
				int dbBin = getBinIndex(dbStrSize);

				if (FlagTargetLargerThanQuery) {
					if (statCurSrtSize >= dbStrSize)
						return (0);
				}

				// Handling the time for each method
				for (int i = 0; i < statMethods.length; i++) {
					long methodTime = Long.parseLong(tokens.get(3 + i));
					statAllObjs[i].addValue(methodTime);
					statBins.get(statCurBinIndex)[i].addValue(methodTime);
					statBins2.get(dbBin)[i].addValue(methodTime);
					if (sssRes == 0) {
						statAllObjs_MapFalse[i].addValue(methodTime);
						if (statCurSrtSize < dbStrSize)
							statAllObjs_MapFalse_[i].addValue(methodTime);
					} else
						statAllObjs_MapTrue[i].addValue(methodTime);
				}
			}
		} catch (Exception e) {
			System.out.println("Error at line " + line);
			// e.printStackTrace();
			return (2);
		}

		return (0);
	}

	public Vector<String> filterTokens(String tokens[]) {
		Vector<String> v = new Vector<String>();
		for (int i = 0; i < tokens.length; i++)
			if (!tokens[i].equals(""))
				v.add(tokens[i]);
		return v;
	}

	public int getBinIndex(int value) {
		int binIndex = binsStrSize.length;
		for (int i = 0; i < binsStrSize.length; i++) {
			if (value <= binsStrSize[i]) {
				binIndex = i;
				break;
			}
		}

		return binIndex;
	}

	public int processLineForSizeStatistics(String line) {
		IQueryAtomContainer query_ambit = spAmbit.parse(line);
		output(line + "  " + query_ambit.getAtomCount() + endLine);
		System.out.println(line + "  " + query_ambit.getAtomCount());
		return (0);
	}

	public int calcBitVectorStrings() {
		// Input file is interpreted as str. keys set
		// for each DB structure BitVector is calculated and saved to the output
		// file

		// Loading the structure keys
		Vector<String> externKeySet = new Vector<String>();
		try {
			File file = new File(inFileName);
			RandomAccessFile f = new RandomAccessFile(file, "r");

			String line;
			long length = f.length();

			while (f.getFilePointer() < length) {
				line = f.readLine();
				String smiles = line.trim();
				if (!smiles.equals(""))
					externKeySet.add(smiles);
			}
			f.close();

		} catch (Exception e) {
			System.out.println("Probleb with the input file:");
			e.printStackTrace();
			return (-1);
		}

		Screening screen = new Screening(externKeySet);

		if (dbFileName.endsWith(".txt") || dbFileName.endsWith(".smi")) {
			try {
				File file = new File(dbFileName);
				RandomAccessFile f = new RandomAccessFile(file, "r");

				String line;
				long length = f.length();

				int record = 0;

				while (f.getFilePointer() < length) {
					line = f.readLine();
					String smiles = line.trim();
					IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);

					BitSet bs = screen.getStructureKeyBits(mol);
					String s = screen.getBitSetString(bs);
					output(s + endLine);

					record++;
					if (record % 100 == 0)
						System.out.println("rec " + record);

				}
				f.close();

			} catch (Exception e) {
				System.out.println("Probleb with the SMILES DB file :");
				e.printStackTrace();
				return (-1);
			}

			return (0);
		}

		try {
			IChemObjectBuilder b = SilentChemObjectBuilder.getInstance();
			MyIteratingMDLReader reader = new MyIteratingMDLReader(
					new FileReader(dbFileName), b);
			int record = 0;
			int readComp = 0;

			while (reader.hasNext()) {
				record++;
				if (record > this.nDBStr)
					break;

				if ((record % 100) == 0)
					System.out.println("rec " + record + "   (" + readComp
							+ ")");

				Object o = reader.next();
				if (o instanceof IAtomContainer) {
					readComp++;

					IAtomContainer mol = (IAtomContainer) o;
					if (mol.getAtomCount() == 0)
						continue;
					AtomContainerManipulator
							.percieveAtomTypesAndConfigureAtoms(mol);
					CDKHueckelAromaticityDetector.detectAromaticity(mol);

					BitSet bs = screen.getStructureKeyBits(mol);
					String s = screen.getBitSetString(bs);
					output(s + endLine);

					// Test
					// BitSet bs2 = screen.stringToBitSet(s);
					// String s2 = screen.getBitSetString(bs2);
					// System.out.println(s + "   " + s2);

				}
			}
		} catch (Exception e) {
			System.out.println("Problem with the DB file:");
			e.printStackTrace();
			return (-1);
		}

		return (0);
	}

	public int calcScreeningRatio() throws Exception {
		// Input file interpretation
		// -i queries-bits
		// -i2 db-bits

		Screening screen = new Screening(SilentChemObjectBuilder.getInstance());
		Vector<BitSet> db = loadDbBitSets(screen);

		if (db == null)
			return (-1);

		try {
			File file = new File(inFileName);
			RandomAccessFile f = new RandomAccessFile(file, "r");

			String line;
			long length = f.length();

			int lineNum = 0;
			while (f.getFilePointer() < length) {
				line = f.readLine();
				lineNum++;
				if (lineNum % 100 == 0)
					System.out.println("query-bits " + lineNum);

				BitSet queryBitSet = screen.stringToBitSet(line);
				int res = getRejectedDBStructs(queryBitSet, db, screen);
				output("#" + lineNum + "    " + res + endLine);

				// File based version
				// int res = countScreenResultFromFile(line, screen);
				// if (res > -1)
				// output("#"+lineNum + "    " + res + endLine);
			}
			f.close();

		} catch (Exception e) {
			System.out.println("Probleb with the query bits (-i input file):");
			e.printStackTrace();
			return (-1);
		}

		return (0);
	}

	public int countScreenResultFromFile(String queryBitSetString,
			Screening screen) {

		try {
			File file = new File(inFileName2);
			RandomAccessFile f = new RandomAccessFile(file, "r");

			String line;
			long length = f.length();

			int rejectNum = 0;
			int lineNum = 0;
			while (f.getFilePointer() < length) {
				line = f.readLine();
				lineNum++;

				BitSet queryBitSet = screen.stringToBitSet(queryBitSetString);
				BitSet dbBitSet = screen.stringToBitSet(line);
				boolean res = screen.bitSetCheck(queryBitSet, dbBitSet);
				if (res == false)
					rejectNum++;

			}

			f.close();
			return (rejectNum);

		} catch (Exception e) {
			System.out.println("Probleb with the db-bits (-i2 input file):");
			e.printStackTrace();
			return (-1);
		}

		// return 0;
	}

	public Vector<BitSet> loadDbBitSets(Screening screen) {
		Vector<BitSet> v = new Vector<BitSet>();

		try {
			File file = new File(inFileName2);
			RandomAccessFile f = new RandomAccessFile(file, "r");

			String line;
			long length = f.length();

			int rejectNum = 0;
			int lineNum = 0;
			while (f.getFilePointer() < length) {
				line = f.readLine();
				lineNum++;
				BitSet dbBitSet = screen.stringToBitSet(line);
				v.add(dbBitSet);
			}

			f.close();
			return (v);

		} catch (Exception e) {
			System.out.println("Probleb with the db-bits (-i2 input file):");
			e.printStackTrace();
			return (null);
		}

		// return 0;
	}

	int getRejectedDBStructs(BitSet queryBits, Vector<BitSet> db,
			Screening screen) {
		int rejectNum = 0;

		if (nBits > 0) {
			for (int i = 0; i < db.size(); i++) {
				boolean res = screen.bitSetCheck(queryBits, db.get(i), nBits);
				if (res == false)
					rejectNum++;
			}
		} else {
			for (int i = 0; i < db.size(); i++) {
				boolean res = screen.bitSetCheck(queryBits, db.get(i));
				if (res == false)
					rejectNum++;
			}
		}

		return (rejectNum);
	}

}
