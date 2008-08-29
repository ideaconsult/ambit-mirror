/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package nplugins.core;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PluginClassPath  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1813037553139995737L;
	protected String pref_key = "/nplugins";

    protected final static String pref_path = "classpath";
	protected static String defaultClassPath = "dist,ext";
	protected ArrayList<String> storage; 
	public PluginClassPath(String classPath) throws BackingStoreException  {
		storage = new ArrayList<String>();
		init(classPath);
	}
	
	public PluginClassPath() throws BackingStoreException  {
		storage = new ArrayList<String>();
		setPref_key(pref_key);
	}
	
    public synchronized void setPref_key(String pref_key) throws BackingStoreException  {
        this.pref_key = pref_key;
        Preferences prefs = Preferences.userRoot().node(pref_key);
        init(prefs.get(pref_path, defaultClassPath));        
    }

    public synchronized String getPref_key() {
        return pref_key;
    }	
	protected void init(String cp) throws BackingStoreException {
    	Preferences prefs = Preferences.userRoot().node(pref_key);
    	StringTokenizer tok = new StringTokenizer(cp, ",");
    	storage.clear();
    	while (tok.hasMoreTokens()) {
    		add(tok.nextToken());
    	}
    	prefs.put(pref_path, toPath());    	
	}
	public String remove(int index) throws BackingStoreException {
		String s =  storage.remove(index);
    	Preferences prefs = Preferences.userRoot().node(pref_key);
    	prefs.put(pref_path, toPath());		
    	prefs.flush();
		return s;
	}
	public boolean remove(Object o) throws BackingStoreException {
		boolean b =  storage.remove(o);
    	Preferences prefs = Preferences.userRoot().node(pref_key);
    	prefs.put(pref_path, toPath());	
    	prefs.flush();
    	return b;
	}
	

	public boolean add(String o) throws BackingStoreException  {
		if (!contains(o) && storage.add(o)) {
	    	Preferences prefs = Preferences.userRoot().node(pref_key);
	    	prefs.put(pref_path, toPath());			
	    	prefs.flush();
			return true;
		} else return false;
	}
	public boolean contains(String path) {
	    return storage.indexOf(path)>=0;
	}
	public void clear() throws BackingStoreException  {
		storage.clear();
		Preferences prefs = Preferences.userRoot().node(pref_key);
		prefs.clear();
		prefs.flush();
	}
	protected String toPath() {
		if (storage.size() == 0) return defaultClassPath;
		
		StringBuffer b = new StringBuffer();
		for (int i=0; i < storage.size(); i++ ) {
			if (i>0) b.append(',');
			b.append(storage.get(i));
		}	
		return b.toString();
	}
	@Override
	public String toString() {
		return toPath();
	}
	public int size() {
		return storage.size();
	}
	public String get(int index) {
		return storage.get(index);
	}
}


