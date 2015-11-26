package ambit2.rendering;

import java.io.StringWriter;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.AtomContainerSet;
import org.openscience.cdk.smiles.FixBondOrdersTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.data.MoleculeTools;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.io.MDLWriter;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.core.smiles.SmilesParserWrapper.SMILES_PARSER;

public class StructureEditorProcessor extends DefaultAmbitProcessor<IStructureRecord,String> {
	protected _commands command = _commands.layout;
	public _commands getCommand() {
		return command;
	}

	public void setCommand(_commands command) {
		this.command = command;
	}

	public enum _commands {
		layout,
		aromatize,
		dearomatize
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -6384602483929803381L;

	public StructureEditorProcessor(String command) {
		try {
			this.command = _commands.valueOf(command);
		} catch (Exception x) {
			this.command = _commands.layout;
		}
	}
	@Override
	public String process(IStructureRecord record) throws AmbitException {
		try {
			MOL_TYPE mtype = MOL_TYPE.valueOf(record.getFormat());
			IAtomContainer mol = null;
			switch (mtype) {
			case SDF: {
				MoleculeReader reader = new MoleculeReader();
				mol = reader.process(record);		
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
				reader = null;				
				break;
			}
			case CSV: { //smiles
				SmilesParserWrapper p = SmilesParserWrapper.getInstance(SMILES_PARSER.CDK);
				mol = p.parseSmiles(record.getContent());
				break;
			}
			default: throw new AmbitException("Unsupported format");
			}
			//actions
			if (mol!=null && mol.getAtomCount()>0)
				switch (command) {
				case layout : {
					IAtomContainerSet molecules = new AtomContainerSet();
					CompoundImageTools cit = new CompoundImageTools();
					cit.generate2D(mol, true, molecules);
					mol = MoleculeTools.newAtomContainer(mol.getBuilder());
					for (IAtomContainer m : molecules.atomContainers()) {
						mol.add(m);
					}
					break;
				}
				case aromatize : 
					if (mol!=null)
						CDKHueckelAromaticityDetector.detectAromaticity(mol);
					break;					
				case dearomatize :
					if (mol!=null) {
						FixBondOrdersTool fbt = new FixBondOrdersTool();
						mol = fbt.kekuliseAromaticRings((IAtomContainer)mol);
						for (IBond bond : mol.bonds()) bond.setFlag(CDKConstants.ISAROMATIC,false);
					}	
					break;
				default: 
					throw new AmbitException("Unknown command");
				}
				StringWriter w = new StringWriter();
				MDLWriter writer = new MDLWriter(w);
				writer.writeMolecule(mol);
				writer.close();
				return w.toString();
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		}

	}

}
