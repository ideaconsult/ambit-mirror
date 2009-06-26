/* DescriptorEngineProcessorTest.java
 * Author: nina
 * Date: Feb 7, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
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

package ambit2.ambitxt.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import junit.framework.Assert;
import nplugins.core.Introspection;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.qsar.DescriptorEngine;

import ambit2.descriptors.processors.DescriptorEngineProcessor;

public class DescriptorEngineProcessorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() {
		
	}
	
	public void testEngine() throws Exception {
		DescriptorEngineProcessor p = new DescriptorEngineProcessor();
		/*
		for (String s : p.getDescriptorEngine().getAvailableDictionaryClasses())
			System.out.println(s);
		for (Object s : p.getDescriptorEngine().getDescriptorClassNames())
			System.out.println(s);		
		
		for (Object s : p.getDescriptorEngine().getDescriptorInstances())
			System.out.println(s);
			*/
		//ERROR when trying the big ambit2-ambitxt-version.jar !!!!!!!
		/*
		List list = p.getDescriptorEngine().getDescriptorClassNameByPackage("ambit2.descriptors",
				 new String[] {"E://Ideaconsult//AmbitXT-v2.00//ambit//ambit2-descriptors-2.0.0-SNAPSHOT.jar"});
				 */
		List list = getDescriptorClassNameByInterface("org.openscience.cdk.qsar.IMolecularDescriptor",
				 new String[] {"E://Ideaconsult//AmbitXT-v2.00//ambit//ambit2-descriptors-2.0.0-SNAPSHOT.jar"});
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() >0);
		for (Object s : list)
			System.out.println(s);				
	}
	
	public void testExternalClasses() throws Exception {
		
	}
	public static List<String> getDescriptorClassNameByInterface(String interfaceName, String[] jarFileNames) {
		Logger logger = Logger.getLogger(DescriptorEngine.class);

	        if (interfaceName == null || interfaceName.equals(""))
	            interfaceName = "org.opensicence.cdk.qsar.IDescriptor";

	        if (!interfaceName.equals("org.openscience.cdk.qsar.IDescriptor") &&
	                !interfaceName.equals("org.openscience.cdk.qsar.IMolecularDescriptor") &&
	                !interfaceName.equals("IAtomicDescriptor") &&
	                !interfaceName.equals("IBondDescriptor")) return null;

	        String[] jars;
	        if (jarFileNames == null) {
	            String classPath = System.getProperty("java.class.path");
	            jars = classPath.split(File.pathSeparator);
	        } else {
	            jars = jarFileNames;
	        }

	        List<String> classlist = new ArrayList<String>();
	        for (int i = 0; i < jars.length; i++) {
	            logger.debug("Looking in " + jars[i]);
	            JarFile jarFile;
	            try {
	                jarFile = new JarFile(jars[i]);
	                Enumeration enumeration = jarFile.entries();
	                while (enumeration.hasMoreElements()) {
	                    JarEntry jarEntry = (JarEntry) enumeration.nextElement();
	                    if (jarEntry.toString().indexOf(".class") != -1) {
	                        String className = jarEntry.toString().replace('/', '.').replaceAll(".class", "");
	                        if (className.indexOf('$') != -1) continue;

	                        Class klass = null;
	                        try {
	                        	System.out.println(className);
	                            klass = Introspection.getClassDefinition(className);
	                        } catch (InstantiationException x) {
	                        	logger.debug(x);	                            
	                        } catch (IllegalAccessException x) {
	                        	logger.debug(x);
	                        } catch (ClassNotFoundException cnfe) {
	                            logger.debug(cnfe);
	                        } catch (NoClassDefFoundError ncdfe) {
	                            logger.debug(ncdfe);
	                        } catch (UnsatisfiedLinkError ule) {
	                            logger.debug(ule);
	                        } catch (Exception x) {

	                        	logger.debug(x);
	                        }
	                        if (klass == null) continue;

	                        // check that its not abstract or an interface
	                        int modifer = klass.getModifiers();
	                        if (Modifier.isAbstract(modifer) ||
	                                Modifier.isInterface(modifer)) continue;

	                        // get the interfaces implemented and see if one matches the one we're looking for
	                        Class[] interfaces = klass.getInterfaces();
	                        for (int k = 0; k < interfaces.length; k++) {
	                            if (interfaces[k].getName().equals(interfaceName)) {
	                                classlist.add(className);
	                                break;
	                            }
	                        }
	                    }
	                }
	            } catch (IOException e) {
	                logger.error("Error opening the jar file: " + jars[i]);
	                logger.debug(e);
	            }
	        }
	        return classlist;
	    }
			
}
