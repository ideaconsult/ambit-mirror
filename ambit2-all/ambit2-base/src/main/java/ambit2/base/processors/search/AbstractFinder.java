package ambit2.base.processors.search;

import java.util.Iterator;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.HttpException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;


public abstract class AbstractFinder<REQUEST,RESULT> extends DefaultAmbitProcessor<IStructureRecord, IStructureRecord>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2897055153094159861L;
	public enum MODE {
		emptyonly {
			@Override
			public String getDescription() {
				return "Lookup only empty structures and replace the current structure representation";
			}
		},
		replace {
			@Override
			public String getDescription() {
				return "Lookup all structures and replace the current structure representation";
			}
		},
		add {
			@Override
			public String getDescription() {
				return "Lookup all structures and add the result as additional structure representation";
			}
		},
		emptyadd {
			@Override
			public String getDescription() {
				return "Lookup only empty structures and add the result as additional structure representation";
			}
		},
		propertyonly {
			@Override
			public String getDescription() {
				return "Lookup all structures and add the results as properties only";
			}
		},		
/*
,
		importproperties {
			@Override
			public String getDescription() {
				return "Lookup all structures, retrieve and add properties";
			}
		}
*/
;		
		public String getDescription() {
			return toString();
		}
	}
	public enum SITE {
		CIR {
			@Override
			public String getTitle() {
				return "Chemical Identifier Resolver";
			}
			@Override
			public String getURI() {
				return "http://cactus.nci.nih.gov/chemical/structure";
			}
		},
		CHEMIDPLUS {
			@Override
			public String getTitle() {
				return "ChemIDplus";
			}
			@Override
			public String getURI() {
				return "http://chem.sis.nlm.nih.gov/chemidplus";
			}
		},
		PUBCHEM_NAME {
			@Override
			public String getTitle() {
				return "Search PubChem by name";
			}
			@Override
			public String getURI() {
				return "http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/%s/SDF";
			}
		},
		PUBCHEM_CID {
			@Override
			public String getTitle() {
				return "Search PubChem by CID";
			}
			@Override
			public String getURI() {
				return "http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/%s/SDF";
			}
		},		
		PUBCHEM_SID {
			@Override
			public String getTitle() {
				return "Search PubChem by SID";
			}
			@Override
			public String getURI() {
				return "http://pubchem.ncbi.nlm.nih.gov/rest/pug/substance/sid/%s/SDF";
			}
		},				
		PUBCHEM_INCHIKEY {
			@Override
			public String getTitle() {
				return "Search PubChem by InChIKey";
			}
			@Override
			public String getURI() {
				return "http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/inchikey/%s/SDF";
			}
		},		
		NAME2STRUCTURE {
			@Override
			public String getTitle() {
				return "Chemical Name to Structure converter (OPSIN) ";
			}
			@Override
			public String getURI() {
				return "http://www-ucc.ch.cam.ac.uk/products/software/opsin";
			}
			@Override
			public boolean isEnabled() {
				return true;
			}
		},		
		CHEBI {
			@Override
			public String getTitle() {
				return "Chemical Entities of Biological Interest (ChEBI)";
			}
			@Override
			public String getURI() {
				return "http://www.ebi.ac.uk/chebi";
			}
			@Override
			public boolean isEnabled() {
				return false;
			}
		},
		CHEMBL {
			@Override
			public String getTitle() {
				return "ChEMBL Web Services";
			}
			@Override
			public String getURI() {
				return "https://www.ebi.ac.uk/chembldb/index.php/ws/";
			}
		},		
		OPENTOX {
			@Override
			public String getTitle() {
				return "OpenTox";
			}
			@Override
			public String getURI() {
				return "http://apps.ideaconsult.net:8080/ambit2";
			}
			@Override
			public boolean isEnabled() {
				return true;
			}
		},
		TBWIKI {
			@Override
			public String getTitle() {
				return "ToxBank Wiki";
			}
			@Override
			public String getURI() {
				return "http://wiki.toxbank.net/wiki/Special:SPARQLEndpoint";
			}
			@Override
			public boolean isEnabled() {
				return true;
			}
		};		
		public String getTitle() {
			return toString();
		}		
		public String getURI() {
			return toString();
		}
		public boolean isEnabled() {return true; }
	}
	
	protected REQUEST request;
	protected Template profile;
	protected AbstractFinder.MODE mode;

	public AbstractFinder(Template profile,REQUEST request,AbstractFinder.MODE mode) {
		super();
		this.profile = profile;
		this.request = request;
		this.mode = mode;
	}
	public AbstractFinder.MODE getMode() {
		return mode;
	}
	public void setMode(AbstractFinder.MODE mode) {
		this.mode = mode;
	}

	protected abstract RESULT query(String value) throws AmbitException ;
	
	protected IStructureRecord transformResult(IStructureRecord record,IStructureRecord result) throws AmbitException {
		record.setContent(result.getContent());
		record.setFormat(result.getFormat());
		for (Property key : result.getProperties())
			record.setProperty(key, result.getProperty(key));
		return record;
	}
	
	protected IStructureRecord transformResult(IStructureRecord record,String result) throws AmbitException {
		record.setContent(result);
		record.setFormat(MOL_TYPE.SDF.toString());
		return record;
	}

	protected IStructureRecord transform(IStructureRecord record,RESULT result) throws AmbitException {
		if (result instanceof IStructureRecord) return transformResult(record, (IStructureRecord) result);
		else return transformResult(record, result.toString());
	}
	
	protected Object retrieveValue(IStructureRecord target, Property key) throws AmbitException {
		 return target.getProperty(key);
	}
	
	@Override
	public IStructureRecord process(IStructureRecord target) throws AmbitException {
		try {
			if (target ==null) return null;
			if ((AbstractFinder.MODE.emptyonly.equals(mode) || AbstractFinder.MODE.emptyadd.equals(mode)) 
					&& !STRUC_TYPE.NA.equals(target.getType())) return null;
			
			Iterator<Property> keys = profile.getProperties(true);
			Object value = null;
			while (keys.hasNext()) {
				Property key = keys.next();
				try {
					value = retrieveValue(target, key);
					if ((value==null) || "".equals(value.toString().trim())) continue;
					RESULT content = query(value.toString());
					if (content!= null) { 
						STRUC_TYPE originalType = target.getType();
						target = transform(target,content);
						
						switch (mode) {

						case emptyonly: {
							if (!STRUC_TYPE.NA.equals(originalType)) {
								target.setIdstructure(-1) ;
							}							
							break;
						}
						case emptyadd: {
							target.setIdstructure(-1) ;
							target.setUsePreferedStructure(false);
							break;							
						}
						case replace: {
							target.setUsePreferedStructure(false);
							break;
						}	
						case propertyonly: {
							break;
						}
						default:  //add
							target.setIdstructure(-1) ;
							target.setUsePreferedStructure(false);
							break;
						}
						return target;
					}
				} catch (HttpException x) {
					if (x.getCode()==404) 
						logger.log(Level.WARNING,value==null?x.getMessage():value.toString(),x);
				} catch (Exception x) {
					logger.log(Level.SEVERE,value==null?x.getMessage():value.toString(),x);
				}
			}
			
		} catch (Exception x) {
			logger.log(Level.SEVERE,x.getMessage(),x);
		}
		return null;
	}
}
