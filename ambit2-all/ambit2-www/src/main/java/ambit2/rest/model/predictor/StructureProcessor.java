package ambit2.rest.model.predictor;

import java.awt.image.BufferedImage;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.core.io.MDLWriter;
import ambit2.core.processors.structure.StructureTypeProcessor;
import ambit2.db.UpdateExecutor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.update.structure.UpdateStructure;
import ambit2.mopac.MopacShell;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.property.PropertyURIReporter;

public class StructureProcessor  extends	ModelPredictor<MopacShell,IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8106073411946010587L;
	protected MopacShell mopacshell;
	protected StructureTypeProcessor stype = new StructureTypeProcessor();
	protected UpdateStructure updateStructure = new UpdateStructure();
	protected UpdateExecutor<UpdateStructure> exec = new UpdateExecutor<UpdateStructure>();
	
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
	public void assignResults(IStructureRecord record, Object value)
			throws AmbitException {
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		exec.setConnection(connection);
	}
	@Override
	public void close() throws SQLException {
		try {exec.close();} catch (Exception x) {}
		super.close();
	}

	@Override
	public synchronized MopacShell createPredictor(ModelQueryResults model)
			throws ResourceException {
			try {
				mopacshell = new MopacShell();
				return mopacshell;
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
			}
	}
	@Override
	public String getCompoundURL(IStructureRecord target) throws AmbitException {
		return null;
	}

	@Override
	public Object predict(IStructureRecord target) throws AmbitException {
		try {
			IMolecule mol = MoleculeTools.readMolfile(target.getContent());
			if (mol!= null) {
				IAtomContainer newmol = predictor.process(mol);
				if (newmol!= null) {
					StringWriter sw = new StringWriter();
					MDLWriter writer = new MDLWriter(sw);
					newmol.setID(mopacshell.getMopac_commands());
					writer.writeMolecule(newmol);
					
					target.setContent(sw.toString());
					target.setType(stype.process(newmol));
					updateStructure.setObject(target);
					exec.process(updateStructure);
				}
			}
		} catch (Exception x) {
			
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
	@Override
	public BufferedImage getLegend(int width, int height) throws AmbitException {
		return null;
	}
}
