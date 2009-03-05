package ambit2.ambitxt;


import nplugins.shell.application.SplashWindow;

/**
 * Splash window starting the application {@link AmbitXT}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Jul 9, 2006
 */
public class Splasher {


    public static void main(String[] args) {
        SplashWindow.splash(Splasher.class.getResource("/images/ambit-256x256.png"));
        SplashWindow.invokeMain("ambit2.ambitxt.AmbitXT", args);
        SplashWindow.disposeSplash();
    }

}
