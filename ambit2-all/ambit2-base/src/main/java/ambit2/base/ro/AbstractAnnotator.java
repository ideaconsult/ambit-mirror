package ambit2.base.ro;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;

import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import net.idea.modbcum.p.DefaultAmbitProcessor;

public class AbstractAnnotator<U> extends DefaultAmbitProcessor<U, U> {
	protected Properties endpoints_lookup;
	protected boolean fulllinks = true;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1938797533321541056L;

	public AbstractAnnotator(File lookupfolder) {
		this(lookupfolder, true);
	}

	public AbstractAnnotator(File lookupfolder, boolean fulllinks) {
		super();
		this.fulllinks = fulllinks;
		try {
			endpoints_lookup = new Properties();
			endpoints_lookup.load(new FileInputStream(new File(lookupfolder, "endpoint.properties")));
		} catch (Exception x) {
			endpoints_lookup = null;
			logger.log(Level.WARNING, lookupfolder.getAbsolutePath(), x);
		}
	}

	@Override
	public U process(U target) throws Exception {
		return target;
	}

	public String[] annotate(String endpoint) throws Exception {
		String[] terms = endpoints_lookup.getProperty(endpoint.toUpperCase().replaceAll(" ", "_")).split(";");
		if (!fulllinks)
			for (int i=0; i < terms.length; i++) 
				terms[i] = ontolink2id(terms[i]);
			
		return terms;
	}

	protected void annotate(ProtocolApplication<Protocol, IParams, String, IParams, String> papp) throws Exception {

	}

	public void annotate(EffectRecord<String, IParams, String> effect) throws Exception {
		String[] terms = annotate(effect.getEndpoint());

		for (String term : terms) {
			effect.addEndpointSynonym(term);
		}

	}

	public static String ontolink2id(String ontolink) {
		if (ontolink.startsWith("http")) {
			String[] terms = null;
			if (ontolink.indexOf("#")>0) 
				terms = ontolink.split("#");
			else
				terms = ontolink.split("/");
			return terms[terms.length-1];
		} else	
			return ontolink;
	}
}
