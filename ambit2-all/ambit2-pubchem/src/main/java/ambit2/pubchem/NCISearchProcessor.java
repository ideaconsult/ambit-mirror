package ambit2.pubchem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.index.CASNumber;

import ambit2.base.data.Property;
import ambit2.base.processors.ProcessorException;


/**
The general format of the URLS are

http://cactus.nci.nih.gov/chemical/structure/{identifier}/{method}

Allowed identifier values are: SMILES, Std. InChI/InChIKey (~50 million InChIKeys are available for lookup), chemical names (~70 million), NCI/CADD Identifier (FICuS, uuuuu), RN numbers

Methods are currently /smiles, /inchi, /inchikey, /names,/image, /ficus, /ficts, and /uuuuu but there is more to come
 * @author nina
 *
 */
public class NCISearchProcessor extends HTTPRequest<String, String>  {
	public enum METHODS {
		all {
			@Override
			public String[] getOpenToxEntry() {
				return new String[] {Property.opentox_CAS,Property.opentox_EC,Property.opentox_IupacName, Property.opentox_Name,Property.opentox_SMILES,Property.opentox_InChI_std,Property.opentox_InChIKey_std,Property.opentox_REACHDATE,Property.opentox_IUCLID5_UUID,Property.opentox_TradeName};
			}			
		},
		smiles {
			@Override
			public String[] getOpenToxEntry() {
				return new String[] {Property.opentox_SMILES};
			}
		},
		reach {
			@Override
			public String[] getOpenToxEntry() {
				
				return new String[] {Property.opentox_CAS,Property.opentox_EC,Property.opentox_REACHDATE,Property.opentox_IUCLID5_UUID,Property.opentox_InChI_std,Property.opentox_IupacName};
			}
		},		
		stdinchi {
			@Override
			public String[] getOpenToxEntry() {
				return new String[] {Property.opentox_InChI_std};
			}
		},
		stdinchikey {
			@Override
			public String[] getOpenToxEntry() {
				return new String[] {Property.opentox_InChIKey_std};
			}
		},			
		names {
			@Override
			public String[] getOpenToxEntry() {
				return new String[] {Property.opentox_IupacName, Property.opentox_Name,Property.opentox_CAS,Property.opentox_EC,Property.opentox_TradeName};
			}
		},
		iupac_name {
			@Override
			public String[] getOpenToxEntry() {
				return new String[] {Property.opentox_IupacName};
			}
		},		
		synonym {
			@Override
			public String[] getOpenToxEntry() {
				return new String[] {Property.opentox_Name};
			}
		},		
		
		cas {
			@Override
			public String[] getOpenToxEntry() {
				return new String[] {Property.opentox_CAS};
			}
		},		
		einecs {
			@Override
			public String[] getOpenToxEntry() {
				return new String[] {Property.opentox_EC};
			}
		},	
		
		image,
		ficus,
		ficts,
		uuuuu,
		sdf {
			@Override
			public String getMediaType() {
				return "chemical/x-mdl-sdfile";
			}
		},
		hashisy,
		dblinks {
			@Override
			public String[] getOpenToxEntry() {
				return new String[] {Property.opentox_ChEBI,Property.opentox_Pubchem, Property.opentox_ChEMBL, Property.opentox_ChemSpider,Property.opentox_CMS,Property.opentox_ToxbankWiki};
			}
		},
		allnlinks {
			@Override
			public String[] getOpenToxEntry() {
				return new String[] {Property.opentox_CAS,Property.opentox_EC,Property.opentox_IupacName, Property.opentox_Name,Property.opentox_SMILES,Property.opentox_InChI_std,Property.opentox_InChIKey_std,Property.opentox_REACHDATE,Property.opentox_IUCLID5_UUID,Property.opentox_ChEBI,Property.opentox_Pubchem, Property.opentox_ChEMBL,Property.opentox_ChemSpider,Property.opentox_CMS,Property.opentox_ToxbankWiki};
			}			
		};
		public String getMediaType() {
			return "text/plain";
		}
		
		public String[] getOpenToxEntry() {return null; }
		
		};
	protected long wait_ms = 0;

	public long getWait_ms() {
		return wait_ms;
	}
	public  void setWait_ms(long wait_ms) {
		this.wait_ms = wait_ms;
	}

	protected String nci_url = "%s/chemical/structure/%s/%s";
	protected String host = "http://cactus.nci.nih.gov";
		public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}

	//e.g. http://cactus.nci.nih.gov/chemical/structure/50-00-0/smiles
	/**
	 * 
	 */
	protected METHODS option = METHODS.smiles;
	public METHODS getOption() {
		return option;
	}
	public void setOption(METHODS option) {
		this.option = option;
	}

	private static final long serialVersionUID = 1503961338542812463L;
	public NCISearchProcessor() {
		super();
		maxretry = 1;
	}
	@Override
	protected String parseInput(String target, InputStream in)
			throws ProcessorException {
		StringBuilder builder = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine())!=null) {
				builder.append(line);
				builder.append((getOption().equals(METHODS.sdf))?'\n':'\t');
			}
			return builder.toString();
		} catch (Exception x) {
			throw new ProcessorException(this,x);
		}
		
	}
	@Override
	public String process(String target) throws AmbitException {
		METHODS method = getOption(); 
		//retrieve SDF file
		if (METHODS.sdf.equals(method)) {
			setOption(method);
			try {
				setUrl(String.format(nci_url, getHost(),URLEncoder.encode(target, "US-ASCII"),getOption()));
				if (wait_ms > 0) {
					double w = wait_ms * (0.5 + Math.random());
					Thread.sleep((long)w);
				}
				StringBuilder b = new StringBuilder();
				b.append(super.process(target));
				
				if (CASNumber.isValid(target)) 
					return b.toString().replace("$$$$",String.format("\n> <CAS>\n%s\n\n$$$$",target)).trim();
				else 
					return b.toString();
				
			} catch (FileNotFoundException x) {
				throw x;
			} catch (AmbitException x) {
				throw x;
			} catch (Exception x) {
				throw new AmbitException(x);
			}
		} else {  //anything else
			StringBuilder b = new StringBuilder();
			b.append(target);
			b.append("\t");
			METHODS[] methods = new METHODS[]{METHODS.smiles,METHODS.stdinchi,METHODS.stdinchikey,METHODS.names};
			for (METHODS m : methods) 
				if (METHODS.all.equals(m)) continue;
				else if ((method==null) || method.equals(METHODS.all) || method.equals(m)) {
					setOption(m);
					try {
						setUrl(String.format(nci_url, getHost(),URLEncoder.encode(target, "US-ASCII"),getOption()));
						if (wait_ms > 0) {
							double w = wait_ms * (0.5 + Math.random());
							Thread.sleep((long)w);
						}
						b.append(super.process(target));
					} catch (Exception x) {
						b.append("");
						//if (isCancelled())	throw new AmbitException(x);
					} finally {
					}
				} else {
					b.append("\t");
				}
			setOption(method);
			return b.toString();			
		}
	}
	@Override
	protected void prepareOutput(String target, OutputStream out)
			throws ProcessorException {
	}


}
