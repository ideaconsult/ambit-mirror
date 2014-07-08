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
package ambit2.base.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.idea.modbcum.i.exceptions.AmbitException;

import com.jgoodies.binding.beans.Model;

/**
 * A place holder for class name and description, in order to allow display of light list of objects, instantiated on demand only.
 * @author nina
 *
 */
public class ClassHolder extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6667954841743487524L;
	protected String clazz;
	protected String title;
	protected String description;
	protected String icon;
	protected ImageIcon image;
	
	public ImageIcon getImage() {
		return image;
	}
	public void setImage(ImageIcon image) {
		this.image = image;
	}
	public ClassHolder(String clazz, String title, String description, String icon) {
		setClazz(clazz);
		setTitle(title);
		setDescription(description);
		setIcon(icon);
		setImage(null);
	}
	public ClassHolder(String clazz, String title, String description, ImageIcon icon) {
		this(clazz,title,description,"");
		setImage(icon);
	}	
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	@Override
	public String toString() {
		return getTitle();
	}

	public static List<ClassHolder> load(InputStream input) throws AmbitException {
		List<ClassHolder> classes = new ArrayList<ClassHolder>();

		try {
	      	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	        boolean eof = false;
	        String fileLine;
	        while (!eof) {
	          fileLine = reader.readLine();
	          if (fileLine == null)
	            eof = true;
	          else {
	        	  classes.add(new ClassHolder(fileLine.trim(),fileLine,"",(String)null));
	          }
	        }
	        reader.close();
		} catch (Exception x) {
			
		} finally {
			try {
				input.close();
			}catch (Exception x) {}
		}
		return classes;
	}
}
