package ambit2.rest.model.predictor;

import java.io.StringWriter;
import java.util.logging.Level;

import javax.vecmath.Point2d;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.silent.AtomContainerSet;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.core.data.MoleculeTools;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.core.io.MDLWriter;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.core.processors.structure.StructureTypeProcessor;
import ambit2.rendering.CompoundImageTools;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.property.PropertyURIReporter;

public class Structure2DProcessor extends
		AbstractStructureProcessor<CompoundImageTools> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2666994533139041193L;
	/**
	 * 
	 */
	protected transient StructureTypeProcessor stype = new StructureTypeProcessor();
	protected transient MoleculeReader reader = new MoleculeReader();

	public Structure2DProcessor(Reference applicationRootReference,
			ModelQueryResults model, ModelURIReporter modelReporter,
			PropertyURIReporter propertyReporter, String[] targetURI)
			throws ResourceException {
		super(applicationRootReference, model, modelReporter, propertyReporter,
				targetURI);
		structureRequired = true;
		valuesRequired = false;
	}

	@Override
	public synchronized CompoundImageTools createPredictor(
			ModelQueryResults model) throws ResourceException {
		try {
			return new CompoundImageTools();
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					x.getMessage(), x);
		}
	}

	@Override
	public Object predict(IStructureRecord target) throws AmbitException {
		try {
			IAtomContainer mol = reader.process(target);
			IAtomContainer newmol = null;
			if (mol != null) {
				STRUC_TYPE structype = stype.process(mol);
				if (mol.getAtomCount() == 1) {
					newmol = mol;
					mol.getAtom(0).setPoint2d(
							new Point2d(new double[] { 0.0001, 0.0001 }));
				} else {
					if (!STRUC_TYPE.D1.equals(structype))
						return target;
					IAtomContainerSet molecules = new AtomContainerSet();
					newmol = MoleculeTools.newAtomContainer(mol.getBuilder());
					predictor.generate2D(mol, true, molecules);
					for (int i = 0; i < molecules.getAtomContainerCount(); i++)
						newmol.add(molecules.getAtomContainer(i));
				}
				if (newmol != null) {
					StringWriter sw = new StringWriter();
					MDLWriter writer = new MDLWriter(sw);
					writer.writeMolecule(newmol);

					target.setContent(sw.toString());
					structype = stype.process(newmol);
					target.setType(structype);
					target.setFormat(MOL_TYPE.SDF.toString());
					updateStructure.setObject(target);
					exec.process(updateStructure);
				}
			}
		} catch (Exception x) {
			logger.log(Level.SEVERE, x.getMessage(), x);
		}
		return target;
	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder();
		b.append(String.format("Structures required\t%s\n",
				structureRequired ? "YES" : "NO"));

		if (predictor != null) {
			b.append(predictor.toString());
		}
		return b.toString();

	}

}