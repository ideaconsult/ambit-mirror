package ambit2.waffles;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.base.io.DownloadTool;

public abstract class ShellWaffles extends CommandShell<Properties, Properties> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 872475206438753370L;
	protected static final String exitCodeProperty = "exitCode";

	public static String getExitcodeProperty() {
		return exitCodeProperty;
	}


	private final static String path = "bin/waffles";  

	protected enum OS {
		win,
		linux,
		freebsd
	}
	protected String tool;
	public String getOutProperty() {
		return outProperty;
	}

	public void setOutProperty(String outProperty) {
		this.outProperty = outProperty;
	}


	protected String outProperty;
	protected Properties out = new Properties();
	
	public String getTool() {
		return tool;
	}

	public void setTool(String tool) {
		this.tool = tool;
	}

	public static String getPath(OS os) {
		return String.format("%s/%s",path,os.name());
	}
	protected ShellWaffles() throws ShellException {
		super();
	}

	protected void initialize(String tool) throws ShellException {
		addExecutable(CommandShell.os_WINDOWS, String.format("%s/%s/%s.exe",path,OS.win,tool),
					new String[]{});	
		addExecutable(CommandShell.os_WINDOWS7, String.format("%s/%s/%s.exe",path,OS.win,tool),
				new String[]{});	
		addExecutable(CommandShell.os_WINDOWSVISTA, String.format("%s/%s/%s.exe",path,OS.win,tool),
				new String[]{});			
        addExecutable(CommandShell.os_LINUX, String.format("%s/%s/%s",path,OS.linux,tool),
        		new String[]{});      
        addExecutable(CommandShell.os_FreeBSD, String.format("%s/%s/%s",path,OS.freebsd,tool),
        		new String[]{});           
		//setInputFile("rough.sdf");
		//setOutputFile("opt.sdf");
	}


	@Override
	protected Properties parseOutput(String path, Properties in)
			throws ShellException {
		this.out.put(exitCodeProperty, "0");
		return this.out;
	}


	@Override
	protected Properties parseOutput(String path, Properties mol, int exitVal)
			throws ShellException {
		this.out.put(exitCodeProperty, Integer.toString(exitVal));
		return this.out;
	}


	@Override
	protected Properties transform(Properties in) {
		return in;
	}
	 
	
	@Override
    protected void processStdOut(InputStream is) throws IOException {
		if (outputFile!=null) {
			DownloadTool.download(is, new File(outputFile));
			if (outProperty!=null) out.put(outProperty, outputFile);
		} else {
			StringBuilder builder = new StringBuilder();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String line;
	        while ((line = br.readLine()) != null) 
	        	if (outProperty!=null) {
	        		builder.append(line);
	        		builder.append("\n");
	        	} else {
	        		logger.info(line);
	        		logger.info("\n");
	        	}
	        if (outProperty!=null) out.put(outProperty, builder.toString());
	        br.close();
		}
    }


}
