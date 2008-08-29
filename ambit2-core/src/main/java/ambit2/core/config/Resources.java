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

package ambit2.core.config;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Localisation resources (singleton)
 * @author nina
 *
 */
public class Resources {
	/**
	 * Exit 
	 */
	public static final String msg_exit="msg_exit";
	/**
	 * Empty file
	 */
	public static final String MSG_EMPTYFILE="MSG_EMPTYFILE";
	/**
	 * successful file open
	 */
	public static final String MSG_OPENSUCCESS="MSG_OPENSUCCESS";
	/**
	 * 	Successful file save 
	 */
	public static final String MSG_SAVESUCCESS="MSG_SAVESUCCESS";
	/**
	 * error on file open
	 */
	public static final String MSG_ERRORONOPEN="MSG_ERRORONOPEN";
	/**
	 * Error on file save
	 */
	public static final String MSG_ERRORONSAVE="MSG_ERRORONSAVE";
	/**
	 * unsupported format
	 */
	public static final String MSG_UNSUPPORTEDFORMAT="MSG_UNSUPPORTEDFORMAT";
	/**
	 not allowed
	 */
	public static final String msg_notallowed="msg_notallowed";
	/**
	 * Invalid CAS
	 */
	public static final String err_invalidcas="err_invalidcas";
	/**
	 * show aromaticity
	 */
	public static final String SHOW_AROMATICITY="SHOW_AROMATICITY";
	/**
	 * default dir
	 */
	public static final String DEFAULT_DIR="DEFAULT_DIR";
	/**
	 * generate 2D
	 */
	public static final String GENERATE2D="GENERATE2D";
	/**
	 * Openbabel or CDK smiles parser
	 */
	public static final String SMILESPARSER="SMILESPARSER";
	
	/**
	 * Undefined smarts
	 */
	public static final String SMARTS_UNDEFINED="SMARTS_UNDEFINED";
		
	protected static ResourceBundle resourceBundle = null;
	private Resources() {
		
	}
	public static ResourceBundle getResourceBundle() {
		if (resourceBundle == null) {
			resourceBundle = ResourceBundle.getBundle("ambit2.config.ResourceBundle",Locale.getDefault());
		}	
		return resourceBundle;		
	}
	public static String getString(String key) {
		return getResourceBundle().getString(key);
	}
	public static String[] getStringArray(String key) {
		return getResourceBundle().getStringArray(key);
	}	
	public static Object getObject(String key) {
		return getResourceBundle().getObject(key);
	}		
	
}


