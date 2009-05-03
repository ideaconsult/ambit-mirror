package ambit2.base.data;

public class PropertyTemplateStats extends PropertyStats {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7523679884610239776L;
	public PropertyTemplateStats() {

	}
	public PropertyTemplateStats(Property p, String template, long count) {
		setProperty(p);
		setTemplate(template);
		setCount(count);
	}
	
	protected String template;
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public Property getProperty() {
		return property;
	}
	public void setProperty(Property property) {
		this.property = property;
	}

	protected Property property;

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getTemplate());
		b.append("\t");
		b.append(getProperty());
		b.append("\t[");
		b.append(getCount());
		b.append("]");
		return b.toString();
	}
}
