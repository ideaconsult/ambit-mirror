package ambit2.rest.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryResource;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.substance.FacetedSearchSubstance;
import ambit2.db.substance.ReadByReliabilityFlags;
import ambit2.db.substance.ReadSubstance;
import ambit2.db.substance.ReadSubstanceByExternalIDentifier;
import ambit2.db.substance.ReadSubstanceByName;
import ambit2.db.substance.ReadSubstanceByOwner;
import ambit2.db.substance.ReadSubstanceByStudy;
import ambit2.db.update.bundle.substance.ReadSubstancesByBundleCompounds;
import ambit2.rest.OpenTox;

public class SubstanceLookup<Q extends IQueryRetrieval<SubstanceRecord>, T extends SubstanceRecord> extends
	SubstanceResource<Q, T> {
    protected enum _query_type {
	related, reference, facet, study
    };

    protected enum _query_subtype {
	protocol, experiment, owner
    };

    protected enum _query_protocol_application {
	uuid, name, externalid, studyResultType, topcategory, reliability, purposeFlag, isRobustStudy
    };

    public SubstanceLookup() {
	super();
	setHtmlbyTemplate(true);
    }

    @Override
    protected Representation post(Representation entity, Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected Representation post(Representation entity) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected Representation put(Representation entity, Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected Representation put(Representation representation) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected Representation delete(Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected Representation delete() throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected Q createQuery(Context context, Request request, Response response) throws ResourceException {
	_query_type type = null;
	try {
	    type = _query_type.valueOf(request.getAttributes().get("type").toString());
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}

	Form form = getRequest().getResourceRef().getQueryAsForm();
	try {

	    Object bundleURI = OpenTox.params.bundle_uri.getFirstValue(form);
	    Integer idbundle = bundleURI == null ? null : getIdBundle(bundleURI, request);
	    SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(idbundle);
	    bundles = new SubstanceEndpointsBundle[1];
	    bundles[0] = bundle;
	} catch (Exception x) {
	    bundles = null;
	}

	Object cmpURI = OpenTox.params.compound_uri.getFirstValue(form);
	Integer idchemical = cmpURI == null ? null : getIdChemical(cmpURI, request);
	Object bundleURI = form.getFirstValue("filterbybundle");
	Integer idbundle = bundleURI == null ? null : getIdBundle(bundleURI, request);

	String search = form.getFirstValue(QueryResource.search_param);

	switch (type) {
	case related: {
	    /**
	     * /query/substance/related
	     */
	    if (idchemical != null) {
		CompositionRelation composition = new CompositionRelation(null, new StructureRecord(idchemical, -1,
			null, null), null);
		return (Q) new ReadSubstance(composition);
	    }
	    if (idbundle != null) {
		SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(idbundle);
		bundles = new SubstanceEndpointsBundle[] { bundle };
		return (Q) new ReadSubstancesByBundleCompounds(bundle);
	    }
	    break;
	}
	case reference: {
	    /**
	     * /query/substance/reference
	     */
	    if (idchemical != null) {
		SubstanceRecord substance = new SubstanceRecord();
		substance.setIdchemical(idchemical);
		return (Q) new ReadSubstance(substance);
	    }
	    if (idbundle != null) {
		SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(idbundle);
		bundles = new SubstanceEndpointsBundle[] { bundle };
		return (Q) new ReadSubstancesByBundleCompounds(bundle);
	    }
	    break;
	}
	case facet: {
	    /**
	     * /query/substance/facet
	     */
	    List<ProtocolApplication<Protocol, Params, String, Params, String>> protocols = new ArrayList<ProtocolApplication<Protocol, Params, String, Params, String>>();
	    for (String value : form.getValuesArray("category"))
		try {
		    String[] categories = value.split("\\.");
		    Protocol protocol = new Protocol(null);
		    protocol.setCategory(Protocol._categories.valueOf(categories[1]).name());
		    protocol.setTopCategory(categories[0]);
		    ProtocolApplication<Protocol, Params, String, Params, String> papp = new ProtocolApplication<Protocol, Params, String, Params, String>(
			    protocol);
		    protocols.add(papp);
		    String effectEndpoint = form.getFirstValue("endpoint." + value);
		    String effectloValue = form.getFirstValue("lovalue." + value);
		    String effectupValue = form.getFirstValue("upvalue." + value);
		    String effectloQualifier = form.getFirstValue("loqlf." + value);
		    String effectupQualifier = form.getFirstValue("upqlf." + value);
		    String units = form.getFirstValue("unit." + value);
		    String interpretationResult = form.getFirstValue("iresult." + value);

		    final String[] qlfs = new String[] { "<=", "<", ">=", ">", "=" };
		    EffectRecord<String, Params, String> effect = null;
		    if (effectEndpoint != null) {
			effect = new EffectRecord<String, Params, String>();
			papp.addEffect(effect);
			effect.setEndpoint(effectEndpoint);
		    }
		    if (effectloValue != null) {
			if (effect == null) {
			    effect = new EffectRecord<String, Params, String>();
			    papp.addEffect(effect);
			}
			try {
			    effect.setLoValue(Double.parseDouble(effectloValue));
			} catch (Exception x) {
			    effect.setTextValue(effectloValue);
			}
			effect.setLoQualifier(">=");
			for (String qlf : qlfs) {
			    if (qlf.equals(effectloQualifier)) {
				effect.setLoQualifier(effectloQualifier);
				break;
			    }
			}

		    }
		    if (effectupValue != null) {
			if (effect == null) {
			    effect = new EffectRecord<String, Params, String>();
			    papp.addEffect(effect);
			}
			try {
			    effect.setUpValue(Double.parseDouble(effectupValue));
			} catch (Exception x) {
			}
			effect.setUpQualifier("<=");
			for (String qlf : qlfs) {
			    if (qlf.equals(effectupQualifier)) {
				effect.setUpQualifier(effectupQualifier);
				break;
			    }
			}
		    }
		    if (units != null) {
			if (effect == null) {
			    effect = new EffectRecord<String, Params, String>();
			    papp.addEffect(effect);
			}
			effect.setUnit(units);
		    }

		    if (interpretationResult != null) {
			papp.setInterpretationResult(interpretationResult);
		    }
		} catch (IllegalArgumentException x) {
		    // invalid category, ignoring
		} catch (Exception x) {
		    // ignoring
		}
	    if (protocols.size() > 0) {
		FacetedSearchSubstance q = new FacetedSearchSubstance();
		q.setFieldname(protocols);
		return (Q) q;
	    }
	    break;
	}
	case study: {
	    /**
	     * /query/substance/study
	     */
	    _query_subtype subtype = null;
	    try {
		subtype = _query_subtype.valueOf(request.getAttributes().get("subtype").toString());
	    } catch (Exception x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	    }

	    Object subsubtype = request.getAttributes().get("subsubtype").toString();
	    switch (subtype) {
	    case protocol: {
		/**
		 * search by study /query/substance/study/protocol/citation
		 * /query/substance/study/protocol/guideline
		 * /query/substance/study/protocol/topcategory
		 * /query/substance/study/protocol/endpointcategory
		 * /query/substance/study/protocol/params
		 */
		try {
		    ReadSubstanceByStudy._studysearchmode byStudy = null;
		    byStudy = ReadSubstanceByStudy._studysearchmode.valueOf(subsubtype.toString());
		    return (Q) new ReadSubstanceByStudy(byStudy, search);
		} catch (Exception x) {
		}
		break;
	    }
	    case experiment: {
		/**
		 * search by protocol application flags
		 * /query/substance/study/experiment
		 */
		try {
		    _query_protocol_application byProtocolApplicationFlags = null;
		    byProtocolApplicationFlags = _query_protocol_application.valueOf(subsubtype.toString());
		    if (search != null)
			switch (byProtocolApplicationFlags) {
			case name: {
			    return (Q) new ReadSubstanceByName(byProtocolApplicationFlags.name(), search);
			}
			case uuid: {
			    SubstanceRecord record = new SubstanceRecord();
			    record.setCompanyUUID(search.trim());
			    return (Q) new ReadSubstance(record);
			}
			case studyResultType: {
			    return (Q) new ReadByReliabilityFlags(byProtocolApplicationFlags.name(), search);
			}
			case reliability: {
			    return (Q) new ReadByReliabilityFlags(byProtocolApplicationFlags.name(), search);
			}
			case purposeFlag: {
			    return (Q) new ReadByReliabilityFlags(byProtocolApplicationFlags.name(), search);
			}
			case isRobustStudy: {
			    return (Q) new ReadByReliabilityFlags(byProtocolApplicationFlags.name(), search);
			}
			case externalid: {
			    return (Q) new ReadSubstanceByExternalIDentifier(byProtocolApplicationFlags.name(), search);
			}
			default: {
			    break;
			}
			}
		} catch (Exception x) {
		}
		break;
	    }
	    case owner: {
		/**
		 * search by owner /query/substance/study/owner/name
		 * /query/substance/study/owner/uuid
		 */
		try {
		    ReadSubstanceByOwner._ownersearchmode byOwner = null;
		    byOwner = ReadSubstanceByOwner._ownersearchmode.valueOf(subsubtype.toString());
		    return (Q) new ReadSubstanceByOwner(byOwner, search);
		} catch (Exception x) {
		}
		break;
	    }
	    }

	    break;
	}
	}
	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
    }
}
