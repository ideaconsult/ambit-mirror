package ambit2.rest.task.tautomers;

import java.io.StringWriter;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.smiles.FixBondOrdersTool;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.core.processors.structure.StructureTypeProcessor;
import ambit2.db.chemrelation.UpdateStructureRelation;
import ambit2.db.processors.RepositoryWriter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.predictor.AbstractStructureProcessor;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.tautomers.TautomerManager;

public class TautomersGenerator  extends	AbstractStructureProcessor<TautomerManager> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6427724584905751373L;
	protected transient StructureTypeProcessor stype = new StructureTypeProcessor();
	protected transient MoleculeReader reader = new MoleculeReader();
	protected RepositoryWriter writer;
	protected FixBondOrdersTool kekulizer = new FixBondOrdersTool();
	protected UpdateStructureRelation updateStrucRelationQuery = new UpdateStructureRelation();
	
	public TautomersGenerator(
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
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		setCloseConnection(true);
		writer = new RepositoryWriter();
		writer.setConnection(connection);
		writer.setCloseConnection(false);
		writer.setDataset(new SourceDataset("TAUTOMERS"));
		writer.setUseExistingStructure(true);
	}
	@Override
	public void close() throws Exception {
		try {writer.setConnection(null);} catch (Exception x) {}
		super.close();
	}

	@Override
	public synchronized TautomerManager createPredictor(ModelQueryResults model)
			throws ResourceException {
			try {
				return new TautomerManager();
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
			}
	}

	@Override
	public String createResultReference() throws Exception {
		// TODO Auto-generated method stub
		return super.createResultReference();
	}

	@Override
	public Object predict(IStructureRecord target) throws AmbitException {
		
		IAtomContainer best = null;
		updateStrucRelationQuery.setGroup(target);
		updateStrucRelationQuery.setRelation(STRUCTURE_RELATION.HAS_TAUTOMER.name());
		
		try {
			IAtomContainer mol = reader.process(target);
			if (mol!= null) {
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
				CDKHueckelAromaticityDetector.detectAromaticity(mol);
				boolean aromatic = false;
				for (IBond bond : mol.bonds()) if (bond.getFlag(CDKConstants.ISAROMATIC)) {aromatic = true; break;}
				if (aromatic && (mol instanceof IMolecule)) mol = kekulizer.kekuliseAromaticRings((IMolecule)mol);

				//implicit H count is NULL if read from InChI ...
				mol = AtomContainerManipulator.removeHydrogens(mol);
				CDKHydrogenAdder.getInstance(mol.getBuilder()).addImplicitHydrogens(mol);
				
				List<IAtomContainer> resultTautomers=null;
				predictor.setStructure(mol);
				resultTautomers = predictor.generateTautomersIncrementaly();
				double bestRank = 0;

				
				IStructureRecord newrecord = new StructureRecord();
				if (resultTautomers.size()>0) {
					writer.setConnection(connection);
				}
				for (IAtomContainer tautomer: resultTautomers) {
					try {
						Object rank_property = tautomer.getProperty("TAUTOMER_RANK");
						double rank;
						if (rank_property == null) {
							logger.log(Level.INFO, String.format("No tautomer rank, probably this is the original structure"));
							continue;
						} else {
							rank = Double.parseDouble(rank_property.toString());
							/**
							 * The rank is energy based, lower rank is better
							 */
							if ((best==null) || (bestRank>rank)) {
								bestRank = rank;
								best = tautomer;
							}
						}
						aromatic = false;
						for (IBond bond : tautomer.bonds()) if (bond.getFlag(CDKConstants.ISAROMATIC)) {aromatic = true; break;}
						if (aromatic) 
							tautomer = kekulizer.kekuliseAromaticRings((IMolecule)tautomer);
						
						
						StringWriter w = new StringWriter();
						SDFWriter molwriter = new SDFWriter(w); 

						//tautomer.getProperties().clear();
						molwriter.write(tautomer);
						w.write("$$$$\n");
						molwriter.close();
						
						newrecord.clear();
						newrecord.setContent(w.toString());
						newrecord.setFormat(MOL_TYPE.SDF.name());
						molwriter.close();
						List<IStructureRecord> strucWritten = writer.write(newrecord);
						for (IStructureRecord struc : strucWritten) {
							//if (target.getIdchemical()==struc.getIdchemical()) continue;
							updateStrucRelationQuery.setObject(struc);
							updateStrucRelationQuery.setMetric(rank);
							exec.process(updateStrucRelationQuery);
							
						}
					} catch (Exception x) {
						logger.log(Level.WARNING, x.getMessage());
					}
					//if (all) writeResult(writer,molecule,tautomer);
				}				

			}
		} catch (Exception x) {
			logger.log(Level.SEVERE,x.getMessage(),x);
		} finally {
		}
		return best==null?target:best; //return the best tautomer. all others are written
	}
	@Override
	public String toString() {

		StringBuilder b = new StringBuilder();
		b.append(String.format("Structures required\t%s\n",structureRequired?"YES":"NO"));
		b.append("Tautomers generator");
		return b.toString();
				
	}	

}
