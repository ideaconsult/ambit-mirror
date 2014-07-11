package ambit2.rest.model.predictor;

import java.io.StringWriter;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.core.io.MDLWriter;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.core.processors.structure.StructureTypeProcessor;
import ambit2.mopac.AbstractMopacShell;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.property.PropertyURIReporter;

public class StructureProcessor  extends	AbstractStructureProcessor<AbstractMopacShell> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8106073411946010587L;
	protected transient AbstractMopacShell mopacshell;
	protected transient StructureTypeProcessor stype = new StructureTypeProcessor();
	protected transient MoleculeReader reader = new MoleculeReader();
	protected String mopac_commands;
	
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
	
	public String getMopac_commands() {
		return mopac_commands;
	}

	public void setMopac_commands(String mopac_commands) {
		this.mopac_commands = mopac_commands;
	}

	
	@Override
	public synchronized AbstractMopacShell createPredictor(ModelQueryResults model)
			throws ResourceException {
			try {
				Class clazz = this.getClass().getClassLoader().loadClass( model.getContent().toString());
				mopacshell = (AbstractMopacShell) clazz.newInstance();
				mopacshell.setErrorIfDisconnected(false);
				if (mopac_commands!=null) {
					mopacshell.setMopac_commands(mopac_commands);
				}
				return mopacshell;
			} catch (ClassNotFoundException x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
			} catch (InstantiationException x) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
			} catch (IllegalAccessException x) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);				
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
					STRUC_TYPE structype = stype.process(newmol);
					if (STRUC_TYPE.D3withH.equals(structype))
						target.setType(STRUC_TYPE.optimized);
					else 
						target.setType(structype);
					target.setFormat(MOL_TYPE.SDF.toString());
					updateStructure.setObject(target);
					exec.process(updateStructure);
				}
			}
		} catch (Exception x) {
			logger.log(Level.SEVERE,x.getMessage(),x);
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
