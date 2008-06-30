package nplugins.core;


import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * A singleton class with static method to provide some sort of introspection :)
 *  <br>
 * This is done by searching for classes implementing given interface in all .jar files in a user
 * defined directory.
 * 
 * @author Nina Jeliazkova nina@acad.bg <b>Modified</b> 2005-10-18
 */
public class Introspection {
	protected final static String pref_key = "/nplugins";
	protected final static String pref_path = "/classpath";
	protected static String defaultClassPath = ".,dist,ext";
	protected static PluginClassPath pluginsClassPath=null;
	

	protected static org.apache.log4j.Category logger;

	protected static PluginsClassLoader loader;

	protected Introspection() {
		super();
		loader = null;
	}

    public static PluginClassPath getDefaultDirectories() throws IntrospectionException{
    	if (pluginsClassPath == null) 
    		try {
    			pluginsClassPath = new PluginClassPath();
    		} catch (Exception x) {
    			throw new IntrospectionException(x);
    		}
    	return pluginsClassPath;
    }


	/**
	 * this is just a test for the concept of introspection
	 */
	public static void listBaseTypes(Class cls, String prefix) {
		if (cls == Object.class)
			return;
		Class[] itfs = cls.getInterfaces();

		for (int n = 0; n < itfs.length; n++) {
			//System.out.println(prefix + "implements " + itfs[n]);
			listBaseTypes(itfs[n], prefix + "\t");
		}
		Class base = cls.getSuperclass();
		if (base == null)
			return;
		//System.out.println(prefix + "extends " + base);
		listBaseTypes(base, prefix + "\t");
	}

	/**
	 * Verifies if a class implements an interface
	 * 
	 * @param className -
	 *            the name of the class to be verified
	 * @param interfaceName -
	 *            the name of the interface to be searched for
	 * @return Class
	 */
	public static Class implementsInterface(String className,
			String interfaceName) throws IntrospectionException {
		try {
			Class clazz = Class.forName(className);
			int modifier = clazz.getModifiers();
			if (Modifier.isAbstract(modifier))
				return null;
			else if (Modifier.isInterface(modifier))
				return null;
			return implementsInterface(clazz, interfaceName);
		} catch (ClassNotFoundException x) {
			//
			if (loader != null) {
				try {
					//replacement reccomended by  http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6500212
					//Class clazz = loader.loadClass(className);
					Class clazz = Class.forName(className, true, loader);
					int modifier = clazz.getModifiers();
					if (Modifier.isAbstract(modifier))
						return null;
					else if (Modifier.isInterface(modifier))
						return null;
					return implementsInterface(clazz, interfaceName);
				} catch (ClassNotFoundException xx) {

					throw new IntrospectionException(xx);
				}
			}
			// x.printStackTrace();
			return null;
		} catch (Exception x) {
			throw new IntrospectionException(x);
		}
	}

	/**
	 * Verifies if a class implements an interface
	 * 
	 * @param clazz -
	 *            the class to be verified
	 * @param interfaceName -
	 *            the name of the interface to be searched for
	 * @return Class
	 */
	public static Class implementsInterface(Class clazz, String interfaceName) {
		Class[] interfaces = clazz.getInterfaces();

		for (int i = 0; i < interfaces.length; i++)
			if (interfaces[i].getName().equals(interfaceName))
				return clazz;

		// try base class
		Class base = clazz.getSuperclass();
		if (base == null)
			return null;
		else
			return implementsInterface(base, interfaceName);

	}
	public static File[] enumerateJars(File directory) throws IntrospectionException {
		if (directory == null) throw new IntrospectionException("Directory not assigned");
		try {
			if (logger != null)
				logger.info("Looking for .jar files in\t" + directory.getAbsolutePath());
			return directory.listFiles(new FileFilter() {
				public boolean accept(File arg0) {
					//return arg0.getName().toLowerCase().endsWith(".jar");
				    return arg0.getName().toLowerCase().endsWith(".jar"); 
/* 
					arg0.getName().toLowerCase().startsWith("tox");
					*/				
				}
			});
		} catch (Exception x) {
			x.printStackTrace();
			throw new IntrospectionException(x);
		}
	}
	/**
	 * Adds URLS of jars in default directories to the class loader.
	 * Calls {@link #configureURLLoader(File directory)} for each of {@link #pluginsClassPath} directories.
	 * @param classLoader
	 */
	public static void configureURLLoader(ClassLoader classLoader) throws  IntrospectionException {
		setLoader(classLoader);
		for (int i=0;i < getDefaultDirectories().size();i++) {
			try {
				configureURLLoader(new File(getDefaultDirectories().get(i)));
			} catch (IntrospectionException x) {
				x.printStackTrace();
			}
		}		

	}	
	/**
	 * Adds URLS of jars in specified directory to the class loader.
	 * @param directory
	 * @throws IntrospectionException
	 */
	public static void configureURLLoader(File directory) throws IntrospectionException {
		if (loader ==null) throw new IntrospectionException("Class loader not set!");
		File[] jars = enumerateJars(directory);
		if (jars == null) return;
		for (int i = 0; i < jars.length; i++)
			try {
				loader.addURL(jars[i].toURL());
			} catch (MalformedURLException x) {
				if (logger != null)	logger.error(x);
				else x.printStackTrace();
			}
	}	
	/**
	 * Adds URLS of jars specified in File[] to the class loader.
	 * @param jars
	 * @throws IntrospectionException
	 */
	public static void configureURLLoader(File[] jars) throws IntrospectionException {
		if (loader ==null) throw new IntrospectionException("Class loader not set!");
		for (int i = 0; i < jars.length; i++)
			try {
				loader.addURL(jars[i].toURL());
			} catch (MalformedURLException x) {
				if (logger != null)	logger.error(x);
				else x.printStackTrace();

			}
	}
	/**
	 * Finds classes implementing an interface in all .jar files in a user
	 * defined directory.
	 * 
	 * @param directory -
	 *            the directory with jar files to be searched for
	 * @param interfaceName -
	 *            the name of the interface to be searched for
	 * @return PluginsPackageEntries of String , each item is a class name
	 */

	public static PluginsPackageEntries implementInterface(ClassLoader classLoader,
			File directory, String interfaceName) throws IntrospectionException {
		setLoader(loader);
		File[] files = enumerateJars(directory);

		PluginsPackageEntries module = new PluginsPackageEntries();
		if (files == null)
			return module;
		for (int i = 0; i < files.length; i++)

			try {
				if (logger != null)
					logger.info("Inspecting jar file\t" + files[i]+ "\t"+ files[i].toURL());
				JarFile jar = new JarFile(files[i]);
				Enumeration entries = jar.entries();
				if (loader != null)
					loader.addURL(files[i].toURL());
				while (entries.hasMoreElements()) {
					JarEntry entry = (JarEntry) entries.nextElement();
					if (!entry.getName().endsWith("class"))
						continue;

					String name = entry.getName();

					name = name.replaceAll("/", ".").substring(0,
							name.indexOf(".class"));

					Class rule = Introspection.implementsInterface(name,
							interfaceName);

					if (rule != null) {
						logger.debug("Class\t" + name+ "\timplements\t"+interfaceName+ "\tYES");
						try {
							module.add(new PluginPackageEntry(name,files[i].getAbsolutePath(),jar));
						} catch (Exception x) {
							if (logger != null)
								logger.error(x);
							else x.printStackTrace();
						}
					} else
						if (logger != null)
						logger.debug("Class\t" + name + "\timplements\t"+
								interfaceName+ "\tNO");

				}

			} catch (IOException x) {
				if (logger != null)
					logger.error(x);
				else x.printStackTrace();
			}
		return module;

	}


	/**
	 * This method is the core of nanoplugins extension mechanism.
	 * Looks for classes that implement interface given by interfacename parameter. 
	 * Returns a list with class names.
	 * Jar files within the following directories are analyzed: <ul>
	 * <li>The directory from where the application was started (current directory "."). 
	 * <li>The directory "dist" below the current directory.
	 * <li>The directory "ext" below the current directory.
	 * </ul>
	 * @param classLoader
	 * @param interfacename  the 
	 * @return a list with available class names , implementing the interface{@link ArrayList}
	 */
	public static PluginsPackageEntries getAvailableTypes(ClassLoader classLoader,String interfacename) throws IntrospectionException {
		setLoader(classLoader);
		PluginsPackageEntries listAll = null;
		for (int i=0;i < getDefaultDirectories().size();i++) {
			try {
				PluginsPackageEntries list = implementInterface(classLoader, 
						new File(getDefaultDirectories().get(i)),
						interfacename);
				if (list.size() != 0) {
					if (listAll == null) listAll = new PluginsPackageEntries();
					listAll.addAll(list);
				}
			} catch (IntrospectionException x) {
				x.printStackTrace();
			}
		}		
		return listAll;

	}

	/**
	 * Creates object given the class name
	 * 
	 * @param className
	 * @return Object 
	 */
	protected static Object createObject(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class classDefinition = Class.forName(className);
		return classDefinition.newInstance();

	}

	public static Object loadCreateObject(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (loader == null)
			return createObject(className);
		Class classDefinition = loader.loadClass(className);
		return classDefinition.newInstance();

	}

	public static ClassLoader getLoader() {
		return loader;
	}

	public static void setLoader(ClassLoader classLoader) throws IntrospectionException {
		//System.out.println("Current loader "+loader);
		//System.out.println("new loader "+classLoader);
		if ((loader == null) || !(loader instanceof PluginsClassLoader)) {
			URL[] defaultURL = new URL[getDefaultDirectories().size()];
			for (int i = 0; i < defaultURL.length; i++) {
				try {

					 //URL u = new URL("jar", "", plugins[i] + "!/");
					 defaultURL[i] = new URL("jar", "",getDefaultDirectories().get(i) + "!/");
					//defaultURL[i] = new URL(defaultLocation[i]);

				} catch (MalformedURLException x) {
					x.printStackTrace();
				}
			}
			
			loader = new PluginsClassLoader(defaultURL, classLoader);
			System.out.println("Now loader "+loader);
		}
		
	}
	/**
	 * Loads an object from an InputStream
	 * Uses Java serialization mechanism
	 * @param stream
	 * @param newTitle
	 * @return a decision tree {@link IDecisionMethod}
	 */
    public static Object loadObject(InputStream stream, String newTitle) throws IntrospectionException{
		try {
			
		    PluginsObjectInputStream in = new PluginsObjectInputStream(stream);
		    
		    Object object =  in.readObject();

    		return object;
		} catch (Exception e) {
			throw new IntrospectionException("Error when loading "+newTitle,e);

		}
   }    

    public static void saveObjectXML(Object method, OutputStream out) throws IntrospectionException {
        Thread.currentThread().setContextClassLoader(Introspection.getLoader());
        XMLEncoder encoder = new XMLEncoder(out);
        //use this for enum types
        //encoder.setPersistenceDelegate( enum type, new TypeSafeEnumPersistenceDelegate() );
       
        encoder.writeObject(method);
        encoder.close();     	
    	
    }
    public static Object loadObjectXML(InputStream stream, String newTitle)  throws IntrospectionException {
		try {
			System.out.println("Classloader "+loader);
			if (loader == null) {
				setLoader(null); //this will trigger creation of the right classloader			
			}
			Thread.currentThread().setContextClassLoader(loader);

			ExceptionListener el = new ExceptionListener() {
				
				public void exceptionThrown(Exception e) {

					e.printStackTrace();
				}
			};
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(stream),null,el,loader);
			Object object = decoder.readObject();
			decoder.close();

    		return object;
		} catch (Exception e) {
			throw new IntrospectionException("Error when loading the decision tree "+newTitle,e);
		}
   }
	public static org.apache.log4j.Category getLogger() {
		return logger;
	}
	public static void setLogger(org.apache.log4j.Category logger) {
		Introspection.logger = logger;
	}  
         
}


class TypeSafeEnumPersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo( Object oldInstance, Object newInstance ) {
        return oldInstance == newInstance;
    }

    protected Expression instantiate( Object oldInstance, Encoder out ) {
        Class type = oldInstance.getClass();
        if ( !Modifier.isPublic( type.getModifiers() ) )
            throw new IllegalArgumentException( "Could not instantiate instance of non-public class: " + oldInstance );

        for ( Field field : type.getFields() ) {
            int mod = field.getModifiers();
            if ( Modifier.isPublic( mod ) && Modifier.isStatic( mod ) && Modifier.isFinal( mod ) && ( type == field.getDeclaringClass() ) ) {
                try {
                    if ( oldInstance == field.get( null ) )
                        return new Expression( oldInstance, field, "get", new Object[]{null} );
                } catch ( IllegalAccessException exception ) {
                    throw new IllegalArgumentException( "Could not get value of the field: " + field, exception );
                }
            }
        }
        throw new IllegalArgumentException( "Could not instantiate value: " + oldInstance );
    }
}