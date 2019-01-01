package ambit2.rest.dataset;

import java.util.Collections;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.processors.IProcessor;

import org.restlet.Request;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.data.study.MultiValue;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.reporters.ARFFReporter;
import ambit2.rest.property.PropertyURIReporter;

/**
 * Extension of {@link ARFFReporter} using URIs instead of names
 * @author nina
 *
 * @param <Q>
 */
public class ARFFResourceReporter<Q extends IQueryRetrieval<IStructureRecord>> extends ARFFReporter<Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2627930277795024333L;
	protected PropertyURIReporter reporter;

	public ARFFResourceReporter(Template template, Request request, String urlPrefix) {
		this(template,null,request,urlPrefix);
	}
	public ARFFResourceReporter(Template template,Profile groupedProperties, Request request, String urlPrefix) {
		this(template,groupedProperties,request,urlPrefix,(IProcessor[]) null);
	}
	
	public ARFFResourceReporter(Template template,Profile groupedProperties,Request request, String urlPrefix,IProcessor... processors) {
		super(template,groupedProperties,processors);
		setUrlPrefix(urlPrefix);
		reporter = new PropertyURIReporter(request);
	}
	@Override
	protected String getRelationName() {
		return reporter.getResourceRef().toString();
	}
	
	@Override
	protected String getPropertyHeader(Property p) {
		StringBuilder allowedValues = null; 
		if (p.isNominal()) {
			
			if (p.getAllowedValues()==null) {
				//smth wrong
			} else {
				try { Collections.sort(p.getAllowedValues()); } catch (Exception x) {}
				for (Comparable value: p.getAllowedValues()) {
					if (allowedValues==null) {
						allowedValues = new StringBuilder();
						allowedValues.append("{");
					}
					else allowedValues.append(","); 
					allowedValues.append(value);
				}
				allowedValues.append("}");
			}
		}
		
		return 
		String.format("@attribute %s %s\n", 
				reporter.getURI(p),
				allowedValues!=null?allowedValues.toString():(p.getClazz().equals(Number.class) || p.getClazz().equals(MultiValue.class))?"numeric":"string");
	}
}
