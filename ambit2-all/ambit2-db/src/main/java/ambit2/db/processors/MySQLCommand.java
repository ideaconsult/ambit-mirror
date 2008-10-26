/**
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import ambit2.core.exceptions.AmbitException;
import ambit2.core.exceptions.AmbitIOException;

/**
 * Used in {@link MySQLShell}
 * @author nina
 *
 */
public class MySQLCommand implements ICommand, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9084470164513487836L;
	protected COMMAND command;
    protected String startFile = "ambit2/db/windows/mysql-start.txt";
    protected String stopFile = "ambit2/db/windows/mysql-stop.txt";
    protected String mysqlPath = "../mysql";
    protected String errFile = "/data/ambit.err";
	public String getErrFile() {
		return errFile;
	}

	public void setErrFile(String errFile) {
		this.errFile = errFile;
	}
	protected transient Process process =null ;
	protected transient Exception exception = null;
	protected long timeout =  120000;

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	protected String getFile(COMMAND command) {
		switch (command) {
		case START:
			return startFile;
		case STOP:
			return stopFile;
		default:
			return null;
			
		}  		
	}

	protected void setFile(COMMAND command,String file) {
		switch (command) {
		case START:
			startFile = file;
		case STOP:
			stopFile = file;
		default:
		
		}  
	}

	public String getStartFile() {
		return getFile(COMMAND.START);
	}

	public void setStartFile(String startFile) {
		setFile(COMMAND.START, startFile);
	}

	public String getStopFile() {
		return getFile(COMMAND.STOP);
	}

	public void setStopFile(String stopFile) {
		setFile(COMMAND.START, stopFile);
	}

	public String getMysqlPath() {
		return mysqlPath;
	}

	public void setMysqlPath(String mysqlPath) {
		this.mysqlPath = mysqlPath;
	}

	public COMMAND getCommand() {
		return command;
	}

	public void setCommand(COMMAND command) {
		this.command = command;
	}
    public String getCommandFromFile(COMMAND cmd) throws AmbitException {
    	String file = getFile(cmd);
    	if (file == null) throw new AmbitException("Unsupported command "+command);
	 	
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
        if (in == null) throw new AmbitIOException(AmbitIOException.MSG_ERRORONOPEN+file);
        String command = null;
        try {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        command = reader.readLine();
	        reader.close();
        } catch (Exception x) {
            throw new AmbitIOException(x);
        }
        return command;
    }
    @Override
    public String toString() {
    	if (process != null) return process.toString();
    	else return getCommand().toString();
    }
}

