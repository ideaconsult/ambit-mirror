package ambit2.db.substance.study.facet;

import java.io.File;

import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.ro.AbstractAnnotator;
import net.idea.modbcum.i.facet.IFacet;

public class FacetAnnotator extends AbstractAnnotator<IFacet,IFacet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6993550791423851737L;

	public FacetAnnotator(File lookupfolder) {
		super(lookupfolder);
	}

	@Override
	public IFacet process(IFacet target) throws Exception {
		if (target instanceof ResultsCountFacet)
			try {
				Object o = ((ResultsCountFacet) target).getResult();
				if (o instanceof EffectRecord)
					annotate((EffectRecord) o);
				else if (o instanceof ProtocolApplication)
					annotate((ProtocolApplication) o);
			} catch (Exception x) {
				// ignore
			}
		return target;
	}
	
	public String[] annotate(EffectRecord<String, IParams, String> effect) throws Exception {
		String[] terms = annotate(lookup.get(_dictionaries.endpoints.name()), effect.getEndpoint());
		if (terms != null)
			for (String term : terms) {
				effect.addEndpointSynonym(term);
			}
		return terms;

	}
	
	protected void annotate(ProtocolApplication<Protocol, IParams, String, IParams, String> papp) throws Exception {

	}
	
}
