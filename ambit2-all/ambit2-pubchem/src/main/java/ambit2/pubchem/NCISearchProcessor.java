package ambit2.pubchem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;

import ambit2.base.exceptions.AmbitException;
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
	protected enum METHODS {all,smiles,inchi,inchikey,names,image,ficus,ficts,uuuuu};
	protected long wait_ms = 0;
	protected double random = 1;
	
	public double getRandom() {
		return random;
	}
	public void setRandom(double random) {
		this.random = random;
	}
	public long getWait_ms() {
		return wait_ms;
	}
	public  void setWait_ms(long wait_ms) {
		this.wait_ms = wait_ms;
	}

	protected static final String nci_url = "http://cactus.nci.nih.gov/chemical/structure/%s/%s";
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
			}
			return builder.toString();
		} catch (Exception x) {
			throw new ProcessorException(this,x);
		}
		
	}
	@Override
	public String process(String target) throws AmbitException {
		METHODS method = getOption(); 
		
		StringBuilder b = new StringBuilder();
		b.append(target);
		b.append("\t");
		METHODS[] methods = new METHODS[]{METHODS.smiles,METHODS.inchi,METHODS.inchikey};
		for (METHODS m : methods) 
			if (METHODS.all.equals(m)) continue;
			else if ((method==null) || method.equals(METHODS.all) || method.equals(m)) {
				setOption(m);
				try {
					setUrl(String.format(nci_url, URLEncoder.encode(target, "US-ASCII"),getOption()));
					if (wait_ms > 0) {
						double w = wait_ms*Math.random()*random;
						Thread.sleep((long)w);
					}
					b.append(super.process(target));
				} catch (Exception x) {
					b.append("");
				} finally {
				}
				b.append("\t");
			} else {
				b.append("\t");b.append("\t");
			}
		setOption(method);
		return b.toString();

	}
	@Override
	protected void prepareOutput(String target, OutputStream out)
			throws ProcessorException {
	}


}
