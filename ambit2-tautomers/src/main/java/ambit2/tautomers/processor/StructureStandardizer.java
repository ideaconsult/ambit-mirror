package ambit2.tautomers.processor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Vector2d;

import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.sf.jniinchi.INCHI_OPTION;

import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.stereo.StereoElementFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.Property;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.processors.FragmentProcessor;
import ambit2.core.processors.IsotopesProcessor;
import ambit2.core.processors.structure.StructureTypeProcessor;
import ambit2.smarts.processors.NeutraliseProcessor;
import ambit2.tautomers.TautomerConst;

public class StructureStandardizer extends
		DefaultAmbitProcessor<IAtomContainer, IAtomContainer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2600599340740351460L;
	protected Map<Object, Property> tags = new HashMap<>();
	protected boolean generateInChI = true;
	protected boolean generateSMILES_Canonical = false;
	protected boolean generateSMILES_Aromatic = false;
	protected boolean splitFragments = false;
	protected boolean generateTautomers = false;
	protected boolean generateSMILES = true; // not canonical
	protected boolean neutralise = false;
	protected boolean generate2D = false;
	protected boolean implicitHydrogens = false;
	protected boolean generateStereofrom2D = false;
	protected boolean clearIsotopes = false;

	public boolean isGenerateSMILES_Aromatic() {
		return generateSMILES_Aromatic;
	}

	public void setGenerateSMILES_Aromatic(boolean generateSMILES_Aromatic) {
		this.generateSMILES_Aromatic = generateSMILES_Aromatic;
	}

	public boolean isGenerate2D() {
		return generate2D;
	}

	public void setGenerate2D(boolean generate2d) {
		generate2D = generate2d;
	}

	public boolean isNeutralise() {
		return neutralise;
	}

	public void setNeutralise(boolean neutralise) {
		this.neutralise = neutralise;
	}

	public boolean isGenerateStereofrom2D() {
		return generateStereofrom2D;
	}

	public void setGenerateStereofrom2D(boolean generateStereofrom2D) {
		this.generateStereofrom2D = generateStereofrom2D;
	}

	public boolean isClearIsotopes() {
		return clearIsotopes;
	}

	public void setClearIsotopes(boolean clearIsotopes) {
		this.clearIsotopes = clearIsotopes;
	}

	protected List<net.sf.jniinchi.INCHI_OPTION> options = new ArrayList<net.sf.jniinchi.INCHI_OPTION>();

	public boolean isGenerateSMILES_Canonical() {
		return generateSMILES_Canonical;
	}

	public void setGenerateSMILES_Canonical(boolean generateSMILES_Canonical) {
		this.generateSMILES_Canonical = generateSMILES_Canonical;
	}

	protected SmilesGenerator getSmilesGenerator() {
		if (smiles_generator == null) {
			if (generateSMILES_Canonical)
				smiles_generator = SmilesGenerator.absolute();
			else if (generateSMILES)
				smiles_generator = SmilesGenerator.isomeric();
			if (generateSMILES_Aromatic) {
				if (smiles_generator == null)
					smiles_generator = SmilesGenerator.isomeric();
				smiles_generator = smiles_generator.aromatic();
			}
		}
		return smiles_generator;
	}

	public boolean isGenerateSMILES() {
		return generateSMILES;
	}

	public void setGenerateSMILES(boolean generateSMILES) {
		this.generateSMILES = generateSMILES;
	}

	public boolean isGenerateInChI() {
		return generateInChI;
	}

	public void setGenerateInChI(boolean generateInChI) {
		this.generateInChI = generateInChI;
	}

	public boolean isSplitFragments() {
		return splitFragments;
	}

	public void setSplitFragments(boolean splitFragments) {
		this.splitFragments = splitFragments;
	}

	public boolean isGenerateTautomers() {
		return generateTautomers;
	}

	public void setGenerateTautomers(boolean generateTautomers) {
		this.generateTautomers = generateTautomers;
	}

	public boolean isImplicitHydrogens() {
		return implicitHydrogens;
	}

	public void setImplicitHydrogens(boolean implicitHydrogens) {
		this.implicitHydrogens = implicitHydrogens;
	}

	protected transient TautomerProcessor tautomers;
	protected transient FragmentProcessor fragments;
	protected transient NeutraliseProcessor neutraliser = null;
	protected transient IsotopesProcessor isotopesProcessor = null;
	protected transient StructureDiagramGenerator sdg = new StructureDiagramGenerator();

	protected transient CDKHydrogenAdder hadder = CDKHydrogenAdder
			.getInstance(SilentChemObjectBuilder.getInstance());
	protected transient InChIGeneratorFactory igf = null;
	protected transient SmilesGenerator smiles_generator;

	public StructureStandardizer() {
		this(null);
	}

	public StructureStandardizer(Logger logger) {
		super();
		options.add(INCHI_OPTION.SAbs);
		options.add(INCHI_OPTION.SAsXYZ);
		options.add(INCHI_OPTION.SPXYZ);
		options.add(INCHI_OPTION.FixSp3Bug);
		options.add(INCHI_OPTION.AuxNone);
		if (logger != null)
			this.logger = logger;

		fragments = new FragmentProcessor(logger);
		tautomers = new TautomerProcessor(logger);
	}

	public IProcessor<IAtomContainer, IAtomContainer> getCallback() {
		return tautomers.getCallback();
	}

	public void setCallback(IProcessor<IAtomContainer, IAtomContainer> callback) {
		this.tautomers.setCallback(callback);
	}

	public final static String ERROR_TAG = "ERROR";

	@Override
	public IAtomContainer process(IAtomContainer mol) throws Exception {
		IAtomContainer processed = mol;
		try {
			String err = processed.getProperty(ERROR_TAG);
			if (err == null)
				processed.setProperty(ERROR_TAG, "");
			if (neutralise) {
				if (neutraliser == null)
					neutraliser = new NeutraliseProcessor(logger);
				processed = neutraliser.process(processed);
				fragments.setAtomtypeasproperties(neutraliser
						.isAtomtypeasproperties());
				fragments.setSparseproperties(neutraliser.isSparseproperties());
			}
			if (splitFragments && (processed != null))
				processed = fragments.process(processed);

			if (processed != null) {
				if (generate2D
						&& (StructureTypeProcessor.has2DCoordinates(processed) <= 1)) {
					sdg.setMolecule(processed, false);
					sdg.generateCoordinates(new Vector2d(0, 1));
					processed = sdg.getMolecule();
				}
				if (clearIsotopes) {
					if (isotopesProcessor == null) {
						isotopesProcessor = new IsotopesProcessor(logger);

					}
					processed = isotopesProcessor.process(processed);
				}
				if (implicitHydrogens)
					try {
						processed = AtomContainerManipulator
								.suppressHydrogens(processed);
					} catch (Exception x) {
						if (processed != null) {
							err = processed.getProperty(ERROR_TAG);
							processed.setProperty(ERROR_TAG, String.format(
									"%s %s %s", err == null ? "" : err, x
											.getClass().getName(), x
											.getMessage()));
						}
					}

				int newse = 0;
				int oldse = 0;

				if (generateTautomers)
					try {
						// todo
						processed = tautomers.process(processed);
					} catch (Exception x) {
						StringWriter w = new StringWriter();
						x.printStackTrace(new PrintWriter(w));
						logger.log(Level.WARNING, "MSG_TAUTOMERGEN",
								new Object[] { x, w.toString() });
						err = processed.getProperty(ERROR_TAG);
						processed.setProperty(ERROR_TAG, String.format(
								"Tautomer %s %s %s", err == null ? "" : err, x
										.getClass().getName(), x.getMessage()));
					}
				if (generateStereofrom2D)
					try {
						StereoElementFactory stereo = StereoElementFactory
								.using2DCoordinates(processed);
						for (IStereoElement se : processed.stereoElements())
							oldse++;
						List<IStereoElement> stereoElements = stereo
								.createAll();
						for (IStereoElement se : stereoElements) {
							newse++;
						}
						if ((oldse > 0) && (newse > 0))
							processed.setProperty(stereo.getClass().getName(),
									String.format("StereoElements %s --> %s",
											oldse, newse));
						processed.setStereoElements(stereoElements);
					} catch (Exception x) {
						if (processed != null) {
							err = processed.getProperty(ERROR_TAG);
							processed.setProperty(ERROR_TAG, String.format(
									"%s %s %s", err == null ? "" : err, x
											.getClass().getName(), x
											.getMessage()));
						}
					}
				if (generateInChI) {
					if (processed.getProperty(Property.opentox_InChI) == null)
						try {
							if (igf == null)
								igf = InChIGeneratorFactory.getInstance();

							InChIGenerator gen = igf
									.getInChIGenerator(
											processed,
											generateTautomers ? tautomers.tautomerManager.tautomerFilter
													.getInchiOptions()
													: options);
							switch (gen.getReturnStatus()) {
							case OKAY: {
								processed.setProperty(Property.opentox_InChI,
										gen.getInchi());
								processed.setProperty(
										Property.opentox_InChIKey,
										gen.getInchiKey());
								break;
							}
							case WARNING: {
								processed.setProperty(Property.opentox_InChI,
										gen.getInchi());
								processed.setProperty(
										Property.opentox_InChIKey,
										gen.getInchiKey());
								logger.log(Level.FINE, "MSG_INCHIGEN",
										new Object[] { gen.getReturnStatus(),
												gen.getMessage() });
								break;
							}
							default: {
								processed.setProperty(Property.opentox_InChI,
										"");
								processed.setProperty(
										Property.opentox_InChIKey, "");
								err = processed.getProperty(ERROR_TAG);
								processed.setProperty(ERROR_TAG,
										String.format(
												"InChI Generation %s %s %s",
												err == null ? "" : err,
												gen.getReturnStatus(),
												gen.getMessage()));
								logger.log(Level.WARNING, "MSG_INCHIGEN",
										new Object[] { gen.getReturnStatus(),
												gen.getMessage() });
								break;
							}
							}
							// gen.getReturnStatus().

						} catch (Exception x) {
							StringWriter w = new StringWriter();
							x.printStackTrace(new PrintWriter(w));
							logger.log(Level.WARNING, "MSG_INCHIGEN",
									new Object[] { x, w.toString() });
							err = processed.getProperty(ERROR_TAG);
							processed.setProperty(ERROR_TAG, String.format(
									"InChI Generation %s %s %s",
									err == null ? "" : err, x.getClass()
											.getName(), x.getMessage()));
						}
				}

				if (getSmilesGenerator() != null) {
					Property p_smiles = Property.getSMILESInstance();
					try {
						processed.setProperty(p_smiles, getSmilesGenerator()
								.create(processed));
					} catch (Exception x) {
						StringWriter w = new StringWriter();
						x.printStackTrace(new PrintWriter(w));
						logger.log(
								Level.WARNING,
								"MSG_SMILESGEN",
								new Object[] {
										x,
										processed
												.getProperty(Property.opentox_InChIKey) });
						logger.log(Level.FINE, "MSG_SMILESGEN", new Object[] {
								x, w.toString() });
						String inchi_err = "";
						if (processed.getProperty(p_smiles) == null)
							try {
								Object inchi = processed
										.getProperty(Property.opentox_InChI);
								String last_resort_smiles = inchi == null ? ""
										: InChI2SMILES(inchi.toString());
								processed.setProperty(p_smiles,
										last_resort_smiles);
								inchi_err = "SMILES generated from InChI as fallback";
							} catch (Exception xx) {
								inchi_err = x.getMessage();
								processed.setProperty(p_smiles, "");
							}

						err = processed.getProperty(ERROR_TAG);
						String msg = String.format("%s %s %s %s",
								err == null ? "" : err, x.getClass().getName(),
								x.getMessage(), inchi_err);
						processed.setProperty(ERROR_TAG, msg);
						logger.log(Level.WARNING, msg);
					}
				}
				renameTags(processed, tags);
			} else {
				logger.log(Level.WARNING, "Null molecule after processing",
						mol.getProperties());
			}

		} catch (Exception x) {
			logger.log(Level.SEVERE, x.getMessage() + " "
					+ mol.getProperties().toString(), x);
			x.printStackTrace();
		} finally {
			// System.out.println(processed.getProperties());

			if (mol != null && processed != null) {
				Iterator<Entry<Object, Object>> p = mol.getProperties()
						.entrySet().iterator();
				// don't overwrite properties from the source
				// molecule
				while (p.hasNext()) {
					Entry<Object, Object> entry = p.next();
					Object value = processed.getProperty(entry.getKey());
					if (value == null || "".equals(value.toString().trim()))
						processed.setProperty(entry.getKey(), entry.getValue());
				}
			}

			// System.out.println(processed.getProperties());
		}
		return processed;

	}

	public static void renameTags(IAtomContainer processed,
			Map<Object, Property> tags) {
		renameTags(processed, tags, false);
	}

	public static void renameTags(IAtomContainer processed,
			Map<Object, Property> tags, boolean removeIfDisabled) {
		Iterator<Map.Entry<Object, Property>> i = tags.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry<Object, Property> entry = i.next();
			Object tag = entry.getKey();
			Object value = processed.getProperty(tag);
			if (value != null) {
				if (tag instanceof Property) {
					entry.getValue().setOrder(((Property) tag).getOrder());
				}
				processed.removeProperty(tag);
				boolean add = (entry.getValue() instanceof Property) ? ((Property) entry
						.getValue()).isEnabled() : true;

				if (removeIfDisabled && !add)
					;
				else
					processed.setProperty(entry.getValue(), value);
			}
		}
	}

	public void setInchiTag(String tag) {
		Property newtag = Property.getInChIInstance();
		newtag.setName(tag);
		tags.put(Property.opentox_InChI, newtag);
		tags.put(Property.getInChIInstance(), newtag);
	}

	public void setInchiKeyTag(String tag) {
		Property newtag = Property.getInChIKeyInstance();
		newtag.setName(tag);
		tags.put(Property.opentox_InChIKey, newtag);
		tags.put(Property.getInChIKeyInstance(), newtag);
	}

	public void setSMILESTag(String tag) {
		Property newtag = Property.getSMILESInstance();
		newtag.setName(tag);
		tags.put(Property.opentox_SMILES, newtag);
		tags.put(Property.getSMILESInstance(), newtag);
	}

	public void setRankTag(String tag) {
		Property newtag = Property.getInstance(tag,
				TautomerProcessor.class.getName());
		newtag.setName(tag);
		tags.put(TautomerConst.CACTVS_ENERGY_RANK, newtag);
		tags.put(TautomerConst.TAUTOMER_RANK, newtag);
	}

	/**
	 * This is last resort, when creating SMILES from structure fails for
	 * whatever reason
	 * 
	 * @param inchi
	 * @return
	 * @throws Exception
	 */
	protected String InChI2SMILES(String inchi) throws Exception {
		try {
			if (igf == null)
				igf = InChIGeneratorFactory.getInstance();

			InChIToStructure c = igf.getInChIToStructure(inchi,
					SilentChemObjectBuilder.getInstance());

			IAtomContainer a = c.getAtomContainer();
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(a);
			CDKHueckelAromaticityDetector.detectAromaticity(a);

			if (getSmilesGenerator() != null)
				return getSmilesGenerator().create(a);
			else
				return null;
		} catch (Exception x) {
			logger.log(Level.WARNING, x.getMessage());
			return null;
		}
	}

}
