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

package ambit2.base.external;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import ambit2.base.io.DownloadTool;

/**
 * A wrapper for an external executable (OS dependent). The executable and
 * supporting files should be available as a resource in a jar file. When
 * requested, the files are copied under user home directory and executed from
 * there. <br>
 * To change this behaviour, override {@link #getExecutable(String, String)}. <br>
 * OS names expected: "Mac OS","Windows","AIX","Linux","HP-UX","Solaris"
 * 
 * @author nina
 * 
 */
public abstract class CommandShell<INPUT, OUTPUT> implements
		IProcessor<INPUT, OUTPUT> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5771392099125415099L;
	protected String prefix = "ambit2/";
	public static final String os_MAC = "Mac OS";
	public static final String os_WINDOWS = "Windows";
	public static final String os_WINDOWSVISTA = "Windows Vista";
	public static final String os_WINDOWS7 = "Windows 7";
	public static final String os_LINUX = "Linux";
	public static final String os_LINUX64 = "Linux64";
	public static final String os_FreeBSD = "FreeBSD";
	private final static String msgEmptyMolecule = "Empty molecule after %s processing";

	public static String getMsgemptymolecule() {
		return msgEmptyMolecule;
	}

	protected Logger logger = Logger.getLogger(getClass().getName());

	protected Hashtable<String, Command> executables; // <os.name, executable>
	protected String inputFile = null;
	protected String outputFile = null;
	protected boolean runAsync = false;

	protected boolean enabled = true;
	protected int exitCode = 0;

	public long getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	protected CommandShell() throws ShellException {
		executables = new Hashtable<String, Command>();
		initialize();
	}

	public synchronized int getExitCode() {
		return exitCode;
	}

	public synchronized void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

	protected void initialize() throws ShellException {

	}

	public String addExecutable(String executable, String[] morefiles)
			throws ShellException {
		return addExecutable(System.getProperty("os.name"), executable,
				morefiles);
	}

	public String addExecutable(String osname, String executable,
			String[] morefiles) throws ShellException {
		// File file = new File(executable);
		// if (!file.exists()) throw new
		// ShellException(this,file.getAbsoluteFile() + " not found!");
		executables.put(osname, new Command(executable, morefiles));
		return executable;
	}

	public String addExecutableMac(String executable, String[] morefiles)
			throws ShellException {
		return addExecutable(os_MAC, executable, morefiles);
	}

	public String addExecutableWin(String executable, String[] morefiles)
			throws ShellException {
		addExecutable(os_WINDOWSVISTA, executable, morefiles);
		return addExecutable(os_WINDOWS, executable, morefiles);
	}

	public String addExecutableLinux(String executable, String[] morefiles)
			throws ShellException {
		return addExecutable(os_LINUX, executable, morefiles);
	}

	public String addExecutableLinux64(String executable, String[] morefiles)
			throws ShellException {
		return addExecutable(os_LINUX64, executable, morefiles);
	}

	public String addExecutableFreeBSD(String executable, String[] morefiles)
			throws ShellException {
		return addExecutable(os_FreeBSD, executable, morefiles);
	}

	public synchronized String getExecutable(String osname, String osarch)
			throws Exception {

		// ambit2/
		boolean runChmod = osname.startsWith(os_LINUX);
		Command command = executables.get(osname);
		if (osarch != null) {
			if (os_LINUX.equals(osname) && osarch.endsWith("64")) {
				Command newCommand = executables.get(os_LINUX64);
				if (newCommand != null) {
					command = newCommand;
					osname = os_LINUX64;
				}
			}
		}
		String exe = command.getExe();
		File file = new File(exe);

		if (!file.exists()) {
			String homeDir = getHomeDir(file);
			file = new File(homeDir, exe);
			if (!file.exists()) {
				logger.fine("Writing " + exe + " to " + file);
				DownloadTool.download(prefix + exe, file);
				command.setExe(file.getAbsolutePath());
				// trying chmod +x
				if (runChmod)
					try {
						Runtime.getRuntime().exec(
								String.format("chmod +x %s",
										file.getAbsolutePath()));
					} catch (Exception x) {
						logger.log(
								Level.WARNING,
								"Error when executing chmod on "
										+ file.getAbsolutePath(), x);
					}
			}
			if (command.getAdditionalFiles() != null)
				for (String lib : command.getAdditionalFiles()) {
					File newfile = new File(homeDir, lib);
					if (!newfile.exists())
						DownloadTool.download(prefix + lib, newfile);
				}
			command.setExe(file.getAbsolutePath());

		}
		return executables.get(osname).getExe();
	}

	/**
	 * invokes {@link #getExecutable(String, String)} with
	 * System.getProperty("os.name") as argument
	 * 
	 * @return absolute path to the executable
	 * @throws ShellException
	 */
	public String getExecutable() throws ShellException {
		String osName = System.getProperty("os.name");
		String osArch = System.getProperty("os.arch");
		// 32bit: x86 i386 sparc
		// 64bit: amd64 x86_64 ppc sparcv9
		Enumeration<String> oss = executables.keys();
		while (oss.hasMoreElements()) {
			String os = oss.nextElement();
			if (osName.startsWith(os))
				try {
					String exeString = getExecutable(os, osArch);
					if (exeString != null)
						return exeString;
				} catch (IOException x) {
					throw new ShellException(this, "Not found");
				} catch (Exception x) {
					throw new ShellException(this, "Not supported for "
							+ osName);
				}
		}
		throw new ShellException(this, "Not supported for " + osName);
	}

	public synchronized OUTPUT runShell(INPUT mol) throws ShellException {

		return runShell(mol, getExecutable());

	}

	public synchronized OUTPUT process(INPUT target) throws AmbitException {
		try {
			return runShell(target);
		} catch (ShellException x) {
			throw x;
		} catch (Exception x) {
			throw new ShellException(this, x);
		}
	};

	/**
	 * Returns empty string, override with smth meaningfull
	 * 
	 * @param mol
	 * @return list of command line parameters (exe name excluded) to be passed
	 *         to the executable
	 * @throws ShellException
	 */
	protected synchronized List<String> prepareInput(String path, INPUT mol)
			throws ShellException {
		return null;
	}

	/**
	 * Does nothing, override with smth meaningfull
	 */
	protected abstract OUTPUT parseOutput(String path, INPUT mol)
			throws ShellException;

	protected OUTPUT parseOutput(String path, INPUT mol, int exitVal)
			throws ShellException {
		if (exitCodeOK(exitVal))
			return parseOutput(path, mol);
		else
			return null;
	}

	protected String getPath(File file) {
		String path = file.getAbsolutePath();
		int i = path.lastIndexOf(File.separatorChar);
		if (i > -1)
			path = path.substring(0, i);
		else
			path = "";
		return path;
	}

	protected String getHomeDir(File file) {
		return System.getProperty("java.io.tmpdir") + "/.ambit2/"
				+ System.getProperty("user.name");
	}

	protected synchronized INPUT transform_input(INPUT input)
			throws AmbitException {
		return input;
	}

	protected synchronized OUTPUT runShell(INPUT input, String execString)
			throws ShellException {
		try {
			setExitCode(0);
			File file = new File(execString);
			String path = getPath(file);

			INPUT mol = transform_input(input);
			List<String> inFile = prepareInput(path, mol);

			List<String> command = new ArrayList<String>();
			command.add(execString);
			if (inFile != null)
				for (int j = 0; j < inFile.size(); j++)
					command.add(inFile.get(j));

			ProcessBuilder builder = new ProcessBuilder(command);
			builder.directory(new File(path));
			// If the value is set to true, the standard error is merged with
			// the standard output
			Map<String, String> environ = builder.environment();
			builder.directory(new File(path));

			if (!runAsync) {
				builder.redirectErrorStream(true);
				logger.fine("<" + toString() + " filename=\"" + execString
						+ "\">");
				logger.log(Level.FINE, "<environ>" + environ.toString()
						+ "</environ>");
				long now = System.currentTimeMillis();
				final Process process = builder.start();
				InputStream is = process.getInputStream();
				processStdOut(is);
				logger.fine("<wait process=\"" + execString + "\">");

				setExitCode(process.waitFor());
				logger.fine("</wait>");
				logger.fine("<exitcode value=\""
						+ Integer.toString(getExitCode()) + "\">");
				logger.fine("<elapsed_time units=\"ms\">"
						+ Long.toString(System.currentTimeMillis() - now)
						+ "</elapsed_time>");
				logger.fine("</" + toString() + ">");

				OUTPUT newmol = null;
				if (exitCodeOK(getExitCode())) {
					logger.fine("<parse>");
					newmol = parseOutput(path, mol);
					logger.fine("</parse>");
				} else {
					logger.severe("<error>" + Integer.toString(getExitCode())
							+ "</error>");
					newmol = parseOutput(path, mol, getExitCode());
				}
				return newmol;
			} else {
				final Process process = builder.start();
				return transform(process, mol);
			}

		} catch (ShellException x) {
			throw x;
		} catch (Throwable x) {
			throw new ShellException(this, x);
		}
	}

	protected void processStdOut(InputStream is) throws IOException {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		logger.fine("<stdout>");
		while ((line = br.readLine()) != null) {
			logger.fine(line);
		}
		br.close();
		logger.fine("</stdout>");
	}

	protected synchronized OUTPUT transform(Process process, INPUT cmd) {
		return transform(cmd);
	}

	/*
	 * protected Process runAsync(INPUT mol,String execString) throws
	 * ShellException { try { File file = new File(execString); String path =
	 * getPath(file);
	 * 
	 * List<String> inFile = prepareInput(path,mol);
	 * 
	 * List<String> command = new ArrayList<String>(); command.add(execString);
	 * if (inFile != null) for (int j=0; j < inFile.size();j++)
	 * command.add(inFile.get(j));
	 * 
	 * ProcessBuilder builder = new ProcessBuilder(command); //If the value is
	 * set to true, the standard error is merged with the standard output
	 * //builder.redirectErrorStream(true); Map<String, String> environ =
	 * builder.environment(); builder.directory(new File(path));
	 * 
	 * logger.info("<" + toString() + " filename=\""+execString+"\">"); long
	 * now=System.currentTimeMillis(); final Process process = builder.start();
	 * InputStream is = process.getInputStream(); InputStreamReader isr = new
	 * InputStreamReader(is); BufferedReader br = new BufferedReader(isr);
	 * String line; logger.info("<stdout>"); while ((line = br.readLine()) !=
	 * null) { logger.info(line); } logger.info("</stdout>");
	 * 
	 * logger.info("<wait process=\""+execString+"\">");
	 * 
	 * int exitVal = process.waitFor(); logger.info("</wait>");
	 * logger.info("<exitcode value=\""+Integer.toString(exitVal)+"\">");
	 * logger.
	 * info("<elapsed_time units=\"ms\">"+Long.toString(System.currentTimeMillis
	 * ()-now)+ "</elapsed_time>"); logger.info("</" + toString() + ">");
	 * 
	 * OUTPUT newmol = null; if (exitCodeOK(exitVal)) { logger.info("<parse>");
	 * newmol = parseOutput(path, mol); logger.info("</parse>");
	 * 
	 * } return newmol;
	 * 
	 * } catch (Throwable x) { logger.debug(x.getMessage()); throw new
	 * ShellException(this,x); } }
	 */
	protected boolean exitCodeOK(int exitVal) {
		return exitVal == 0;
	}

	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	protected abstract OUTPUT transform(INPUT mol);

	@Override
	public void open() throws Exception {

	}

	@Override
	public void close() throws Exception {
	}
}
