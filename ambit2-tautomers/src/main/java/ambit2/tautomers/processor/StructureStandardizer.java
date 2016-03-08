package ambit2.tautomers.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.vecmath.Vector2d;

import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.sf.jniinchi.INCHI_OPTION;

import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.stereo.StereoElementFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.Property;
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
	protected boolean generateSMILES_Canonical = true;
	protected boolean splitFragments = false;
	protected boolean generateTautomers = false;
	protected boolean generateSMILES = false; // not canonical
	protected boolean neutralise = false;
	protected boolean generate2D = false;
	protected boolean implicitHydrogens = false;
	protected boolean generateStereofrom2D = false;
	protected boolean clearIsotopes = false;

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

	protected transient TautomerProcessor tautomers = new TautomerProcessor();
	protected transient FragmentProcessor fragments = new FragmentProcessor();
	protected transient NeutraliseProcessor neutraliser = null;
	protected transient IsotopesProcessor isotopesProcessor = null;
	protected transient StructureDiagramGenerator sdg = new StructureDiagramGenerator();

	protected transient CDKHydrogenAdder hadder = CDKHydrogenAdder
			.getInstance(SilentChemObjectBuilder.getInstance());
	protected transient InChIGeneratorFactory igf = null;
	protected transient SmilesGenerator smilesGenerator = SmilesGenerator
			.isomeric();
	protected transient SmilesGenerator smilesGeneratorAbsolute = SmilesGenerator
			.absolute();

	public StructureStandardizer() {
		super();
		options.add(INCHI_OPTION.SAbs);
		options.add(INCHI_OPTION.SAsXYZ);
		options.add(INCHI_OPTION.SPXYZ);
		options.add(INCHI_OPTION.FixSp3Bug);
		options.add(INCHI_OPTION.AuxNone);
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
					neutraliser = new NeutraliseProcessor();
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
						isotopesProcessor = new IsotopesProcessor();

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
						err = processed.getProperty(ERROR_TAG);
						processed.setProperty(ERROR_TAG, String.format(
								"%s %s %s", err == null ? "" : err, x
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
							processed.setProperty(Property.opentox_InChI,
									gen.getInchi());
							processed.setProperty(Property.opentox_InChIKey,
									gen.getInchiKey());
						} catch (Exception x) {
							err = processed.getProperty(ERROR_TAG);
							processed.setProperty(ERROR_TAG, String.format(
									"%s %s %s", err == null ? "" : err, x
											.getClass().getName(), x
											.getMessage()));
						}
				}
				if (generateSMILES || generateSMILES_Canonical) {
					Property smiles = Property.getSMILESInstance();
					try {
						if (generateSMILES_Canonical)
							processed.setProperty(smiles,
									smilesGeneratorAbsolute.create(processed));
						else
							processed.setProperty(smiles,
									smilesGenerator.create(processed));
					} catch (Exception x) {
						if (processed.getProperty(smiles) == null)
							processed.setProperty(smiles, "");
						err = processed.getProperty(ERROR_TAG);
						processed.setProperty(ERROR_TAG, String.format(
								"%s %s %s", err == null ? "" : err, x
										.getClass().getName(), x.getMessage()));
						logger.log(Level.WARNING, x.getMessage());
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

}
