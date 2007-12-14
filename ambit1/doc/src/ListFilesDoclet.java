/**
 * <b>Filename</b> ListFilesDoclet.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-24
 * <b>Project</b> ambit
 */
//package ambit.doc;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

/**
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-24
 */
public class ListFilesDoclet {
    private final String javaDocModuleTag = "ambit.module";
    private Hashtable ambitPackages;


    public ListFilesDoclet() {
        ambitPackages = new Hashtable();
    }

    private String toAPIPath(String className) {
        StringBuffer sb = new StringBuffer();
        
        for (int i=0; i<className.length(); i++) {
            if (className.charAt(i) == '.') {
                sb.append('/');
            } else {
                sb.append(className.charAt(i));
            }
        }
        return sb.toString();
    }
    /**
     * 
     * @param className
     * @return third level package
     */
    private String toPackageName(ClassDoc classDoc) {
        String className = classDoc.qualifiedName();
        ClassDoc s = classDoc.superclass();
        if ((s != null) && ((s.name().equals("CoreApp")))) {
            System.out.println(s.name());
            return classDoc.name();
        } else {    
        
	        int p1 = className.indexOf(".",0);
	        int p2 = className.indexOf(".",p1+1);
	        if (className.substring(p1+1,p2).equals("stats")) return "stats";

	        int p3 = className.indexOf(".",p2+1);
	        if (p3 > 0) 
		        return className.substring(p1+1,p3);
	        else
		        return className.substring(p1+1,p2);
        }     
    }
    
    public void process(RootDoc root) throws IOException {
        processPackages(root.specifiedPackages());

        // output information in .javafiles and .classes files
        Enumeration keys = ambitPackages.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            
            // create one file for each cdk package = key
            PrintWriter outJava = new PrintWriter((Writer)new FileWriter(key + ".javafiles"));
            PrintWriter outClass = new PrintWriter((Writer)new FileWriter(key + ".classes"));
            Vector packageClasses = (Vector)ambitPackages.get(key);
            Enumeration classes = packageClasses.elements();
            while (classes.hasMoreElements()) {
                String packageClass = (String)classes.nextElement();
                outJava.println(toAPIPath(packageClass) + ".java");
                outClass.println(toAPIPath(packageClass) + "*.class");
            }
            outJava.flush(); outJava.close();
            outClass.flush(); outClass.close();
        }
    }

    private void processPackages(PackageDoc[] pkgs) throws IOException {
        for (int i=0; i < pkgs.length; i++) {
            processClasses(pkgs[i].allClasses());
        }
    }

    private void addClassToCDKPackage(String packageClass, String ambitPackageName) {
        Vector packageClasses = (Vector)ambitPackages.get(ambitPackageName);
        if (packageClasses == null) {
            packageClasses = new Vector();
            ambitPackages.put(ambitPackageName, packageClasses);
        }
        packageClasses.addElement(packageClass);
    }
    
    private void processClass(ClassDoc classDoc) throws IOException {
        String className = classDoc.qualifiedName();
        // first deal with modules
        //Tag[] tags = classDoc.tags(javaDocModuleTag);
        addClassToCDKPackage(className, toPackageName(classDoc));
    }
    
    private void processClasses(ClassDoc[] classes) throws IOException {
        for (int i=0; i<classes.length; i++) {
            ClassDoc doc = classes[i];
            processClass(doc);
        }
    }

    public static boolean start(RootDoc root) {
        try {
            ListFilesDoclet doclet = new ListFilesDoclet();
            doclet.process(root);
            return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
            return false;
        }
    }

}

