package ambit2.namestructure;

import nu.xom.Element;

import org.openscience.cdk.interfaces.IAtomContainer;

import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.data.MoleculeTools;

/**
 * Name to structure convertor, based on OPSIN package
 * http://sourceforge.net/projects/oscar3-chem
 * @author nina
 <pre>
According to OPSIN 0.5.0 Change log :
 
Supported nomenclature includes:
alkanes/alkenes/alkynes/heteroatom chains e.g. hexane, hex-1-ene, tetrasiloxane and their cyclic analogues e.g. cyclopropane
All IUPAC 1993 recommended rings
Trivial acids
Hantzsch-Widman e.g. 1,3-oxazole
Mono spiro systems 
All von Baeyer rings e.g. bicyclo[2.2.2]octane
Hydro e.g. 2,3-dihydropyridine
Indicated hydrogen e.g. 1H-benzoimidazole
Heteroatom replacement
Specification of charge e.g. ium/ide
Multiplicative nomenclature e.g. ethylenediaminetetraacetic acid
Fused ring systems with only two rings e.g. imidazo[4,5-d]pyridine
Ring assemblies e.g. biphenyl
The following functional classes: esters, diesters, glycols, acids, azides, bromides, chlorides, cyanates, cyanides, fluorides, fulminates, hydroperoxides, iodides, isocyanates, isocyanides, isoselenocyanates, isothiocyanates, selenocyanates, thiocyanates, alcohols, selenols, thiols, ethers, ketones, peroxides, selenides, selenones, selenoxides, selones, selenoketones, sulfides, sulfones, sulfoxides, tellurides, telluroketones, tellurones, telluroxides and thioketones

Currently UNsupported nomenclature includes:
Stereochemistry (terms are identified and ignored)
Greek letters
Lambda convention
Amino Acids (simple subsitutitive operations are allowed)
Carbohydrates
Steroids
Nucleic acids
Bridged rings 
Fused ring systems with more than two rings
Some conjuctive operations e.g. cyclohexaneethanol
Functional replacement nomenclature e.g. ethanthioic acid
The following functional classes: Hydrazides, lactones, lactams, acetals, hemiacetals, oxime, oxides, ketals, hydrazones, anhydrides and semicarbazones
 
 </pre>
 *
 */
public class Name2StructureProcessor extends
		DefaultAmbitProcessor<String,IAtomContainer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6157770369957204199L;
	protected NameToStructure nameToStructure = null;
	public synchronized IAtomContainer process(String target) throws AmbitException {
		try {
			if (nameToStructure == null) nameToStructure = NameToStructure.getInstance();
			Element cmlElement = nameToStructure.parseToCML(target.trim());
			if (cmlElement != null)
				return MoleculeTools.readCMLMolecule(cmlElement.toXML());
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		throw new AmbitException("Unable to parse chemical name '"+target+"'");
	}
    /*
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage: "+Name2StructureProcessor.class.getName() + " chemical_name");
			return;
		}
	}
	*/
}
