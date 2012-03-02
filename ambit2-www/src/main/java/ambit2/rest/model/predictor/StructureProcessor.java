package ambit2.rest.model.predictor;

import java.io.StringWriter;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.io.MDLWriter;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.core.processors.structure.StructureTypeProcessor;
import ambit2.db.model.ModelQueryResults;
import ambit2.mopac.MopacShell;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.property.PropertyURIReporter;

public class StructureProcessor  extends	AbstractStructureProcessor<MopacShell> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8106073411946010587L;
	protected transient MopacShell mopacshell;
	protected transient StructureTypeProcessor stype = new StructureTypeProcessor();
	protected transient MoleculeReader reader = new MoleculeReader();
	
	public StructureProcessor(
			Reference applicationRootReference,
			ModelQueryResults model, 
			ModelURIReporter modelReporter,
			PropertyURIReporter propertyReporter, 
			String[] targetURI) throws ResourceException {
		super(applicationRootReference,model,modelReporter,propertyReporter,targetURI);
		structureRequired = true;
		valuesRequired = false;
	}
	

	@Override
	public synchronized MopacShell createPredictor(ModelQueryResults model)
			throws ResourceException {
			try {
				mopacshell = new MopacShell();
				mopacshell.setErrorIfDisconnected(false);
				return mopacshell;
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
			}
	}


	@Override
	public Object predict(IStructureRecord target) throws AmbitException {
		try {
			IAtomContainer mol = reader.process(target);
			if (mol!= null) {
				IAtomContainer newmol = predictor.process(mol);
				if (newmol!= null) {
					StringWriter sw = new StringWriter();
					MDLWriter writer = new MDLWriter(sw);
					newmol.setProperty(CDKConstants.TITLE,mopacshell.getMopac_commands());
					writer.writeMolecule(newmol);
					
					target.setContent(sw.toString());
					target.setType(stype.process(newmol));
					target.setFormat(MOL_TYPE.SDF.toString());
					updateStructure.setObject(target);
					exec.process(updateStructure);
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		return target;
	}
	@Override
	public String toString() {

		StringBuilder b = new StringBuilder();
		b.append(String.format("Structures required\t%s\n",structureRequired?"YES":"NO"));

		if (predictor != null) {
			b.append(predictor.toString());
		}
		return b.toString();
				
	}	

}
