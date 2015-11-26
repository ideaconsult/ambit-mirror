package ambit2.chebi;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import uk.ac.ebi.chebi.webapps.chebiWS.client.ChebiWebServiceClient;
import uk.ac.ebi.chebi.webapps.chebiWS.model.ChebiWebServiceFault_Exception;
import uk.ac.ebi.chebi.webapps.chebiWS.model.DataItem;
import uk.ac.ebi.chebi.webapps.chebiWS.model.Entity;
import uk.ac.ebi.chebi.webapps.chebiWS.model.LiteEntity;
import uk.ac.ebi.chebi.webapps.chebiWS.model.LiteEntityList;
import uk.ac.ebi.chebi.webapps.chebiWS.model.SearchCategory;
import uk.ac.ebi.chebi.webapps.chebiWS.model.StarsCategory;
import uk.ac.ebi.chebi.webapps.chebiWS.model.StructureDataItem;
import uk.ac.ebi.chebi.webapps.chebiWS.model.StructureSearchCategory;
import uk.ac.ebi.chebi.webapps.chebiWS.model.StructureType;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;

/**
 * http://www.ebi.ac.uk/chebi/webServices.do
 * @author nina
 *
 */
public class ChEBIClient implements Iterator<IStructureRecord>{
	protected static Logger logger = Logger.getLogger(ChEBIClient.class.getName());
	 protected IStructureRecord structure;
	 protected ChebiWebServiceClient client = new ChebiWebServiceClient();
	 protected  LiteEntityList entities ;
	 protected List<LiteEntity> resultList;
	 protected int record = -1;
	 
	 public static Property getChebiProperty(String name, String alias) {
		 Property p =  Property.getInstance(name, "CHEBI", "http://www.ebi.ac.uk/chebi");
		 p.setLabel(alias);
		 return p;
	 }
	 public ChEBIClient(String search, SearchCategory searchCategory) throws ChebiWebServiceFault_Exception {
		 structure = new StructureRecord();

		 entities = client.getLiteEntity(search, searchCategory, 50, StarsCategory.ALL);

	}
		 
	  public static void getStructureSearchExample (){ //fails

		    try {

		      // Create client
		      ChebiWebServiceClient client = new ChebiWebServiceClient();
		      logger.info("Invoking getStructureSearch");
		      String water =
		      " Marvin 02220718252D " +
		      " " +
		      " 3 2 0 0 0 0 999 V2000 " +
		      " -0.4125 0.7145 0.0000 H 0 0 0 0 0 0 0 0 0 0 0 0 " +
		      " 0.0000 0.0000 0.0000 O 0 0 0 0 0 0 0 0 0 0 0 0 " +
		      " -0.4125 -0.7145 0.0000 H 0 0 0 0 0 0 0 0 0 0 0 0 " +
		      " 2 1 1 0 0 0 0 " +
		      " 2 3 1 0 0 0 0 " +
		      " M END ";
		      LiteEntityList entities = client.getStructureSearch(water, StructureType.MOLFILE, StructureSearchCategory.SIMILARITY, 50, 0.70F);
		      for ( LiteEntity liteEntity : entities.getListElement()) {
		    	  logger.info("CHEBI ID: " + liteEntity.getChebiId());
		      }

		    } catch ( ChebiWebServiceFault_Exception e ) {
		    	logger.severe(e.getMessage());
		    }
		  }
	  
	@Override
	public boolean hasNext() {
		record++;
		return record < entities.getListElement().size();
	}

	@Override
	public IStructureRecord next() {
		try {
			return entry2structurerecord(entities.getListElement().get(record),structure);
		} catch (Exception x) {
			return null;
		}
	}

	@Override
	public void remove() {
	}
	
	public IStructureRecord entry2structurerecord(LiteEntity chebiEntity,IStructureRecord struc) throws  ChebiWebServiceFault_Exception {

	      if (struc == null) struc = new StructureRecord();	
	      else struc.clear();
	      // Create client
	      ChebiWebServiceClient client = new ChebiWebServiceClient();
	      Entity entity = client.getCompleteEntity(chebiEntity.getChebiId());
	      
	      List<StructureDataItem> list = entity.getChemicalStructures();
	      for (StructureDataItem item: list) 
	    	  if (item.isDefaultStructure()) {
	    		 struc.setContent(item.getStructure());
	    		 struc.setFormat(MOL_TYPE.SDF.toString());
	    	  }
	      struc.setRecordProperty(getChebiProperty("CHEBI_ID", "http://www.ebi.ac.uk/chebi/id"), entity.getChebiId());
	      struc.setRecordProperty(getChebiProperty("Name", Property.opentox_Name), entity.getChebiAsciiName());
	      struc.setRecordProperty(getChebiProperty("InChI", Property.opentox_InChI), entity.getInchi());
	      struc.setRecordProperty(getChebiProperty("InChIKey", Property.opentox_InChIKey), entity.getInchiKey());
	      struc.setRecordProperty(getChebiProperty("CHEBI:Star", Property.opentox_InChIKey), entity.getEntityStar());

	      struc.setRecordProperty(getChebiProperty("SMILES", Property.opentox_SMILES), entity.getSmiles());
	      
	      List<DataItem> iupacnames = entity.getIupacNames();  //List all synonyms
	      for (int i=0; i < iupacnames.size();i++) 
		    struc.setRecordProperty(getChebiProperty(String.format("IUPAC Name%d",i+1), Property.opentox_IupacName),  iupacnames.get(i).getData());
	      
	      
	      List<DataItem> synonyms = entity.getSynonyms();  //List all synonyms
	      for (int i=0; i < synonyms.size();i++) 
		    struc.setRecordProperty(getChebiProperty(String.format("Synonym%d",i+1), Property.opentox_Name),  synonyms.get(i).getData());


	      return struc;

	  }
}
