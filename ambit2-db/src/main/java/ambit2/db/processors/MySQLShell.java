/* MySQLShell.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-9 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Ideaconsult Ltd.
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
 * 
 */

package ambit2.db.processors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.AmbitIOException;
import ambit2.base.interfaces.IProcessor;
import ambit2.db.processors.ICommand.COMMAND;

/**
 * A class to start ant stop MySQL process. 
 * TODO make it singleton class
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class MySQLShell  implements IProcessor<MySQLCommand,MySQLCommand> {
	protected boolean enabled=true;
    /**
	 * 
	 */
	private static final long serialVersionUID = -8839094043823680323L;
	protected StreamGobbler gin;
    protected StreamGobbler gerr;

	protected static Logger logger = Logger.getLogger(MySQLShell.class.getName());
    /**
     * 
     */
    public MySQLShell() {
        super();
        
    }
    public long getID() {

    	return 0;
    }
    protected void startMySQL(MySQLCommand cmd)  {
        if (cmd.getProcess() != null) {
        	cmd.setException(new AmbitException(cmd + " is already running"));
        	return;
        }
        cmd.setException(null);
        
        try {
        	String command = cmd.getCommandFromFile(COMMAND.START);
        	final String errFile=cmd.getMysqlPath()+"/data/ambit.err"; 
            File f = new File(errFile);
            if (f.exists()) f.delete();
            
            String run = cmd.getMysqlPath() + "/bin/" + command;
	        StringTokenizer st = new StringTokenizer(run, " ");
	        if (st.hasMoreTokens()) {
	        	String file = st.nextToken();
	        	if (!(new File(file).exists())) {
	        		cmd.setException(new AmbitIOException(AmbitIOException.MSG_NOTFOUND,file));
	        		return;
	        	}
	        }
            logger.info(run);
            Timer timerIn = new Timer(true);
            Process process = Runtime.getRuntime().exec(run);
	        cmd.setProcess(process);
	        f = new File(errFile);	        
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
        } catch (Exception x) {
            cmd.setException(x);
            cmd.setProcess(null);
        //} catch (InterruptedException x) {
           // throw new AmbitException("MySQL start interrupted!");
        
        }
    }
    /**
     * Runs mysqladmin shutdown and waits for the mysql daemon to stop.
     * @param cmd
     * @throws AmbitException
     */
    public void stopMySQL(MySQLCommand cmd) {
        if (cmd.getProcess() == null) {
        	AmbitException x = new AmbitException(cmd.getMysqlPath() + " not started");
        	cmd.setException(x);
        }
        cmd.setException(null);
        try {
            String command = cmd.getCommandFromFile(COMMAND.STOP);
            String run = cmd.getMysqlPath() + "/bin/" + command;
            StringTokenizer st = new StringTokenizer(run, " ");
	        if (st.hasMoreTokens()) {
	        	String file = st.nextToken();
	        	if (!(new File(file).exists())) {
	        		cmd.setException(new AmbitIOException(AmbitIOException.MSG_NOTFOUND,file));
	        		return;
	        	}
	        }            
	        Runtime.getRuntime().exec(run);
	        cmd.getProcess().waitFor();
	        cmd.setProcess(null);
	        logger.info("MySQL stopped\t"+run);

	        final String errFile = cmd.getMysqlPath() + "/data/ambit2.err"; 
	        File f = new File(errFile);
	        f.delete();
	        
        } catch (Exception x) {
            cmd.setException(x);
        }
    }    

	public boolean isEnabled() {
		return enabled;
	}
	public MySQLCommand process(MySQLCommand target) throws AmbitException {
		switch (target.getCommand()) {
		case START:
			startMySQL(target);
			break;
		case STOP:
			stopMySQL(target);
			break;
		default:
			target.setException(new Exception("Unknown command "+ target));
			
		}
		if (target.getException()!= null)
			throw new AmbitException(target.getException());
		return target;
	}
	public void setEnabled(boolean value) {
		this.enabled = value;
		
	}
}

class StreamGobbler extends Thread
{
    protected boolean interrupted = false;	
    InputStream is;
    String type;
    
    StreamGobbler(InputStream is, String type)
    {
        this.interrupted = false;
        this.is = is;
        this.type = type;
    }
    
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null) {
                System.out.println(type + ">" + line);    
                if (line.indexOf("ready for connections")>0) break;
                if (interrupted) break;
            }    
            is.close();
         } catch (IOException ioe) {
                ioe.printStackTrace();  
         }
    }
    public synchronized boolean isInterrupted() {
        return interrupted;
    }
    public synchronized void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }
}