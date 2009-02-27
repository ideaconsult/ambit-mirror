/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.process;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.microworkflow.events.ObjectWithPropertyChangeSupport;
import com.microworkflow.events.WorkflowContextEvent;

public class WorkflowContext<V> extends ObjectWithPropertyChangeSupport implements Map<String, V> {
	protected HashMap<String,V> context=new HashMap<String,V>();
	protected String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object get(String key) {
		return context.get(key);
	}
    
	public V put(String key, V value) {
        Object oldValue = context.get(key);
        V v = null;
        if (value != null)
        	v = context.put(key,value);
        else
        	context.remove(key);
        firePropertyChange(new WorkflowContextEvent(this,key, oldValue, value));
        return v;
	}
	public boolean containsKey(String key) {
		return context.containsKey(key);
	}
    public void clear() {
        context.clear();
        
    }
    public boolean containsKey(Object key) {
        return context.containsKey(key);
    }
    public boolean containsValue(Object value) {
        return context.containsValue(value);
    }
    public Set<java.util.Map.Entry<String, V>> entrySet() {
        return context.entrySet();
    }
    public V get(Object arg0) {
        return context.get(arg0);
    }
    public boolean isEmpty() {
        return context.isEmpty();
    }
    public Set<String> keySet() {
        return context.keySet();
    }

    public void putAll(Map<? extends String, ? extends V> arg0) {
        context.putAll(arg0);
        
    }
    public V remove(Object arg0) {
        return context.remove(arg0);
    }
    public int size() {
        return context.size();
    }
    public Collection<V> values() {
        return context.values();
    }
    public void fireAnimateEvent(boolean enable) {
        firePropertyChange(new WorkflowContextEvent(this,WorkflowContextEvent.WF_ANIMATE,null,new Boolean(enable)));
    }
    @Override
    public String toString() {
    	return getName();
    }
}
