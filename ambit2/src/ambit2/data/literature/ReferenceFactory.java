/**
 * Created on 2005-3-22
 *
 */
package ambit2.data.literature;

/**
 * Provides static functions to create several literature entries 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2008-4-20
 */
public class ReferenceFactory {

	/**
	 * 
	 */
	protected ReferenceFactory() {
		super();
	}
	/**
	 * Creates an empty reference
	 * @return {@link LiteratureEntry}
	 */
	public static LiteratureEntry createEmptyReference() {
		return createDatasetReference("","");
	}
	/**
	 * Creates a reference for a dataset (provided it is the filename and www site)
	 * @param reference
	 * @param www
	 * @return {@link LiteratureEntry}
	 */
	public static LiteratureEntry createDatasetReference(String reference, String www) {
		return new LiteratureEntry(reference,www);
	}

	/**
	 * Creates a reference for the Ames test
	 * @return the reference
	 */
	public static LiteratureEntry createAmesReference() {
		return new LiteratureEntry(
				"Maron D.M.,Ames B.N.,Revised methods for the Salmonella mutagenicity test, Mutation Res., 113,pp.173-215,1983"
				);
	}
		
	/**
	 * A demo how to create LiteratureEntry from
	 * <code> 
	 * A.K. Debnath, G. Debnath, A.J. Shusterman, C. Hansch,
	 * A QSAR investigation of the role of hydrophobicity in
	 * regulating mutagenicity in the Ames test: Part 1. Mutagenicity
	 * of aromatic and heteroaromatic amines in Salmonella
	 * typhimurium TA98 and TA100, Environ. Mol. Mutagen. 19
	 * (1992) 37–52.
	 * </code>
	 * @return the literature entry created
	 */
	public static LiteratureEntry createDebnathReference() {
		return new LiteratureEntry(
				"Debnath A.K.,Debnath G.,Shusterman A.J.,Hansch C.A QSAR investigation of the role of hydrophobicity in regulating mutagenicity in the Ames test:"+
				"Part 1. Mutagenicity aromatic and heteroaromatic amines in Salmonella typhimurium TA98 and TA100"+
				"Environ. Mol. Mutagen.,19,37-52,1992");
	}
	/**
	 * Creates the reference for a test data set for the model in {@link ReferenceFactory#createDebnathReference()} 
	 * @return {@link LiteratureEntry}
	 */
	public static LiteratureEntry createGlendeReference() {
				return new LiteratureEntry(
						"Glende C.,Schmitt H.,Erdinger L.,Boche G."+
						"Transformation of mutagenic aromatic amines into non-mutagenic species by alkyl substituents "+
						"Part I. Alkylation ortho to the amino function",
						"Mutation Res.,498,pp.19-37,2001");
				
			}
	/**
	 * Creates reference for P.Grammatica BCF model at QSAR Comb Sci 22 (2003)
	 */
	public static LiteratureEntry createBCFGrammaticaReference() {
		return new LiteratureEntry(
				"Grammatica P.,Papa E.,"+
				"QSAR Modelling of Bioconcentration Factor by theoretical molecular descriptors"+
				"QSAR Comb. Sci.,22,374-385,2003",
				"http://www.syrres.com/esc/bcfwin.htm");
	}

	/**
	 * Creates SRC BCFWin reference
	 * @return the reference created
	 * <a href="http://www.syrres.com/esc/bcfwin.htm">SRC BcfWin program</a>	 * 
	 */
	public static LiteratureEntry createBCFWinReference() {
		return new LiteratureEntry(
				"Meylan,W.M.,Howard,P.H.,Boethling,RS.,"+
				"Improved Method for Estimating Bioconcentration / Bioaccumulation Factor from Octanol/Water Partition Coefficient"+
				"Environ. Toxicol. Chem., 18(4),pp.664-672,1996",
				"http://www.syrres.com/esc/bcfwin.htm"
				); 
	}
	/**
	 * Creates SRC KowWin reference
	 * @see <a href="http://www.syrres.com/esc/kowwin.htm" target=_blank>SRC KowWin program</a>
	 * @return the reference
	 */
	public static LiteratureEntry createKOWWinReference() {

		return new LiteratureEntry(
				"Meylan,W.M.,Howard,P.H."+
				"Atom/fragment contribution method for estimating octanol-water partition coefficients"+
				"J. Pharm. Sci.,84,83-92,1995",
				"http://www.syrres.com/esc/kowwin.htm"
				);
	}	
	
	/**
	 * Reference for ECOTOX database (AQUIRE and TERRETOX). http:/www.epa.gov/ecotox/
	 * @return
	 */
	public static LiteratureEntry createECOTOXReference() {
		
		return new LiteratureEntry(
				"U.S. Environmental Protection Agency. 2006. ECOTOX User Guide: ECOTOXicology Database System. Version 4.0.",
				"http:/www.epa.gov/ecotox/"
				);
	


	}
}
