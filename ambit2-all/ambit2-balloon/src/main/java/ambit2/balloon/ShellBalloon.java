/*
Copyright (C) 2013  

Contact: www.ideaconsult.net

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

package ambit2.balloon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.SDFWriter;

import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.core.external.ShellSDFoutput;

/**
 * A wrapper for Balloon http://users.abo.fi/mivainio/balloon
 * 
 * @author nina
 * 
 */
public class ShellBalloon extends ShellSDFoutput<IAtomContainer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -872897475497724440L;
	public static final String BALLOON_EXE = "balloon";
	public static final String BALLOON_HOME = "BALLOON_HOME";
	protected boolean hydrogens = true;
	//private String uuid = null;

	public boolean isHydrogens() {
		return hydrogens;
	}

	public void setHydrogens(boolean hydrogens) {
		this.hydrogens = hydrogens;
	}

	public ShellBalloon() throws ShellException {
		super();
		prefix = "";
	}

	protected String getBalloonHome() throws ShellException {
		return System.getenv(BALLOON_HOME);
	}

	@Override
	protected void initialize() throws ShellException {
		super.initialize();
		String balloon_home = getBalloonHome();
		File exe = new File(String.format("%s%s%s", balloon_home,
				File.separator, BALLOON_EXE));
		File winexe = new File(String.format("%s%s%s.exe", balloon_home,
				File.separator, BALLOON_EXE));

		if (!exe.exists() && !winexe.exists()) {
			throw new ShellException(this, String.format(
					"%s does not exist! Have you set %s environment variable?",
					exe.getAbsolutePath(), BALLOON_HOME));
		}
		addExecutable(CommandShell.os_WINDOWS, winexe.getAbsolutePath(), null);
		addExecutable(CommandShell.os_WINDOWSVISTA, winexe.getAbsolutePath(),
				null);
		addExecutable(CommandShell.os_WINDOWS7, winexe.getAbsolutePath(), null);
		addExecutable(CommandShell.os_FreeBSD, exe.getAbsolutePath(), null);
		addExecutable(CommandShell.os_LINUX, exe.getAbsolutePath(), null);

		setOutputFile(getOutputFileName());
		setInputFile(outputFile.replace("o_bln", "i_bln"));
		setReadOutput(true);
	}

	protected String getOutputFileName() {
		return "o_bln" + UUID.randomUUID().toString() + ".sdf";
	}

	protected String getOutputOption() {
		return "--output-file";
	}

	@Override
	protected IMolecule transform(IAtomContainer mol) {
		return null;
	}

	@Override
	public String toString() {
		return "Balloon";
	}

	@Override
	protected String getPath(File file) {
		return String.format("%s", getHomeDir(null));
	}

	@Override
	protected String getHomeDir(File file) {
		return String
				.format("%s%s.ambit2%s%s%sballoon",
						System.getProperty("java.io.tmpdir"), File.separator,
						File.separator, System.getProperty("user.name"),
						File.separator);
	}

	@Override
	protected synchronized java.util.List<String> prepareInput(String path, IAtomContainer mol) throws ShellException {

		try {
	    	String homeDir = getHomeDir(null); // getPath(new File(exe));
	    	File dir = new File(homeDir);
	    	if (!dir.exists()) dir.mkdirs();
	    	
	    	String inFile = writeInputSDF(path,mol);
	    	
	    	String outFile = String.format("%s%s%s",homeDir,File.separator,getOutputFile());
	    	
			List<String> list = new ArrayList<String>();
			//if (hydrogens)	list.add("-h");
			list.add("-f");
			list.add(getBalloonHome()+"/MMFF94.mff");
			//list.add("--allowcrowded");
			//list.add("--useSimplex");
			list.add("--singleconf");
			list.add("--energyConstant");
			list.add("40");
			//list.add("--nosymmetry");
			//list.add("--rebuildGeometry");
			//list.add("-t");
			//list.add("0.5");
			//list.add("--nGenerations");
			//list.add("200");
			//list.add("--fullforce");
			//list.add("--noGA");
			list.add("--input-file");
			list.add(inFile);
			list.add(getOutputOption());
			list.add(outFile);
			return list;
		} catch (Exception x) {
			throw new ShellException(this,x);
		}
	}

	protected String writeInputSDF(String path, IAtomContainer mol)
			throws IOException, CDKException {
		SDFWriter writer = null;
		try {
			String input = path + File.separator + getInputFile();
			FileOutputStream out = new FileOutputStream(input);
			writer = new SDFWriter(out);
			writer.write(mol);
			return input;
		} catch (CDKException x) {
			throw x;
		} catch (IOException x) {
			throw x;
		} finally {
			try {
				writer.close();
			} catch (Exception x) {
			}
		}
	}

	@Override
	protected synchronized IAtomContainer parseOutput(String path,
			IAtomContainer mol) throws ShellException {
		File out = new File(String.format("%s%s%s", path, File.separator,
				getOutputFile()));
		if (!out.exists()) { // Balloon writes _bad. files on failure. But it's
								// ok for a starting point structure
			out = new File(out.getAbsolutePath().replace(".sdf", "_bad.sdf"));
			setOutputFile(out.getName());
		}
		IAtomContainer newmol = super.parseOutput(path, mol);
		try {
			File file = new File(String.format("%s%s%s", path, File.separator,	getInputFile()));
			file.delete();
		} catch (Exception x) {	}
		try {	File file = out; file.delete();	} catch (Exception x) {}
		setOutputFile(out.getName().replace("_bad", ""));
		return newmol;
	}
}
