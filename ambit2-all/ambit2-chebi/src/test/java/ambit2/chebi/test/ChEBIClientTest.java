package ambit2.chebi.test;

import junit.framework.Assert;

import org.junit.Test;

import uk.ac.ebi.chebi.webapps.chebiWS.model.SearchCategory;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.chebi.ChEBIClient;

public class ChEBIClientTest {
	@Test
	public void test() throws Exception {
		ChEBIClient.getStructureSearchExample();
	}
	
	@Test
	public void searchInchiKey() throws Exception {
		ChEBIClient client = new ChEBIClient("InChIKey=SMQUZDBALVYZAC-UHFFFAOYAD",SearchCategory.INCHI_INCHI_KEY);
		while (client.hasNext()) {
			IStructureRecord struc = client.next();
		      System.out.println(struc.getContent());
		      for (Property p : struc.getProperties())
		    	  System.out.println(String.format("%s %s %s",p.getName(),p.getLabel(),struc.getProperty(p)));
		}
	}
	@Test
	public void searchInchi() throws Exception {
		ChEBIClient client = new ChEBIClient("InChI=1S/C6H8O6/c7-1-2(8)5-3(9)4(10)6(11)12-5/h2",SearchCategory.ALL);
		while (client.hasNext()) {
			IStructureRecord struc = client.next();
		      System.out.println(struc.getContent());
		      for (Property p : struc.getProperties())
		    	  System.out.println(String.format("%s %s %s",p.getName(),p.getLabel(),struc.getProperty(p)));
		}
	}
	@Test
	public void searchChebi() throws Exception {
		ChEBIClient client = new ChEBIClient("CHEBI:16008",SearchCategory.ALL);
		while (client.hasNext()) {
			IStructureRecord struc = client.next();
		      System.out.println(struc.getContent());
		      verify(struc);
		      /*
		      for (Property p : struc.getProperties())
		    	  System.out.println(String.format("%s %s %s %s %s",
		    			  p.getName(),p.getLabel(),p.getTitle(),p.getUrl(),struc.getProperty(p)));
		    			  */
		}
	}	
	
	protected void verify(IStructureRecord struc) throws Exception {
		String[][] s = { 
	{"CHEBI:Synonym3","http://www.opentox.org/api/1.1#ChemicalName","CHEBI","http://www.ebi.ac.uk/chebi","salicylal"},
	{"CHEBI:Star","http://www.opentox.org/api/1.1#InChIKey","CHEBI","http://www.ebi.ac.uk/chebi","3"},
	{"CHEBI:InChI","http://www.opentox.org/api/1.1#InChI","CHEBI","http://www.ebi.ac.uk/chebi","InChI=1/C7H6O2/c8-5-6-3-1-2-4-7(6)9/h1-5,9H"},
	{"CHEBI:IUPAC","NAME1","http://www.opentox.org/api/1.1#IUPACName","CHEBI","http://www.ebi.ac.uk/chebi","2-hydroxybenzaldehyde"},
	{"CHEBI:ID","http://www.opentox.org/api/1.1#ChemicalName","CHEBI","http://www.ebi.ac.uk/chebi","CHEBI:16008"},
	{"CHEBI:Synonym5","http://www.opentox.org/api/1.1#ChemicalName","CHEBI","http://www.ebi.ac.uk/chebi","Salicylaldehyde"},
	{"CHEBI:Synonym2","http://www.opentox.org/api/1.1#ChemicalName","CHEBI","http://www.ebi.ac.uk/chebi","o-Hydroxybenzaldehyde"},
	{"CHEBI:InChIKey","http://www.opentox.org/api/1.1#InChIKey","CHEBI","http://www.ebi.ac.uk/chebi","InChIKey=SMQUZDBALVYZAC-UHFFFAOYAD"},
	{"CHEBI:Name","http://www.opentox.org/api/1.1#ChemicalName","CHEBI","http://www.ebi.ac.uk/chebi","salicylaldehyde"},
	{"CHEBI:Synonym4","http://www.opentox.org/api/1.1#ChemicalName","CHEBI","http://www.ebi.ac.uk/chebi","Salicylaldehyd"},
	{"CHEBI:Synonym1","http://www.opentox.org/api/1.1#ChemicalName","CHEBI","http://www.ebi.ac.uk/chebi","o-formylphenol"},
	{"CHEBI:Synonym6","http://www.opentox.org/api/1.1#ChemicalName","CHEBI","http://www.ebi.ac.uk/chebi","Salizylaldehyd"},
	{"CHEBI:SMILES","http://www.opentox.org/api/1.1#SMILES","CHEBI","http://www.ebi.ac.uk/chebi","[H]C(=O)c1ccccc1O"}
		};
		
		for (int i=0; i < s.length; i++) {
			Property p = new Property(s[i][0],s[i][1],new LiteratureEntry(s[i][2],s[i][3]));
			Object value = struc.getProperty(p);
			if (value ==null) System.out.println(p);
			else
				Assert.assertEquals(s[i][4],value.toString());
		}
	}
	

}
