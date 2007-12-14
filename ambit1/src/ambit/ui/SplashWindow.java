/* SplashWindow.java
 * Author: Nina Jeliazkova
 * Date: Jul 9, 2006 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
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

package ambit.ui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Splash window.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Jul 9, 2006
 */
public class SplashWindow extends Window {
    protected Image image;
    protected static SplashWindow instance;
    private boolean paintCalled = false;

    /**
     * 
     */
    private SplashWindow(Frame parent, Image image) {
        super(parent);
        this.image = image;

        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image,0);
        try {
            mt.waitForID(0);
        } catch(InterruptedException ie){}

        
        // Center the window on the screen
        int imgWidth = image.getWidth(this);
        int imgHeight = image.getHeight(this);
        setSize(imgWidth, imgHeight);
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(
        (screenDim.width - imgWidth) / 2,
        (screenDim.height - imgHeight) / 2
        );
        
        // Users shall be able to close the splash window by
        // clicking on its display area. This mouse listener
        // listens for mouse clicks and disposes the splash window.
        MouseAdapter disposeOnClick = new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                // Note: To avoid that method splash hangs, we
                // must set paintCalled to true and call notifyAll.
                // This is necessary because the mouse click may
                // occur before the contents of the window
                // has been painted.
                synchronized(SplashWindow.this) {
                    SplashWindow.this.paintCalled = true;
                    SplashWindow.this.notifyAll();
                }
                dispose();
            }
        };
        addMouseListener(disposeOnClick);         
    }

    public static void invokeMain(String className, String[] args) {
        try {
            Class.forName(className)
                .getMethod("main", new Class[] {String[].class})
                .invoke(null, new Object[] {args});
        } catch (Exception e) {
            InternalError error =

                new InternalError("Failed to invoke main method");
            error.initCause(e);
            throw error;
        }
    }
    public static void splash(URL imageURL) {
        if (imageURL != null) {
            splash(Toolkit.getDefaultToolkit().createImage(imageURL));
        }
    }
    



    public void update(Graphics g) {
        paint(g);
    }
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);

        if (! paintCalled) {
            paintCalled = true;
            synchronized (this) { notifyAll(); }
        }

    }


    public static void splash(Image image) {
        if (instance == null && image != null) {
            Frame f = new Frame();

            instance = new SplashWindow(f, image);
            instance.show();


            if (! EventQueue.isDispatchThread()
            && Runtime.getRuntime().availableProcessors() == 1) {

                synchronized (instance) {
                    while (! instance.paintCalled) {
                        try {

                            instance.wait(); 

                        } catch (InterruptedException e) {}
                    }
                }
            }
            
        }
    }
    public static void disposeSplash() {
        if (instance != null) {
            instance.getOwner().dispose();
            instance = null;
        }
    }
}
