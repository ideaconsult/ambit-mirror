package ambit2.namestructure;

import nu.xom.Element;

import org.openscience.cdk.interfaces.IAtomContainer;

import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.data.MoleculeTools;

public class Name2StructureProcessor extends
		DefaultAmbitProcessor<String,IAtomContainer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6157770369957204199L;

	public IAtomContainer process(String target) throws AmbitException {
		try {
			NameToStructure nameToStructure = NameToStructure.getInstance();
			Element cmlElement = nameToStructure.parseToCML(target.trim());
			if (cmlElement != null)
				return MoleculeTools.readCMLMolecule(cmlElement.toXML());
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		throw new AmbitException("Unable to parse chemical name '"+target+"'");
	}

}
