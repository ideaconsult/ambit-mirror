import java.awt.Event;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;





/**
 * JME is defined in an unnamed package, the only way to access it is within unnamed package...
 * <pre>It is a compile time error to import a type from the unnamed package.</pre>
 * @author nina
 *
 */
public class JMEAppletWrapper extends JME {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2664315767096961855L;
    protected static final String PARAM_APP_SESSION = "appSession";
    protected static final String PARAM_APP_URL = "appUrl";
    protected static final String PARAM_APPLET_ID = "appletId";
    protected static final String PARAM_PAINTABLE_ID = "paintableId";
    protected static final String PARAM_APP_DEBUG = "appDebug";
    protected static long MAX_JS_WAIT_TIME = 10000;
    
    private boolean debug = false;
    private String applicationURL;
    private String sessionCookie;
    private String paintableId;
    private String appletId;

	protected enum commands {
		smiles,
		nonisomericSmiles,
		molFile,
		jmeFile,
		readMolecule,
		readMolFile,
		readSmiles,
		readTopology,
		setText,
		setTemplate,
		showAtomNumbers,
		reset,
		getpreviousMolecule,
		hasPrevious,
		options
	}

	public JMEAppletWrapper() {
		super();
	}
	
	@Override
	public void init() {
        setDebug("true".equals(getParameter(PARAM_APP_DEBUG)));
        setAppletId(getParameter(PARAM_APPLET_ID));
        setPaintableId(getParameter(PARAM_PAINTABLE_ID));
        setApplicationURL(getParameter(PARAM_APP_URL));
        setApplicationSessionCookie(getParameter(PARAM_APP_SESSION));
		super.init();
	}
	
	
	@Override
	public boolean keyUp(Event evt, int key) {
		boolean ok = super.keyUp(evt, key);
		vaadinUpdateMol();
		return ok;
	}
	@Override
	public boolean mouseUp(Event arg0, int arg1, int arg2) {

		boolean ok = super.mouseUp(arg0, arg1, arg2);
		vaadinUpdateMol();
		return ok;
	}
	
	public void vaadinUpdateMol() {
		//String smiles = smiles();
		String molFile = molFile();
		molFile = molFile.replace("\n", "|");
		//try {vaadinUpdateVariable(commands.smiles.toString(),smiles , true);} catch (Exception x) {x.printStackTrace();}
		try {vaadinUpdateVariable(commands.molFile.toString(),molFile.replace("\n\r", "|") , true);} catch (Exception x) {x.printStackTrace();}
	}	

	  /**
     * Set the id of the applet in DOM.
     *
     * @param paintableId
     */
    private void setAppletId(String appletId) {
        this.appletId = appletId;
        debug("appletId=" + appletId);
    }

    /**
     * Get the id of the applet in DOM.
     *
     * @return The id of this applet in the Vaadin application DOM document.
     */
    protected String getAppleteId() {
        return appletId;
    }

    /**
     * Set the paintable id of the applet widget.
     *
     * @param paintableId
     *            The id of this applet widget in the Vaadin application.
     */
    private void setPaintableId(String paintableId) {
        this.paintableId = paintableId;
        debug("paintableId=" + paintableId);
    }

    /**
     * Get the paintable id of the applet widget.
     *
     * @return The id of this applet widget in the Vaadin application.
     */
    protected String getPaintableId() {
        return paintableId;
    }

    /**
     * Set the application session cookie. Called from init.
     *
     * @param appUrl
     */
    private void setApplicationSessionCookie(String appSessionCookie) {
        sessionCookie = appSessionCookie;
        debug("sessionCookie=" + sessionCookie);
    }

    /**
     * Get the application session cookie.
     *
     * @return The session cookie needed to communicate back to the Vaadin
     *         application instance.
     */
    protected String getApplicationSessionCookie() {
        return sessionCookie;
    }

    /**
     * Set the application URL. Called from init.
     *
     * @param appUrl
     */
    private void setApplicationURL(String appUrl) {
        applicationURL = appUrl;
        debug("applicationURL=" + applicationURL);
    }

    /**
     * Get the application URL.
     *
     * @return The URL of the Vaadin application.
     */
    protected String getApplicationURL() {
        return applicationURL;
    }

    /**
     * Debug a string if debugging has been enabled.
     *
     * @param string
     */
    protected void debug(String string) {
        if (!isDebug()) {
            return;
        }
        System.err.println("debug: " + string);
    }

    /**
     * Invokes vaadin.forceSync that synchronizes the client-side GWT
     * application with server.
     *
     */
    public void vaadinSync() {
        jsCallAsync("vaadin.forceSync()");
    }

    /**
     * Invokes vaadin.appletUpdateVariable sends a variable to server.
     *
     * @param variableName
     * @param newValue
     * @param immediate
     */
    public void vaadinUpdateVariable(String variableName, boolean newValue,
            boolean immediate) {
        jsCallAsync("vaadin.appletUpdateBooleanVariable('" + getPaintableId()
                + "','" + variableName + "'," + newValue + "," + immediate
                + ")");
    }

    /**
     * Invokes vaadin.appletUpdateVariable sends a variable to server.
     *
     * @param variableName
     * @param newValue
     * @param immediate
     */
    public void vaadinUpdateVariable(String variableName, int newValue,
            boolean immediate) {
        jsCallAsync("vaadin.appletUpdateIntVariable('" + getPaintableId()
                + "','" + variableName + "'," + newValue + "," + immediate
                + ")");
    }

    /**
     * Invokes vaadin.appletUpdateVariable sends a variable to server.
     *
     * @param variableName
     * @param newValue
     * @param immediate
     */
    public void vaadinUpdateVariable(String variableName, double newValue,
            boolean immediate) {
        jsCallAsync("vaadin.appletUpdateDoubleVariable('" + getPaintableId()
                + "','" + variableName + "'," + newValue + "," + immediate
                + ")");
    }

    /**
     * Invokes vaadin.appletUpdateVariable sends a variable to server.
     *
     * @param variableName
     * @param newValue
     * @param immediate
     */
    public void vaadinUpdateVariable(String variableName, String newValue,
            boolean immediate) {
        jsCallAsync("vaadin.appletUpdateStringVariable('" + getPaintableId()
                + "','" + variableName + "','" + newValue + "'," + immediate
                + ")");
    }
    

    /*
     * TODO: Variable support missing for: String[], Object[], long, float,
     * Map<String,Object>, Paintable
     */

    /**
     * Execute a JavaScript asynchronously.
     *
     * @param command
     */
    public void jsCallAsync(String command) {
        JSCallThread t = new JSCallThread(command);
        t.start();
    }

    /**
     * Execute a JavaScript synchronously.
     *
     * @param command
     * @throws InterruptedException
     */
    public Object jsCallSync(String command) throws InterruptedException {
        JSCallThread t = new JSCallThread(command);
        t.start();
        t.join(MAX_JS_WAIT_TIME);
        return t.getResult();
    }

	public void execute(String command) {
		execute(command,null);
	}
	public void execute(String command, Object[] params) {
		try {
		commands cmd = commands.valueOf(command);
			switch (cmd) {
			case smiles: {
				vaadinUpdateVariable("smiles", smiles(), true);
				break;
			}
			case nonisomericSmiles: {
				vaadinUpdateVariable("nonisomericSmiles", nonisomericSmiles(), true);
				break;
			}
			case molFile: {
				vaadinUpdateVariable("molFile", molFile(), true);
				break;
			}
			case jmeFile: {
				vaadinUpdateVariable("jmeFile", jmeFile(), true);
				break;
			}
			case readMolecule: {
				if (params.length>0)
					readMolecule(params[0].toString());
				break;
			}
			case readMolFile: {
				if (params.length>0)
					readMolFile(params[0].toString());
				break;				
			}
			case readSmiles: {
				if (params.length>0)
					readSmiles(params[0].toString());
				break;	
			}
/*
			case readTopology: {
				if (params.length>0)
					readTopology(params[0].toString());
				break;					
			}
			*/
			case setText: {
				if (params.length>0)
					setText(params[0].toString());
				break;					
			}		
			/*
			case setTemplate: {
				if (params.length>0)
					setTemplate(params[0].toString());
				break;					
			}			
			*/	
			case showAtomNumbers: {
				showAtomNumbers();
				break;					
			}
			case reset: {
				reset();
				break;					
			}		
			case options: {
				if (params.length>0)
					options(params[0].toString());
				break;					
			}				
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	
	   /**
     * Thread for executing outgoing JavaScript commands. This thread
     * implementation is used to asynchronously invoke JavaScript commands from
     * applet.
     *
     * @author Sami Ekblad
     *
     */
    public class JSCallThread extends Thread {

        private String command = null;
        private Object result = null;
        private boolean success = false;

        /**
         * Constructor
         *
         * @param command
         *            Complete JavaScript command to be executed including
         */
        public JSCallThread(String command) {
            super();
            // SE: We need to remove all line changes to avoid exceptions
            this.command = command.replaceAll("\n", " ");
        }

        @Override
        public void run() {

            debug("Call JavaScript '" + command + "'");

            String jscmd = command;

            try {
                Method getWindowMethod = null;
                Method evalMethod = null;
                Object jsWin = null;
                Class<?> c = Class.forName("netscape.javascript.JSObject");
                Method ms[] = c.getMethods();
                for (int i = 0; i < ms.length; i++) {
                    if (ms[i].getName().compareTo("getWindow") == 0) {
                        getWindowMethod = ms[i];
                    } else if (ms[i].getName().compareTo("eval") == 0) {
                        evalMethod = ms[i];
                    }

                }

                // Get window of the applet
                jsWin = getWindowMethod.invoke(c,
                        new Object[] { JMEAppletWrapper.this });

                // Invoke the command
                result = evalMethod.invoke(jsWin, new Object[] { jscmd });

                if (!(result instanceof String) && result != null) {
                    result = result.toString();
                }
                success = true;
                debug("JavaScript result: " + result);
            }

            catch (InvocationTargetException e) {
                success = true;
                result = e;
                debug(e);
            } catch (Exception e) {
                success = true;
                result = e;
                debug(e);
            }
        }

        /**
         * Get result of the execution.
         *
         * @return
         */
        public Object getResult() {
            return result;
        }

        /**
         * Get the result of execution as string.
         *
         * @return
         */
        public String getResultAsString() {
            if (result == null) {
                return null;
            }
            return (String) (result instanceof String ? result : result
                    .toString());
        }

        /**
         * Get the exception that occurred during JavaScript invocation.
         *
         * @return
         */
        public Exception getException() {
            return (Exception) (result instanceof Exception ? result : null);
        }

        /**
         * Check if the JavaScript invocation was an success.
         *
         * @return
         */
        public boolean isSuccess() {
            return success;
        }

    }
    
    public void setDebug(boolean debug) {
        boolean change = this.debug != debug;
        this.debug = debug;
        if (change) {
            debug("" + isDebug());
        }
    }

    public void debug(Exception e) {
        if (!isDebug()) {
            return;
        }
        System.err.println("debug: Exception " + e);
        e.printStackTrace();
    }

    public boolean isDebug() {
        return debug;
    }
    
}
