package ambit2.db.processors;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.AmbitIOException;
import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.db.processors.ICommand.COMMAND;

/**
 * A newer implementation of MySQLShell, as a {@link CommandShell} descendant.
 * Something is wrong here. 
 * @author nina
 *
 */
public class MySQLStart extends CommandShell<MySQLCommand, MySQLCommand> {
	protected StreamGobbler gin;
    //protected StreamGobbler gerr;	
	public MySQLStart() throws ShellException {
		super();
		runAsync = true;
		
	}
	@Override
	protected void initialize() throws ShellException {
	}
	@Override
	public String getExecutable(String osname) {
		return executables.get(osname).getExe();
	}

	@Override
	protected List<String> prepareInput(String path, MySQLCommand cmd)
			throws ShellException {
		try {
			String run = cmd.getMysqlPath() + "/bin/"  + cmd.getCommandFromFile(COMMAND.START);
	        StringTokenizer st = new StringTokenizer(run, " ");
	        int index = 0;
	        List<String> params = new ArrayList<String>();
	        while (st.hasMoreTokens()) {
	        	String param = st.nextToken();
	        	if (index>0) {
	        		params.add(param);
	        	}
	        	index++;
	        }	
	        return params;
		} catch (AmbitException x) {
			cmd.setException(x);
			throw new ShellException(this,x);
		}
	}
	@Override
	protected MySQLCommand parseOutput(String path, MySQLCommand mol)
			throws ShellException {
		return mol;
	}
	@Override
	protected MySQLCommand transform(Process process, MySQLCommand cmd) {
		switch (cmd.getCommand()) {
		case START: 
			try {
	        	final String errFile=cmd.getMysqlPath()+cmd.getErrFile(); 

	            Timer timerIn = new Timer(true);			
		        File f = new File(errFile);	        
		        long now = System.currentTimeMillis();
		        while (!f.exists()) {
		            if ((System.currentTimeMillis()-now) > cmd.getTimeout()) break;
		        }
		        InputStream in = new FileInputStream(f);
		        gin = new StreamGobbler(in,f.getAbsolutePath());
				timerIn.schedule(new TimerTask() {
		            public void run() {
		               gin.setInterrupted(true);
		               //gerr.setInterrupted(true);
		            }
				},cmd.getTimeout());
			
				gin.run();
				in.close();
		        if (gin.isInterrupted()) {
		        	cmd.setException(new AmbitIOException("Can't start MySQL in\t"+cmd.getTimeout()/1000 + "s.!  " + cmd.getMysqlPath()));
		        	cmd.setProcess(null);
		        }			
				cmd.setProcess(process);	
				break;
			} catch (Exception x) {
				cmd.setException(x);
			}
		case STOP:
			try {
				cmd.getProcess().waitFor();	
			} catch (Exception x) {
				cmd.setException(x);
				
			}
			break;

		default:
			break;
		}
		
		return transform(cmd);
	}
	@Override
	protected MySQLCommand transform(MySQLCommand cmd) {

		return cmd;
	}

	@Override
	public MySQLCommand runShell(MySQLCommand cmd) throws ShellException {
		try {
			
			String run = cmd.getMysqlPath() + "/bin/"  + cmd.getCommandFromFile(COMMAND.START);
	        StringTokenizer st = new StringTokenizer(run, " ");
	        if (st.hasMoreTokens()) {
	        	String file = st.nextToken();
	        	if (!(new File(file).exists())) {
	        		ShellException x = new ShellException(this,AmbitIOException.MSG_NOTFOUND +run);
	        		cmd.setException(x);
	        		throw x;
	        	} else 
	        		addExecutable(os_WINDOWS, file,null);
	        }			
	        /**
	         * this is the log file. If existing, then this was already launched
	         */
        	final String errFile=cmd.getMysqlPath()+cmd.getErrFile(); 
            File f = new File(errFile);
            if (f.exists()) f.delete();				
			return super.runShell(cmd);
		} catch (AmbitException x) {
    		ShellException e = new ShellException(this,x);
    		cmd.setException(e);
    		throw e;			
		}
		
	}
	
	

}

