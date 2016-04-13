package ambit2.smarts.processors;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.processors.AbstractStructureProcessor;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SmartsConst;
import ambit2.smarts.SmartsConst.SSM_MODE;

public class SMIRKSProcessor extends AbstractStructureProcessor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5540483406795033374L;

	protected boolean loadExamples = false;

	public boolean isLoadExamples() {
		return loadExamples;
	}

	public void setLoadExamples(boolean loadExamples) {
		this.loadExamples = loadExamples;
	}

	/**
	 * assigns performed transformations as atomcontainer properties
	 * 
	 * @return
	 */
	public boolean isTransformationasproperties() {
		return transformationasproperties;
	}

	public void setTransformationasproperties(boolean transformationasproperties) {
		this.transformationasproperties = transformationasproperties;
	}

	protected boolean transformationasproperties = false;

	protected List<SMIRKSTransformation> transformations = null;

	public List<SMIRKSTransformation> getTransformations() {
		return transformations;
	}

	protected SMIRKSManager smrkMan;

	public SMIRKSProcessor(Logger logger) {
		super(logger);
		smrkMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
	}

	public SMIRKSProcessor(File jsonFile, Logger logger) throws Exception {
		this(logger);
		loadReactionsFromJSON(jsonFile);
	}
	public SMIRKSProcessor(InputStream jsonFile, Logger logger) throws Exception {
		this(logger);
		loadReactionsFromJSON(jsonFile);
	}


	public void loadReactionsFromJSON(File jsonFile) throws Exception {
		loadReactionsFromJSON(new FileInputStream(jsonFile));

	}

	public void loadReactionsFromJSON(InputStream fin) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;

		try {
			root = mapper.readTree(fin);
		} catch (Exception x) {
			throw x;
		} finally {
			try {
				if (fin != null)
					fin.close();
			} catch (Exception x) {
			}
		}

		transformations = new ArrayList<SMIRKSTransformation>();

		JsonNode optionsNode = root.path("OPTIONS");
		try {
			setAtomtypeasproperties(optionsNode.get("properties")
					.get("atomtypes").asBoolean());
		} catch (Exception x) {
		}
		try {
			setSparseproperties(optionsNode.get("properties").get("sparse")
					.asBoolean());
		} catch (Exception x) {
		}
		try {
			setTransformationasproperties(optionsNode.get("properties")
					.get("transformations").asBoolean());
		} catch (Exception x) {
			setTransformationasproperties(false);
		}

		JsonNode reactionsNode = root.path("REACTIONS");
		if ((reactionsNode == null) || reactionsNode.isMissingNode())
			throw new Exception("REACTIONS section is missing!");

		if (!reactionsNode.isArray())
			throw new Exception("REACTIONS section is not array!");

		for (int i = 0; i < reactionsNode.size(); i++)
			try {
				SMIRKSTransformation reaction = parseTransformation(
						(ObjectNode) reactionsNode.get(i), loadExamples);
				reaction.setId(i + 1);
				if (reaction.isEnabled())
					transformations.add(reaction);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "MSG_ERR_TRANSFORMATION",
						new Object[] { (i + 1), e.getMessage() });
			}
		Collections.sort(transformations,new Comparator<SMIRKSTransformation>() {
			@Override
			public int compare(SMIRKSTransformation o1, SMIRKSTransformation o2) {
				return o1.getOrder()-o2.getOrder();
			}
		});
		/*
		for (SMIRKSTransformation t : transformations) {
			System.out.print(t.getOrder());
			System.out.println(t.getName());
		}
		*/	
	}

	public void configureReactions(SMIRKSManager smrkMan) throws Exception {
		if (transformations == null)
			return;

		for (SMIRKSTransformation reaction : transformations)
			reaction.configure(smrkMan);
	}

	public SMIRKSTransformation getReactionByID(int id) {
		if (transformations != null)
			for (SMIRKSTransformation r : transformations) {
				if (r.getId() == id)
					return r;
			}

		return null;
	}

	private static final String transformed_property = "Transformed";

	@Override
	public IAtomContainer process(IAtomContainer reactant) throws Exception {
		if (transformations == null)
			return reactant;

		if (atomtypeasproperties)
			reactant = atomtypes2property(at_property, reactant, null,
					isSparseproperties());
		if (!isSparseproperties()) {
			reactant.setProperty(transformed_property, null);
			if (atomtypeasproperties) {
				reactant.setProperty(at_property_removed, null);
				reactant.setProperty(at_property_added, null);
			}
		}

		for (SMIRKSTransformation transform : transformations) {
			transform.setApplicable(false);
			// if no precondition defined, assuming always applicable
			if (!transform.hasPreconditionAtomTypeDefined()
					&& !transform.hasPreconditionAtomDefined()) {
				transform.setApplicable(true);
				logger.log(Level.FINER, transform.name);
			}
		}

		boolean hasApplicable = false;
		for (IAtom atom : reactant.atoms())
			for (SMIRKSTransformation transform : transformations)
				if (transform.isEnabled() && !transform.isApplicable()) { // if
																			// not
																			// yet
																			// set
																			// as
																			// applicable

					if (transform.hasPreconditionAtomTypeDefined()) {
						if (transform.hasPreconditionAtomtype(atom
								.getAtomTypeName())) {
							logger.log(Level.FINE, String.format(
									"Applicable %s atomtype %s",
									transform.name, atom.getAtomTypeName()));
							transform.setApplicable(true);
							hasApplicable = true;
						}
						continue;
					}
					if (transform.hasPreconditionAtomDefined()) {
						if (transform.hasPreconditionAtom(atom.getSymbol())) {
							transform.setApplicable(true);
							hasApplicable = true;
							logger.log(Level.FINE, String.format(
									"Applicable %s atom %s", transform.name,
									atom.getSymbol()));
						}
						continue;
					}
				}

		if (hasApplicable) {
			AtomContainerManipulator
					.convertImplicitToExplicitHydrogens(reactant);

			int transformed = 0;
			for (SMIRKSTransformation transform : transformations) {
				if (transform.isEnabled() && !isSparseproperties()
						&& isTransformationasproperties())
					reactant.setProperty(
							String.format("T.%s", transform.getName()), null);
				if (transform.isApplicable()) {
					transform.configure(smrkMan);

					try {
						if (transform.applyTransformation(smrkMan, reactant)) {
							if (isTransformationasproperties())
								reactant.setProperty(
										String.format("T.%s",
												transform.getName()), 1);

							logger.log(Level.FINE, String.format(
									"Transformed %s", transform.name));
							transformed++;
						} else{
							logger.log(Level.FINE, String.format(
									"Not transformed %s", transform.name));
						}
					} catch (Exception x) {
						logger.log(Level.WARNING, x.getMessage());
					}
				}
				if (reactant.getAtomCount() == 0)
					break;
			}
			if (transformed > 0) {
				if (isTransformationasproperties())
					reactant.setProperty(transformed_property, transformed);
				AtomContainerManipulator
						.percieveAtomTypesAndConfigureAtoms(reactant);
				AtomContainerManipulator.suppressHydrogens(reactant);
				if (isAtomtypeasproperties())
					reactant = atomtypes2property(reactant,
							reactant.getProperty(at_property),
							isSparseproperties());

			}
		}
		return reactant;
	}

	public static SMIRKSTransformation parseTransformation(ObjectNode node,
			boolean loadExample) throws Exception {
		try {
			String smirks = node.get("SMIRKS").asText();
			SMIRKSTransformation transformation = new SMIRKSTransformation();
			transformation.setSMIRKS(smirks);

			try {
				transformation.setEnabled(node.get("USE").getBooleanValue());
			} catch (Exception x) {
			}

			try {
				transformation.setName(node.get("NAME").asText());
			} catch (Exception x) {
			}

			try {
				transformation.setReactionClass(node.get("CLASS").asText());
			} catch (Exception x) {
			}

			try {
				transformation.setMode(SSM_MODE.valueOf(node.get("mode")
						.asText()));
			} catch (Exception x) {
				transformation
						.setMode(SmartsConst.SSM_MODE.SSM_NON_OVERLAPPING);
			}

			try {
				ArrayNode atoms = (ArrayNode) node.get("precondition").get(
						"atom");
				for (int i = 0; i < atoms.size(); i++)
					transformation.addPreconditionAtom(atoms.get(i).asText());
			} catch (Exception x) {
			}
			try {
				ArrayNode atomtypes = (ArrayNode) node.get("precondition").get(
						"atomtype");
				for (int i = 0; i < atomtypes.size(); i++)
					transformation.addPreconditionAtomtype(atomtypes.get(i)
							.asText());
			} catch (Exception x) {
			}
			if (loadExample)
				try {
					transformation.setExample(node.get("example").asText());
				} catch (Exception x) {
				}

			try {
				transformation.setOrder(node.get("order").asInt());
			} catch (Exception x) {
			}
			
			return transformation;
		} catch (Exception x) {
			// if no smirks we can't do much
			throw x;
		}
	}

}
