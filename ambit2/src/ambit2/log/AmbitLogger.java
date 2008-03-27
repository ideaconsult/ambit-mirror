/*
 * Created on 2005-8-19
 *
 */
package ambit2.log;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;

/**
 * Logging facility
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-8-19
 */
public class AmbitLogger {
    
    private boolean debug = false;
    private boolean tostdout = false;

    private org.apache.log4j.Category log4jLogger;
    private AmbitLogger logger;
    private String classname;

    //private int stackLength;  // NOPMD
    
    /** Default number of StackTraceElements to be printed by debug(Exception). */
    public final int DEFAULT_STACK_LENGTH = 5;

    
    public AmbitLogger() {
        this(AmbitLogger.class);
    }

    /**
     * Constructs a LoggingTool which produces logger lines indicating them to be
     * for the Class of the <code>Object</code>.
     *
     * @param object Object from which the logger messages originate
     */
    public AmbitLogger(Object object) {
        this(object.getClass());
    }
    public static void configureLog4j(boolean console) {

    	if (console) 
    		configureLog4j("/ambit/log/log4.console");
    	else
    		configureLog4j("/ambit/log/log4.properties");
    }
    public static void configureLog4j(String file) {

        try { // NOPMD
            org.apache.log4j.PropertyConfigurator.configure(
            		AmbitLogger.class.getResource(file));
        } catch (NullPointerException e) { // NOPMD
            System.err.println("Properties file not found: " + e.getMessage());
            //e.printStackTrace();
            
        } catch (Exception e) {
        	System.err.println("Unknown error occured: " + e.getMessage());
        	e.printStackTrace();
        }
    }


    public AmbitLogger(Class classInst) {
        this.logger = this;
        //stackLength = DEFAULT_STACK_LENGTH;
        this.classname = classInst.getName();
        try {
            /* Next line is required to not have the compiler trip over the
             * the catch clause later, which in turn is needed on runtime when
             * security is checked, which is with the PluginManager, as it
             * uses the java.net.URLClassLoader. */
            //if (false) throw new ClassNotFoundException(); // NOPMD
            
            log4jLogger = (org.apache.log4j.Category)org.apache.log4j.Category
                                                     .getInstance( classname );
            /*
        } catch (ClassNotFoundException e) {
            tostdout = true;
            logger.debug("Log4J class not found!");
            */
        } catch (NoClassDefFoundError e) {
            tostdout = true;
            logger.debug("Log4J class not found!");
        } catch (NullPointerException e) { // NOPMD
            tostdout = true;
            logger.debug("Properties file not found!");
        } catch (Exception e) {
            tostdout = true;
            logger.debug("Unknown error occured: " + e.getMessage());
        }
        /****************************************************************
         * but some JVMs (i.e. MSFT) won't pass the SecurityException to
         * this exception handler. So we are going to check the JVM
         * version first
         ****************************************************************/
        debug = false;
        String strJvmVersion = System.getProperty("java.version");
        if (strJvmVersion.compareTo("1.2") >= 0) {
          // Use a try {} to catch SecurityExceptions when used in applets
          try {
            // by default debugging is set off, but it can be turned on
            // with starting java like "java -Dambit2.debugging=true"
            if (System.getProperty("ambit2.debugging", "false").equals("true")) {
            	System.out.println("Debug=true");
              debug = true;
            }
            if (System.getProperty("ambit2.debug.stdout", "false").equals("true")) {
              tostdout = true;
            }
          } catch (Exception e) {
        	  System.err.println(e.getMessage());
        	  debug =false;
        	  tostdout = true;
        	  
          }
        }
    }
    
    /**
     * Forces the <code>LoggingTool</code> to configurate the Log4J toolkit.
     * Normally this should be done by the application that uses the CDK library,
     * but is available for convenience.
     */

    /**
     * Outputs system properties for the operating system and the java
     * version. More specifically: os.name, os.version, os.arch, java.version
     * and java.vendor.
     */
    public void dumpSystemProperties() {
        debug("os.name        : " + System.getProperty("os.name"));
        debug("os.version     : " + System.getProperty("os.version"));
        debug("os.arch        : " + System.getProperty("os.arch"));
        debug("java.version   : " + System.getProperty("java.version"));
        debug("java.vendor    : " + System.getProperty("java.vendor"));
    }

    /**
     * Sets the number of StackTraceElements to be printed in DEBUG mode when
     * calling <code>debug(Throwable)</code>.
     * The default value is DEFAULT_STACK_LENGTH.
     *
     * @param length the new stack length
     *
     * @see #DEFAULT_STACK_LENGTH
     */
    public void setStackLength(int length) {
        //this.stackLength = length;
    }
    
    /**
     * Outputs the system property for java.class.path.
     */
    public void dumpClasspath() {
        debug("java.class.path: " + System.getProperty("java.class.path"));
    }

    /**
     * Shows DEBUG output for the Object. If the object is an instanceof
     * Throwable it will output the trace. Otherwise it will use the
     * toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    public void debug(Object object) {
        if (debug) {
            if (object instanceof Throwable) {
                debugThrowable((Throwable)object);
            } else {
            	   log4jLogger.debug(object.toString());
            }
        }
    }
    private void debugThrowable(Throwable problem) {
        if (problem != null) {
            if (problem instanceof Error) {
                debug("Error: " + problem.getMessage());
            } else {
                debug("Exception: " + problem.getMessage());
            }
            java.io.StringWriter stackTraceWriter = new java.io.StringWriter();
            problem.printStackTrace(new PrintWriter(stackTraceWriter));
            String trace = stackTraceWriter.toString();
            try {
                BufferedReader reader = new BufferedReader(new StringReader(trace));
                if (reader.ready()) {
                    String traceLine = reader.readLine();
                    while (reader.ready() && traceLine != null) {
                        debug(traceLine);
                        traceLine = reader.readLine();
                    }
                }
            } catch (Exception ioException) {
                error("Serious error in LoggingTool while printing exception stack trace: " + 
                      ioException.getMessage());
                logger.debug(ioException);
            }
        }
    }
    
    /**
     * Shows ERROR output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    public void error(Object object) {
        if (debug) {
            log4jLogger.debug(object);
        } else log4jLogger.error(object);
    }

    public void error(Object object,Exception x) {
        if (debug) {
        	log4jLogger.error(object);
            log4jLogger.debug(x.toString());
        } else log4jLogger.error(object.toString()+x.toString());
    }
    
    public void fatal(Object object) {
        if (debug) {
            log4jLogger.fatal(object.toString());
        }
    }

    
    public void info(Object object) {
        if (debug) {
        	log4jLogger.info(object.toString());
        }
    }

    /**
     * Shows WARN output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() too and output
     */
    public void warn(Object object) {
        if (debug) {
            log4jLogger.warn(object);
        }
    }

    public boolean isDebugEnabled() {
        return debug;
    }
    
   
}
