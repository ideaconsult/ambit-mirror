/*
 * Copyright Ideaconsult Ltd. (C) 2005-2008 
 *
 * Contact: nina@acad.bg
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/
package nplugins.core;

import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Attributes.Name;

import javax.swing.ImageIcon;

import nplugins.shell.application.Utils;

/**
 * A class, containing information about className, its package and a toString() output
 * @author Nina Jeliazkova
 *
 */
public class PluginPackageEntry {
	protected String className;
	protected String packageName;
	protected JarEntry jar;
    protected String[] parameters;
    protected String defaultTitle = null;
    protected ImageIcon defaultIcon = null;

 	protected static final Name ModuleOrder= new Name("Module-Order");
 	
	public int getOrder() {
		if (jar != null)
			try {
				Object o = jar.getAttributes().get(ModuleOrder);
				if(o!=null) 
					return Integer.parseInt(o.toString());
			} catch (Exception x) {
				x.printStackTrace();
			}
		
		return Integer.MAX_VALUE;
	}
	public synchronized String getDefaultTitle() {
        return defaultTitle;
    }
    public synchronized void setDefaultTitle(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }
    public synchronized ImageIcon getDefaultIcon() {
        return defaultIcon;
    }
    public synchronized void setDefaultIcon(ImageIcon defaultIcon) {
        this.defaultIcon = defaultIcon;
    }
    public PluginPackageEntry(String className, String packageName, JarEntry jar) throws Exception {
		super();
		this.className = className;
		this.packageName = packageName;
		this.jar = jar;

		/*
		this.objectTitle = objectTitle;
		if (objectTitle.equals("")) {
				Object o = Introspection.loadCreateObject(className);
				this.objectTitle = o.toString();
		}
		*/
	}
	public Object createObject() throws ClassNotFoundException ,IllegalAccessException, InstantiationException {
        return Introspection.loadCreateObject(className);
    }
	public String getClassName() {
		return className;
	}

	
	public String getPackageName() {
		return packageName;
	}

	@Override
	public String toString() {
        if (jar != null) {
            StringBuffer b = new StringBuffer();
            try {
                b.append(jar.getName());
                b.append('\n');
                b.append(jar.getComment());
                b.append('\n');                
                    Attributes a = jar.getAttributes();
                    Iterator ai = a.keySet().iterator();
                    while (ai.hasNext()) {
                        Object ak = ai.next();
                        b.append(ak);
                        b.append(':');
                        b.append(a.get(ak));
                        b.append('\n');
                    }
                    b.append("----\n");
            } catch (Exception x) {
                
            }
            return b.toString();
        } else
		return className;
	}

    protected String toString(JarFile jar) {
        StringBuffer b = new StringBuffer();
        b.append('\n');
        try {
            b.append(jar.getName());
            b.append('\n');

            
            Map<String,Attributes> map = jar.getManifest().getEntries();
            Iterator<String> i = map.keySet().iterator();
            while (i.hasNext()) {
                String key = i.next();
                b.append(key);
                b.append('\n');
                b.append("----\n");
                Attributes a = map.get(key);
                Iterator ai = a.keySet().iterator();
                while (ai.hasNext()) {
                    Object ak = ai.next();
                    b.append(ak);
                    b.append(':');
                    b.append(a.get(ak));
                    b.append('\n');
                }
                b.append("----\n");
            }
        } catch (Exception x) {
            
        }
        return b.toString();        
    }
	public JarEntry getJar() {
		return jar;
	}

    public synchronized String[] getParameters() {
        return parameters;
    }

    public synchronized void setParameters(String[] parameters) {
        this.parameters = parameters;
    }
    public String getTitle() {
        if (jar == null) {
            if (defaultTitle == null) return className;
            else return defaultTitle;
        }
        try {
            return jar.getAttributes().get(Attributes.Name.IMPLEMENTATION_TITLE).toString();
            //String version = jar.getAttributes().get(Attributes.Name.IMPLEMENTATION_VERSION).toString();
            //return title + ' ' + version;
        } catch (Exception x) {
            return className;
        }
    }
    public ImageIcon getIcon() {
        if (jar == null) {
            if (defaultIcon == null)
                return Utils.createImageIcon("nplugins/shell/resources/plugin.png");
            else return defaultIcon;
        }
        try {
            Object title = jar.getAttributes().getValue("Image-icon");
            return Utils.createImageIcon(title.toString());
        } catch (Exception x) {
            
            x.printStackTrace();
            return Utils.createImageIcon("nplugins/shell/resources/plugin.png");
        }        
        
        
    }    
}
