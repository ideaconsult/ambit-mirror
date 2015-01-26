package ambit2.base.data.study;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ambit2.base.json.JSONUtils;

/**
 * 
 * @author nina
 * 
 * @param <VALUE>
 * 
 *            Typical use: new Params<IValue>
 */
public class Params<VALUE> implements IParams<VALUE> {
    protected Map<String, VALUE> storage;

    public Params() {
	super();
	storage = new TreeMap<String, VALUE>();
    }

    @Override
    public int hashCode() {
	return storage.hashCode();
    }

    public Params(VALUE value) {
	this();
	setLoValue(value);
    }

    public Params(String key, VALUE value) {
	super();
	storage.put(key, value);
    }

    public VALUE getUnits() {
	return storage.get(_FIELDS_RANGE.unit.name());
    }

    public void setUnits(VALUE unit) {
	storage.put(_FIELDS_RANGE.unit.name(), unit);
    }

    public VALUE getLoValue() {
	return storage.get(_FIELDS_RANGE.loValue.name());
    }

    public VALUE getUpValue() {
	return storage.get(_FIELDS_RANGE.upValue.name());
    }

    public void setLoValue(VALUE value) {
	storage.put(_FIELDS_RANGE.loValue.name(), value);
    }

    public void setUpValue(VALUE value) {
	storage.put(_FIELDS_RANGE.upValue.name(), value);
    }

    public VALUE getUpQualifier() {
	return storage.get(_FIELDS_RANGE.upQualifier.name());
    }

    public VALUE getLoQualifier() {
	return storage.get(_FIELDS_RANGE.loQualifier.name());
    }

    public void setUpQualifier(VALUE qualifier) {
	storage.put(_FIELDS_RANGE.upQualifier.name(), qualifier);
    }

    public void setLoQualifier(VALUE qualifier) {
	storage.put(_FIELDS_RANGE.loQualifier.name(), qualifier);
    }

    @Override
    public String toString() {
	StringBuilder b = new StringBuilder();
	b.append("{");
	Iterator<String> keys = keySet().iterator();
	String comma = null;
	while (keys.hasNext()) {
	    if (comma != null)
		b.append(comma);
	    String key = keys.next();
	    b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(key)));
	    b.append(":");

	    VALUE value = get(key);
	    if (value == null)
		b.append("null");
	    else if (value instanceof IValue)
		b.append(value.toString());
	    else if (value instanceof IParams)
		b.append(value.toString());
	    else if (value instanceof Number)
		b.append(value);
	    else
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(value.toString())));
	    comma = ",";
	}
	b.append("}");
	return b.toString();
    }

    @Override
    public int size() {
	return storage.size();
    }

    @Override
    public boolean isEmpty() {
	return storage.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
	return storage.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
	return storage.containsValue(value);
    }

    @Override
    public VALUE get(Object key) {
	return storage.get(key);
    }

    @Override
    public VALUE put(String key, VALUE value) {
	return storage.put(key, value);
    }

    @Override
    public VALUE remove(Object key) {
	return storage.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends VALUE> m) {
	storage.putAll(m);

    }

    @Override
    public void clear() {
	storage.clear();
    }

    @Override
    public Set<String> keySet() {
	return storage.keySet();
    }

    @Override
    public Collection<VALUE> values() {
	return storage.values();
    }

    @Override
    public Set<java.util.Map.Entry<String, VALUE>> entrySet() {
	return storage.entrySet();
    }
}
