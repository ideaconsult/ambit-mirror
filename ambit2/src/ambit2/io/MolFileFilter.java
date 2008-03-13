/*
Copyright Ideaconsult Ltd. (C) 2005-2007 

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*//**
 * <b>Filename</b> toxTreeFileFilter.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-8-4
 * <b>Project</b> toxTree
 */
package ambit2.io;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-8-4
 */
public class MolFileFilter extends FileFilter {
    public final static String[] supported_extensions={
		".sdf",".csv",".txt",".mol",".mol2",
		".smi",".cml",".hin",".ichi",".inchi",".pdb",".xyz" };
    public final static String[] supported_exts_description={
		"SDF files (*.sdf)","Comma delimited (*.csv)","Text files (*.txt)",
		"MOL files (*.mol)","MOL2 files (*.mol2)",
		"SMILES files (*.smi)","CML files (*.cml)","HIN files (*.hin)",
		"ICHI files (*.ichi)","INChI files (*.inchi)","PDB files (*.pdb)","XYZ files (*.xyz)" };
    /*
    public final static String[] toxTree_ext={".tree",".tresult",".rules"};
    public final static String[] toxTree_ext_descr={"Decision tree files (*.tree)","ToxTree result files (*.tresult)","ToxTree rules (*.rules)"};
    */
    public final static String[] toxTree_ext={".tml",".tree",".fml"};
    public final static String[] toxTree_ext_descr={"XML Decision tree files (*.tml)","Binary decision tree files (*.tree)","Set of decision trees files (*.fml)"};
    public final static String[] toxTree_ext_save={".tml",".tree",".fml",".csv",".txt",".html",".pdf"};
    public final static String[] toxTree_ext_descr_save={"XML Decision tree files (*.tml)","Binary decision tree files (*.tree)","Set of decision trees files (*.fml)","Comma delimited files (*.csv)","Text files (*.txt)","HTML files (*.html)","PDF files (*.pdf)"};
    
    public final static String[] toxForest_ext={".fml",".forest"};
    public final static String[] toxForest_ext_descr={"Set of decision trees files (*.fml)","Set of decision trees binary files (*.forest)"};
    protected String extension = ".sdf";
    protected String description = "SDF file";
    public MolFileFilter() {
        super();
	}    
    /**
     * 
     */
    public MolFileFilter(String ext, String description) {
        super();
        this.extension = ext.toLowerCase();
        this.description = description;
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
	public boolean accept(File f) {
		if (f.isDirectory()) return true;
		else return (f.getName().toLowerCase().endsWith(extension));
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
	public String getDescription() {
        return description;
    }
	public static String getSuffix(String fname) {
	    String suffix = null;
	    int i = fname.lastIndexOf('.');

	    if(i > 0 && i < fname.length() - 1)
	      suffix = fname.substring(i).toLowerCase();

	    return suffix;
	  }
	
	  public static String getSuffix(File f) {
	    return getSuffix(f.getPath());
	  }
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
}

