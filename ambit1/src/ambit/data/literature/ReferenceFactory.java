/**
 * Created on 2005-3-22
 *
 */
package ambit.data.literature;

/**
 * Provides static functions to create several literature entries 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
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
		AuthorEntries authors = new AuthorEntries();
		authors.addItem(new AuthorEntry(""));
		JournalEntry journal = new JournalEntry("");
		LiteratureEntry ref = new LiteratureEntry(
				reference,
				journal,
				"","",0,authors
				);
		ref.setWWW(www);
		return ref;
	}
	/**
	 * Demo
	 * Create a JournalEntry for the journal "Mutation Research"
	 * @return {@link JournalEntry}
	 */
	public static JournalEntry createJournalMutRes() {
		JournalEntry journal = 
			new JournalEntry("Mutation Res.","Mutation Research");
		journal.setPublisher("Elsevier");
		return journal;
	}
	/**
	 * Creates a reference for the Ames test
	 * @return the reference
	 */
	public static LiteratureEntry createAmesReference() {
		AuthorEntries authors = new AuthorEntries();
		authors.addItem(new AuthorEntry("Maron D.M."));
		authors.addItem(new AuthorEntry("Ames B.N."));
		
		JournalEntry journal = createJournalMutRes();
		LiteratureEntry ref = new LiteratureEntry(
				"Revised methods for the Salmonella mutagenicity test",
				journal,
				"113","173-215",1983,authors
				);
		return ref;
	}
	/**
	 * A demo how to create {@link AuthorEntries} from the reference in {@link ReferenceFactory#createDebnathReference()}
	 * @return the author entries created
	 */
	public static AuthorEntries createDebnathRefAuthors() {
		AuthorEntries authors = new AuthorEntries();
		authors.addItem(new AuthorEntry("Debnath A.K."));
		authors.addItem(new AuthorEntry("Debnath G."));
		authors.addItem(new AuthorEntry("Shusterman A.J."));
		authors.addItem(new AuthorEntry("Hansch C."));
		return authors;
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
		AuthorEntries authors = createDebnathRefAuthors();
		
		JournalEntry journal = new JournalEntry("Environ. Mol. Mutagen.");
		journal.setName("Environmental and Molecular Mutagenics");
		LiteratureEntry reference = new LiteratureEntry(
				"A QSAR investigation of the role of hydrophobicity in regulating mutagenicity in the Ames test:"+
				"Part 1. Mutagenicity aromatic and heteroaromatic amines in Salmonella typhimurium TA98 and TA100",
				journal,
				"19",
				"37-52",
				1992,
				authors
				); 
		return reference;
		
	}
	/**
	 * Creates the reference for a test data set for the model in {@link ReferenceFactory#createDebnathReference()} 
	 * @return {@link LiteratureEntry}
	 */
	public static LiteratureEntry createGlendeReference() {
				AuthorEntries authors = new AuthorEntries();
				authors.addItem(new AuthorEntry("Glende C."));
				authors.addItem(new AuthorEntry("Schmitt H."));
				authors.addItem(new AuthorEntry("Erdinger L."));
				authors.addItem(new AuthorEntry("Boche G."));
				
				JournalEntry journal = createJournalMutRes();
				journal.setPublisher("Elsevier");
				LiteratureEntry reference = new LiteratureEntry(
						"Transformation of mutagenic aromatic amines into non-mutagenic species by alkyl substituents "+
						"Part I. Alkylation ortho to the amino function",
						journal,
						"498",
						"19-37",
						2001,
						authors
						); 
				return reference;
				
			}
	/**
	 * Creates reference for P.Grammatica BCF model at QSAR Comb Sci 22 (2003)
	 */
	public static LiteratureEntry createBCFGrammaticaReference() {
		JournalEntry journal = new JournalEntry("QSAR Comb. Sci.");
		AuthorEntries authors = new AuthorEntries();
		authors.addItem(new AuthorEntry("Grammatica P."));
		authors.addItem(new AuthorEntry("Papa E."));
		
		LiteratureEntry reference = new LiteratureEntry(
				"QSAR Modelling of Bioconcentration Factor by theoretical molecular descriptors",
				journal,
				"22",
				"374-385",
				2003,
				authors
				); 
		reference.setWWW("http://www.syrres.com/esc/bcfwin.htm");
		return reference;
	}
	public static LiteratureEntry createSearchReference(String name) {
		JournalEntry journal = new JournalEntry("Ambit Database Tools");
		AuthorEntries authors = new AuthorEntries();
		authors.addItem(new AuthorEntry("Ambit Database Tools"));
		
		LiteratureEntry reference = new LiteratureEntry(
				name,
				journal,
				"",
				"",
				2007,
				authors
				); 
		reference.setWWW("http://ambit.acad.bg");
		return reference;
	}
	/**
	 * Creates SRC BCFWin reference
	 * @return the reference created
	 * <a href="http://www.syrres.com/esc/bcfwin.htm">SRC BcfWin program</a>	 * 
	 */
	public static LiteratureEntry createBCFWinReference() {
		JournalEntry journal = new JournalEntry("Environ. Toxicol. Chem.");
		AuthorEntries authors = new AuthorEntries();
		authors.addItem(new AuthorEntry("Meylan,W.M."));
		authors.addItem(new AuthorEntry("Howard,P.H."));
		authors.addItem(new AuthorEntry("Boethling,RS"));
		
		LiteratureEntry reference = new LiteratureEntry(
				"Improved Method for Estimating Bioconcentration / Bioaccumulation Factor from Octanol/Water Partition Coefficient",
				journal,
				"18(4)",
				"664-672",
				1996,
				authors
				); 
		reference.setWWW("http://www.syrres.com/esc/bcfwin.htm");
		return reference;
	}
	/**
	 * Creates SRC KowWin reference
	 * @see <a href="http://www.syrres.com/esc/kowwin.htm" target=_blank>SRC KowWin program</a>
	 * @return the reference
	 */
	public static LiteratureEntry createKOWWinReference() {
		JournalEntry journal = new JournalEntry("J. Pharm. Sci.","J. Pharm. Sci.");
		AuthorEntries authors = new AuthorEntries();
		authors.addItem(new AuthorEntry("Meylan,W.M."));
		authors.addItem(new AuthorEntry("Howard,P.H."));
		
		LiteratureEntry reference = new LiteratureEntry(
				"Atom/fragment contribution method for estimating octanol-water partition coefficients",
				journal,
				"84",
				"83-92",
				1995,
				authors
				);
		reference.setWWW("http://www.syrres.com/esc/kowwin.htm");
		return reference;
	}	
	
	/**
	 * Reference for ECOTOX database (AQUIRE and TERRETOX). http:/www.epa.gov/ecotox/
	 * @return
	 */
	public static LiteratureEntry createECOTOXReference() {
		
		JournalEntry journal = new JournalEntry("U.S. Environmental Protection Agency");
		AuthorEntries authors = new AuthorEntries();
		authors.addItem(new AuthorEntry("U.S. EPA"));
		
		LiteratureEntry reference = new LiteratureEntry(
				"U.S. Environmental Protection Agency. 2006. ECOTOX User Guide: ECOTOXicology Database System. Version 4.0. Available: http:/www.epa.gov/ecotox/",
				journal,
				"",
				"",
				2006,
				authors
				);
		reference.setWWW("http:/www.epa.gov/ecotox/");
		return reference;		


	}
}
