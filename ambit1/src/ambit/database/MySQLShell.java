/* MySQLShell.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-9 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import ambit.exceptions.AmbitException;
import ambit.exceptions.AmbitIOException;
import ambit.log.AmbitLogger;

/**
 * A class to start ant stop MySQL process. 
 * TODO make it singleton class
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class MySQLShell  extends Thread {
    protected StreamGobbler gin;
    protected StreamGobbler gerr;
    protected static AmbitLogger logger = new AmbitLogger(MySQLShell.class);
    protected static String startFile = "ambit/database/mysql-start.txt";
    protected static String stopFile = "ambit/database/mysql-stop.txt";
    //protected static String startFile = "mysql-start.txt";
    //protected static String stopFile = "mysql-stop.txt";
    //protected String mysqlPath = "D:/src/ambit/mysql/bin";
    protected static String mysqlPath = "../mysql/bin";
    protected static boolean started = false;
    protected Process mysqlProcess = null;
    /**
     * 
     */
    public MySQLShell() {
        super();
        
    }
    public void startMySQL() throws AmbitException {
        //if (started) return;
        String command = getCommandFromFile(startFile);
        try {
            File f = new File("../mysql/data/ambit.err");
            if (f.exists()) f.delete();
            
            String run = mysqlPath + "/" + command;
            logger.info(run);
            Timer timerIn = new Timer(true);
	        mysqlProcess = Runtime.getRuntime().exec(run);
	        long timeout = 120000;
	        f = new File("../mysql/data/ambit.err");	        
	        long now = System.currentTimeMillis();
	        while (!f.exists()) {
	            if ((System.currentTimeMillis()-now) > timeout) break;
	        }
	        InputStream in = new FileInputStream(f);
	        gin = new StreamGobbler(in,f.getAbsolutePath());
			timerIn.schedule(new TimerTask() {
	            public void run() {
	               gin.setInterrupted(true);
	               //gerr.setInterrupted(true);
	            }
			},timeout);
			

	        /*
	        InputStream in = mysqlProcess.getInputStream();
	        gin = new StreamGobbler(in,"Stdin");
			timerIn.schedule(new TimerTask() {
	            public void run() {
	               gin.setInterrupted(true);
	               //gerr.setInterrupted(true);
	            }
			},timeout);
			
	        InputStream err = p.getInputStream();
	        gerr = new StreamGobbler(p.getErrorStream(),"Stderr");
	        Timer timerErr = new Timer(true);
			timerErr.schedule(new TimerTask() {

	            public void run() {
	               gerr.setInterrupted(true);
	               gin.setInterrupted(true);
	            }
			},timeout);
			*/
			//gin.run();
			//gerr.start();

			//started = !gin.isInterrupted();
			gin.run();
			in.close();
			started = !gin.isInterrupted();
	        if (!started)
	            throw new AmbitIOException("Can't start MySQL in\t"+timeout/1000 + "s.!  " + mysqlPath);
        } catch (IOException x) {
            logger.error(x);
            started = false;
            throw new AmbitIOException("Can't start MySQL!\t"+mysqlPath);
        //} catch (InterruptedException x) {
           // throw new AmbitException("MySQL start interrupted!");
        
        }
    }
    public void stopMySQL() throws AmbitException {
        if (mysqlProcess == null) return;
        String command = getCommandFromFile(stopFile);
        try {
            String run = mysqlPath + "/" + command;
	        Process p = Runtime.getRuntime().exec(run);
	        //,new String[]{},       new File(mysqlPath));
	        mysqlProcess.waitFor();
	        mysqlProcess = null;
	        logger.info("MySQL stopped\t"+run);
	        started = false;
	        File f = new File("../mysql/data/ambit.err");
	        f.delete();
	        
        } catch (IOException x) {
            logger.error(x);
            throw new AmbitIOException("Can't stop MySQL!\t"+mysqlPath);
        } catch (InterruptedException x) {
            logger.error(x);
            throw new AmbitException("MySQL stop interrupted!");
        }
    }    
    protected String getCommandFromFile(String file) throws AmbitException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
        if (in == null) throw new AmbitIOException(AmbitIOException.MSG_ERRORONOPEN+file);
        String command = null;
        try {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        command = reader.readLine();
	        reader.close();
        } catch (Exception x) {
            throw new AmbitIOException(AmbitIOException.MSG_ERRORONOPEN + file);
        }
        return command;
    }
    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public void run() {
        try { 
        stopMySQL();
        } catch (Exception x) {
            x.printStackTrace();
        }
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