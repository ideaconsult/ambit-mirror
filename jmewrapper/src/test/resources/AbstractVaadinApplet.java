package org.vaadin.applet;

import java.applet.Applet;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class can be used as base to implement Java Applets that integrate to
 * Vaadin application.
 *
 * The class implements thread that polls for JavaScript (GWT) calls of
 * {@link #execute(String)} and {@link #execute(String, Object[])} methods. This
 * allows function privilege elevation if the applet has been signed
 * accordingly. To support this behavior the inheriting applet should implement
 * the {@link #doExecute(String, Object[])} method.
 *
 * Also the class introduces {@link #vaadinSync()} method for syncing the rest
 *
 * @author Sami Ekblad
 *
 */
public abstract class AbstractVaadinApplet extends Applet {

    private static final long serialVersionUID = -1091104541127400420L;

    protected static final String PARAM_APP_SESSION = "appSession";
    protected static final String PARAM_APP_URL = "appUrl";
    protected static final String PARAM_APPLET_ID = "appletId";
    protected static final String PARAM_PAINTABLE_ID = "paintableId";
    protected static final String PARAM_APP_DEBUG = "appDebug";

    protected static long MAX_JS_WAIT_TIME = 10000;

    private boolean debug = false;

    private JsPollerThread pollerThread;

    private Object pollerLock = new Object[] {};

    public boolean runPoller = true;

    private String applicationURL;

    private String sessionCookie;

    private String paintableId;

    private String appletId;

    @Override
    public void init() {
        setDebug("true".equals(getParameter(PARAM_APP_DEBUG)));
        setAppletId(getParameter(PARAM_APPLET_ID));
        setPaintableId(getParameter(PARAM_PAINTABLE_ID));
        setApplicationURL(getParameter(PARAM_APP_URL));
        setApplicationSessionCookie(getParameter(PARAM_APP_SESSION));

        // Start the poller thread for JS commands
        pollerThread = new JsPollerThread();
        pollerThread.start();
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
     * Stop the poller and destroy the applet.
     *
     */
    @Override
    public void destroy() {
        runPoller = false;
        super.destroy();
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

    /**
     * Thread for polling incoming JavaScript commands. Threading is used to
     * change the call stack. If an applet function is invoked from JavaScript
     * it will always use JavaScript permissions regardless of applet signing.
     *
     * This thread allows commands to be sent to the applet and executed with
     * the applet's privileges.
     *
     * @author Sami Ekblad
     */
    public class JsPollerThread extends Thread {

        private static final long POLLER_DELAY = 100;
        private String jsCommand;
        private Object[] jsParams;

        @Override
        public void run() {
            debug("Poller thread started.");
            while (runPoller) {

                // Check if a command was received
                String cmd = null;
                Object[] params = null;
                synchronized (pollerLock) {
                    if (jsCommand != null) {
                        cmd = jsCommand;
                        params = jsParams;
                        jsCommand = null;
                        jsParams = null;
                        debug("Received JavaScript command '" + cmd + "'");
                    }
                }

                if (cmd != null) {
                    doExecute(cmd, params);
                }

                try {
                    Thread.sleep(POLLER_DELAY);
                } catch (InterruptedException e) {
                }
            }
            debug("Poller thread stopped.");
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
                        new Object[] { AbstractVaadinApplet.this });

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

    /**
     * Execute method that should be invoked from a JavaScript. This invokes a
     * second thread (with applet's permission) to execute the command.
     *
     * @param command
     * @param params
     */
    public void execute(String command) {
        execute(command, null);
    }

    /**
     * Execute method that should be invoked from a JavaScript. This invokes a
     * second thread (with applet's permission) to execute the command.
     *
     * @param command
     * @param params
     */
    public void execute(String command, Object[] params) {
        if (pollerThread == null) {
            debug("Poller thread stopped. Cannot execute: '" + command + "'");
            return;
        }
        synchronized (pollerLock) {
            pollerThread.jsCommand = command;
            pollerThread.jsParams = params;
        }
    }

    /**
     * Function to to actually execute a specific command.
     *
     * The inheriting applet must implement this to execute commands sent from
     * JavaScript.
     *
     * Implementation may be empty if no JavaScript initiated commands are
     * supported.
     *
     * @param command
     */
    protected abstract void doExecute(String command, Object[] params);

}