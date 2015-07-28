package ambit2.rest.bundle;

import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.restlet.Request;

import ambit2.base.data.study.Protocol._categories;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.facet.BundleRoleFacet;
import ambit2.db.facets.bundle.EndpointRoleByBundle;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.substance.study.facet.SubstanceByCategoryFacet;
import ambit2.rest.facet.FacetJSONReporter;

public class BundleStudyJSONReporter<Q extends IQueryRetrieval<IFacet>> extends
		FacetJSONReporter<Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5511031336207765476L;
	protected SubstanceEndpointsBundle bundle;
	protected boolean includesSupportingCategory = false;
	protected boolean mergeDatasets = false;

	public BundleStudyJSONReporter(Request baseRef, String jsonp) {
		this(baseRef, jsonp, null,false);
	}

	public BundleStudyJSONReporter(Request request, String jsonp,
			SubstanceEndpointsBundle bundle,boolean mergeDatasets) {
		super(request, jsonp);
		this.mergeDatasets = mergeDatasets;

		if (bundle != null) {
			getProcessors().clear();
			EndpointRoleByBundle q = new EndpointRoleByBundle(request
					.getRootRef().toString());
			q.setValue(bundle);
			MasterDetailsProcessor<IFacet, BundleRoleFacet, IQueryCondition> bundleReader = new MasterDetailsProcessor<IFacet, BundleRoleFacet, IQueryCondition>(
					q) {
				/**
			     * 
			     */
				private static final long serialVersionUID = -135061244120269844L;

				@Override
				public IFacet process(IFacet master) throws AmbitException {
					if (master instanceof SubstanceByCategoryFacet) {
						((SubstanceByCategoryFacet) master).setBundleRole(null);
					}
					return super.process(master);
				}

				@Override
				protected IFacet processDetail(IFacet master,
						BundleRoleFacet detail) throws Exception {
					if (master instanceof SubstanceByCategoryFacet) {
						((SubstanceByCategoryFacet) master)
								.setBundleRole(detail);
					}
					return master;
				}
			};
			bundleReader.setCloseConnection(false);
			getProcessors().add(bundleReader);
			getProcessors().add(new DefaultAmbitProcessor<IFacet, IFacet>() {
				/**
			     * 
			     */
				private static final long serialVersionUID = -3911776354159473349L;

				public IFacet process(IFacet target) throws AmbitException {
					processItem(target);
					return target;
				};
			});
		}
	}

	@Override
	public Object processItem(IFacet item) throws AmbitException {
		Object o = super.processItem(item);
		if (o instanceof SubstanceByCategoryFacet) {
			if (_categories.SUPPORTING_INFO_SECTION
					.equals(((SubstanceByCategoryFacet) o).getEndpoint()))
				includesSupportingCategory = true;
		}
		return o;
	}

	@Override
	public void footer(Writer output, Q query) {

		if (mergeDatasets && !includesSupportingCategory) try {
			SubstanceByCategoryFacet item = new SubstanceByCategoryFacet(uriReporter.getBaseReference().toString());
			item.setValue("SUPPORTING_INFO_SECTION");
			item.setSubcategoryTitle("TOX");
			item.setSubstancesCount(-1);
			super.processItem(item);
		} catch (Exception x) {
			logger.log(Level.INFO,x.getMessage());
		}
		
		super.footer(output, query);
	}

}
