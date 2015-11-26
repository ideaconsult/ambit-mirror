package ambit2.dbcli.descriptors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.json.JSONUtils;
import ambit2.core.io.FileInputState;
import ambit2.descriptors.AtomEnvironentMatrix;

public class AtomEnvironmentGeneratorApp {
	static Logger logger ;
	protected CDKHydrogenAdder hAdder = CDKHydrogenAdder
			.getInstance(SilentChemObjectBuilder.getInstance());;
	protected boolean hydrogens = false;
	protected Aromaticity aromaticity = new Aromaticity(ElectronDonation.cdk(),
			Cycles.cdkAromaticSet());
	
	public AtomEnvironmentGeneratorApp(Logger logger) {
		if (logger == null) this.logger = Logger.getLogger("ambitcli", "ambit2.dbcli.msg");
		else this.logger = logger;
	}

	protected Map<String, String> lookup(String id_tag, String activityTag,
			String mergeResultsFile) {

		if (mergeResultsFile == null)
			return null;
		if (activityTag == null)
			return null;
		if (id_tag == null)
			return null;

		File file = new File(mergeResultsFile);
		if (!file.exists())
			return null;

		Map<String, String> map = new HashMap<String, String>();
		IIteratingChemObjectReader reader = null;
		try {
			reader = FileInputState.getReader(new FileInputStream(
					mergeResultsFile), mergeResultsFile);
			while (reader.hasNext()) {
				IAtomContainer mol = (IAtomContainer) reader.next();
				String id = mol.getProperty(id_tag);
				Object activity = mol.getProperty(activityTag);
				if ((id != null) && (activity != null)) {
					map.put(id, activity.toString());
				}
			}
		} catch (Exception x) {
			logger.severe(x.getMessage());
		} finally {
			try {
				reader.close();
			} catch (Exception x) {
			}
		}
		return map;
	}

	private void mm_write(FileWriter mmwriter, String value) throws Exception {
		if (mmwriter == null)
			return;
		mmwriter.write(value);
	}

	public long runAtomTypeMatrixDescriptor(String root, File file,
			String id_tag, String activityTag, String mergeResultsFile,
			boolean noCSV, boolean noMM, boolean noJSON, boolean normalize,
			Double laplace_smoothing, boolean costsensitive,
			boolean levels_as_namespace) throws Exception {

		Map<String, String> lookup = lookup(id_tag, activityTag,
				mergeResultsFile);

		AtomEnvironentMatrix gen = new AtomEnvironentMatrix(CDKAtomTypeMatcher.getInstance(SilentChemObjectBuilder.getInstance()),"org/openscience/cdk/dict/data/cdk-atom-types.owl",7);
		InputStream in = new FileInputStream(file);

		IIteratingChemObjectReader<IAtomContainer> reader = new IteratingSDFReader(
				new InputStreamReader(in),
				SilentChemObjectBuilder.getInstance());

		File mmfile = new File(root, file.getName().replace(".sdf", ".mm.tmp"));
		FileWriter mmwriter = null;
		if (!noMM) {
			/**
			 * Matrix market format
			 */
			mmwriter = new FileWriter(mmfile);
		}

		File vwfile = new File(root, file.getName().replace(".sdf", ".vw"));
		FileWriter vwwriter = new FileWriter(vwfile);

		FileWriter jsonwriter = null;
		if (!noJSON) {
			File jsonfile = new File(root, file.getName().replace(".sdf",
					".json"));
			jsonwriter = new FileWriter(jsonfile);
		}
		int mmrows = 0;
		int mmcols = 0;
		int mmentries = 0;

		Hashtable<String, FileWriter> writers = new Hashtable<String, FileWriter>();
		String[] sets = new String[] { "ALL" };
		for (String set : sets) {
			FileWriter writer = new FileWriter(new File(root, file.getName()
					.replace(".sdf", "") + set + "_AEMATRIX.csv"));
			writers.put(set, writer);
		}
		int row = 0;
		boolean header = false;
		while (reader.hasNext()) {
			row++;
			IAtomContainer mol = reader.next();
			Object id = id_tag == null ? row : mol.getProperty(id_tag);

			String set = mol.getProperty("Set") == null ? "" : mol.getProperty(
					"Set").toString();
			Object activityValue = mol.getProperty(activityTag) == null ? ""
					: mol.getProperty(activityTag);

			if (lookup != null && (id_tag != null)) {
				activityValue = lookup.get(id);
			}

			FileWriter csvWriter = null;
			if (!noCSV)
				csvWriter = writers.get("ALL");

			if (row % 100 == 1) {
				logger.log(Level.INFO,"MSG_INFO_PROCESSED", row);
			}

			try {
				AtomContainerManipulator
						.percieveAtomTypesAndConfigureAtoms(mol);
			} catch (Exception x) {
				printError(row, id_tag, id, x);
			}

			try {
				hAdder.addImplicitHydrogens(mol);
			} catch (Exception x) {
				printError(row, id_tag, id, x);
			}

			try {
				if (hydrogens) {
					AtomContainerManipulator
							.convertImplicitToExplicitHydrogens(mol);

				} else {
					if (mol.getBondCount() > 1) {
						for (IAtom atom : mol.atoms())
							if (atom.getImplicitHydrogenCount() == null) {
								if ("H".equals(atom.getSymbol()))
									atom.setImplicitHydrogenCount(0);
								else
									logger.fine(atom.toString());
							}
						mol = AtomContainerManipulator.suppressHydrogens(mol);
					}

				}
			} catch (Exception x) {
				printError(row, id_tag, id, x);
			}

			// if (useHydrogens) { //always, otherwise atom types are not
			// recognised correctly
			// for some reason H atoms are added as bond references, but not in
			// atom list - bug?

			try {
				aromaticity.apply(mol);
			} catch (Exception x) {
				printError(row, id_tag, id, x);
			}
			Map<String,Integer> values = gen.doCalculation(mol).getResults();
			if (!header) {
				mm_write(csvWriter, id_tag == null ? "Row" : id_tag);
				mm_write(csvWriter, ",");
				for (int i = 0; i < gen.getDescriptorNames().length; i++) {
					// attr.write(value.getNames()[i]);
					// attr.write('\n');
					mm_write(csvWriter, "\"");
					mm_write(csvWriter, gen.getDescriptorNames()[i]);
					mm_write(csvWriter, "\",");
				}
				mm_write(csvWriter, "Activity,Set\n");
				header = true;
				mmcols = gen.getDescriptorNames().length;
				// attr.close();
				mm_write(jsonwriter, "[");
			} else {
				mm_write(jsonwriter, ",");
			}
			mm_write(jsonwriter, "\n{");
			// row
			if (id == null) {
				if (!noCSV)
					mm_write(csvWriter, String.format("%d", row));
				mm_write(jsonwriter, String.format("\"id\":%d,", row));
			} else {
				mm_write(jsonwriter, String.format("\"id\":%s,", JSONUtils
						.jsonQuote(JSONUtils.jsonEscape(id.toString()))));
				if (!noCSV)
					mm_write(csvWriter, id.toString());
			}
			if (!noCSV) {
				mm_write(csvWriter, ",");
				for (int i = 0; i < gen.getDescriptorNames().length; i++) {
					int count = 0;
					if (values.containsKey(gen.getDescriptorNames()[i]))
						count = values.get(gen.getDescriptorNames()[i]);
					mm_write(csvWriter, Integer.toString(count));
					mm_write(csvWriter, ",");
				}
			}
			double importance = 1.0;
			String sActivity = null;
			if (activityValue == null) {
				mm_write(csvWriter, "Unknown");
				vwwriter.write(" ");
			} else
				try {
					int activity = Integer.parseInt(activityValue
							.toString());
					sActivity = activity == 1.0 ? "Positive"
							: (activity == 0) ? "Negative" : activityValue
									.toString();
					mm_write(csvWriter, sActivity);
					mm_write(
							jsonwriter,
							String.format("\"activity\":%s,", JSONUtils
									.jsonQuote(JSONUtils.jsonEscape(sActivity))));
					if (jsonwriter != null)
						jsonwriter.flush();
					//int iActivity = activity == 1.0 ? 3 : ((activity == 0) ? 1		: 0);
					importance = activity == 1.0 ? 3
							: ((activity == 0) ? 1 : 0);
					// int iActivity = activity == 1.0 ? 1 : ((activity == 0) ?
					// -1 : 0);
					String vwActivity;
					if (costsensitive)
						vwActivity = activity == 1.0 ? "1:5.0 2:0.5 3:0"
								: ((activity == 0) ? "1:0.0 2:5.0 3:5.0"
										: "1 2 3");
					else
						vwActivity = activity == 1.0 ? "1"
								: ((activity == 0) ? "2" : "3");
					;
					// vwwriter.write(Integer.toString(iActivity)); // label
					vwwriter.write(vwActivity); // label

				} catch (Exception x) {
					mm_write(csvWriter, activityValue.toString());
					// vwwriter.write("A".equals(activityValue)?"1":("B".equals(activityValue)?"1":("C".equals(activityValue)?"-1":activityValue.toString())));
					if (costsensitive)
						vwwriter.write("A".equals(activityValue) ? "1:5.0 2:0.5 3:0.0"
								: ("B".equals(activityValue) ? "1:5.0 1:0.0 3:0.5"
										: ("C".equals(activityValue) ? "1:0 2:5.0 3:5.0"
												: activityValue.toString())));
					else
						vwwriter.write("A".equals(activityValue) ? "1" : ("B"
								.equals(activityValue) ? "3" : ("C"
								.equals(activityValue) ? "2" : activityValue
								.toString())));
				}
			if (!noCSV) {
				mm_write(csvWriter, ",");
				mm_write(csvWriter, set);
				mm_write(csvWriter, "\n");
				if (csvWriter != null)
					csvWriter.flush();
			}
			/*
			 * vwwriter.write(String.format(" %f",importance));
			 */
			vwwriter.write(" '");

			if (id == null)
				vwwriter.write(row);
			else
				vwwriter.write(id.toString());

			mmrows++;
			String comma = "";
			for (int i = 0; i < gen.getDescriptorNames().length; i++) {

				int count = 0;
				if (values.containsKey(gen.getDescriptorNames()[i]))
					count = values.get(gen.getDescriptorNames()[i]);
				if (count <= 0)
					continue;
				mmentries++;
				mm_write(mmwriter,
						String.format("%d\t%d\t%d\n", mmrows, (i + 1), count));
				mm_write(jsonwriter, comma);
				mm_write(jsonwriter,
						String.format("\"%s\":%d", gen.getDescriptorNames()[i], count));
				comma = ",";

			}

			double[] sum = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			int[] n = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

			if (!levels_as_namespace)
				vwwriter.write(" |AE");

			
			for (int l = 0; l < 9; l++) {
				StringBuilder aebuilder = new StringBuilder();
				String ns = String.format("L%d_", l);

				if (levels_as_namespace) {
					vwwriter.write(" |");
					vwwriter.write(String.format("%dL_	",l));
				}

				for (int i = 0; i < gen.getDescriptorNames().length; i++)
					if (gen.getDescriptorNames()[i].startsWith(ns)) {
						if (skip(gen.getDescriptorNames()[i]))
							continue;
						n[l]++;
						if (values.containsKey(gen.getDescriptorNames()[i]))
							sum[l] += values.get(gen.getDescriptorNames()[i]);
					}

				for (int i = 0; i < gen.getDescriptorNames().length; i++)
					if (gen.getDescriptorNames()[i].startsWith(ns)) {
						if (skip(gen.getDescriptorNames()[i]))
							continue;

						int count = 0;
						if (values.containsKey(gen.getDescriptorNames()[i])) {
							count = values.get(gen.getDescriptorNames()[i]);
							String tag = gen.getDescriptorNames()[i];

							double v;
							if (laplace_smoothing != null) {
								if (sum[l] <= 0)
									continue;
								v = (((double) count) + laplace_smoothing)
										/ (sum[l] + laplace_smoothing * n[l]);
								aebuilder.append(String
										.format(" %s:%e", tag, v));
							} else {

								if (count <= 0)
									continue;

								if (normalize) {
									aebuilder.append(String.format(" %s:%f",
											tag, ((double) count) / sum[l]));

								} else
									aebuilder.append(String.format(" %s:%d",
											tag, count));
							}
						}

					}
				vwwriter.write(aebuilder.toString());
			}

			mm_write(jsonwriter, "}");

			vwwriter.write("\n");
			vwwriter.flush();
		}
		reader.close();
		for (String set : sets) {
			writers.get(set).close();
		}
		try {
			mm_write(jsonwriter, "\n]");
			if (jsonwriter != null)
				jsonwriter.close();
		} catch (Exception x) {
			logger.severe(x.getMessage());
		}
		if (mmwriter != null) {
			mmwriter.close();
			mmwriter = new FileWriter(new File(root, file.getName().replace(
					".sdf", ".mm")));
			mm_write(mmwriter,
					"%%MatrixMarket matrix coordinate real general\n");
			mm_write(mmwriter,
					String.format("%d\t%d\t%d\n", mmrows, mmcols, mmentries));
			BufferedReader bin = null;
			try {
				String line;
				bin = new BufferedReader(new FileReader(mmfile));
				while ((line = bin.readLine()) != null) {
					mm_write(mmwriter, line);
					mm_write(mmwriter, "\n");
				}

			} catch (Exception x) {
				logger.severe(x.getMessage());
			} finally {
				bin.close();
				mmwriter.close();
			}
		}
		if (vwwriter != null)
			vwwriter.close();

		return row;

	}

	protected boolean skip(String key) {
		if (key.indexOf("Any") >= 0)
			return true;
		if (key.indexOf("Het") >= 0)
			return true;
		if (key.indexOf("Hev") >= 0)
			return true;
		if (key.indexOf("Hal") >= 0)
			return true;
		if (key.indexOf("_H") >= 0)
			return true;
		return false;
	}

	private void printError(int row, String id_tag, Object id, Exception x) {
		logger.severe(String.format("\nError at row %d\t%s = %s\t%s", row,
				id_tag == null ? "ROW=" : id_tag, id, x.getMessage()));
	}

	public static void main(String[] args) {

		AtomEnvironmentGeneratorApp test = new AtomEnvironmentGeneratorApp(null);
		String root = args.length > 0 ? args[0] : null;
		String file = args.length > 1 ? args[1] : null;
		String id_tag = args.length > 2 ? args[2] : null;
		String activityTag = args.length > 3 ? args[3] : "Activity";
		String mergeResultsFile = args.length > 4 ? args[4] : null;
		boolean noCSV = false;
		try {
			noCSV = Boolean.parseBoolean(args[5]);
		} catch (Exception x) {
		}
		try {
			if (root == null)
				throw new Exception(
						"Folder not specified.\nUsage: AtomEnvironmentGeneratorTest rootfolder file.sdf idtag activitytag mergeresultsfile");
			if (file == null)
				throw new Exception(
						"SDF file not specified.\nUsage: AtomEnvironmentGeneratorTest rootfolder file.sdf idtag activitytag mergeresultsfile");
			test.runAtomTypeMatrixDescriptor(root, new File(root, file),
					id_tag, activityTag, mergeResultsFile, noCSV, false, false,
					true, null, true, false);
		} catch (Exception x) {
			logger.severe(x.getMessage());
		}
	}
}
