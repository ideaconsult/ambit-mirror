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

package ambit2.external;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import ambit2.log.AmbitLogger;

/**
 * "Mac OS","Windows","AIX","Linux","HP-UX","Solaris" 
 * @author nina
 *
 */
public abstract class CommandShell<INPUT,OUTPUT> {
	public static final String os_MAC = "Mac OS";
	public static final String os_WINDOWS = "Windows";
	public static final String os_LINUX = "Linux";
	
    protected static AmbitLogger logger = new  AmbitLogger(CommandShell.class);	
	protected Hashtable<String, String> executables; //<os.name, executable>
	protected String inputFile = null;
	protected String outputFile = null;
	
	protected CommandShell() throws ShellException {
		executables = new Hashtable<String, String>();
		initialize();
	}
	protected void initialize() throws ShellException  {
		
	}
	
	public String addExecutable(String osname,String executable) throws ShellException {
		//File file = new File(executable);
		//if (!file.exists()) throw new ShellException(this,file.getAbsoluteFile() + " not found!");
		return executables.put(osname,executable);
	}
	public String addExecutableMac(String executable) throws ShellException {
		return addExecutable(os_MAC,executable);
	}
	public String addExecutableWin(String executable) throws ShellException  {
		return addExecutable(os_WINDOWS,executable);
	}
	public String addExecutableLinux(String executable) throws ShellException  {
		return addExecutable(os_LINUX,executable);
	}			
	public String getExecutable(String osname) {
		return executables.get(osname);
	}
    public OUTPUT runShell(INPUT mol) throws ShellException {

            
        String osName = System.getProperty("os.name");
        Enumeration<String> oss = executables.keys();
        while (oss.hasMoreElements()) {
        	String os = oss.nextElement();
        	if (osName.startsWith(os)) {
        		String exeString = executables.get(os);
        		if (exeString == null) throw new ShellException(this,"Not supported for "+osName);
        		else 
        			try {
        				return runShell(mol, exeString);
        			} catch (Exception x) {
        				throw new ShellException(this,x);
        			}
        	}
        }
        return null;
    }
    /**
     * Returns empty string, override with smth meaningfull
     * @param mol
     * @return
     * @throws ShellException
     */
    protected List<String> prepareInput(String path, INPUT mol) throws ShellException {
    	return null;
    }
    /**
     * Does nothing, override with smth meaningfull
     */
    protected abstract OUTPUT parseOutput(String path, INPUT mol) throws ShellException ;
    

    protected String getPath(File file) {
        String path = file.getAbsolutePath();
        int i = path.lastIndexOf(File.separatorChar);
        if (i>-1) path = path.substring(0,i);
        else path="";    	
        return path;
    }
    protected OUTPUT runShell(INPUT mol,String execString) throws ShellException {
    	try {
    			File file = new File(execString);
    			String path = getPath(file);
    			
                List<String> inFile = prepareInput(path,mol);

                List<String> command = new ArrayList<String>();
                command.add(execString);
                if (inFile != null)
	                for (int j=0; j < inFile.size();j++)
	                	command.add(inFile.get(j));

                ProcessBuilder builder = new ProcessBuilder(command);
                //If the value is set to true, the standard error is merged with the standard output
                builder.redirectErrorStream(true);
                Map<String, String> environ = builder.environment();
                builder.directory(new File(path));
                
                logger.info("<" + toString() + " filename=\""+execString+"\">");
                long now=System.currentTimeMillis();    
                final Process process = builder.start();
                InputStream is = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                logger.info("<stdout>");
                while ((line = br.readLine()) != null) {
                	logger.info(line);
                }
                logger.info("</stdout>");
                
                logger.info("<wait process=\""+execString+"\">");

                int exitVal = process.waitFor();
                logger.info("</wait>");
                logger.info("<exitcode value=\""+Integer.toString(exitVal)+"\">");
                logger.info("<elapsed_time units=\"ms\">"+Long.toString(System.currentTimeMillis()-now)+ "</elapsed_time>");                
                logger.info("</" + toString() + ">");
                
                OUTPUT newmol = null;
                if (exitCodeOK(exitVal)) {
                	logger.info("<parse>");
                	newmol = parseOutput(path, mol);
                	logger.info("</parse>");
                	
                }
                return newmol;
                
            } catch (Throwable x) {
            	logger.debug(x.getMessage());
                throw new ShellException(this,x);
            }
    }
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
	protected abstract OUTPUT transform(INPUT mol) ;
}



/*
Since 1.5, the ProcessBuilder class provides more controls overs the process to be started. It's possible to set a starting directory. 

public class CmdProcessBuilder {
  public static void main(String args[]) 
     throws InterruptedException,IOException 
  {
    List<String> command = new ArrayList<String>();
    command.add(System.getenv("windir") +"\\system32\\"+"tree.com");
    command.add("/A");

    ProcessBuilder builder = new ProcessBuilder(command);
    Map<String, String> environ = builder.environment();
    builder.directory(new File(System.getenv("temp")));

    System.out.println("Directory : " + System.getenv("temp") );
    final Process process = builder.start();
    InputStream is = process.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);
    String line;
    while ((line = br.readLine()) != null) {
      System.out.println(line);
    }
    System.out.println("Program terminated!");
  }
}

*/