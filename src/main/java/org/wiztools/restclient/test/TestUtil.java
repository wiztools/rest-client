/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.test;

import groovy.lang.GroovyClassLoader;
import groovy.util.GroovyTestSuite;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.codehaus.groovy.control.CompilationFailedException;
import org.wiztools.restclient.RequestBean;
import org.wiztools.restclient.ResponseBean;
import org.wiztools.restclient.Util;
import org.wiztools.restclient.View;

/**
 *
 * @author schandran
 */
public class TestUtil {
    
    private static final Logger LOG = Logger.getLogger(TestUtil.class.getName());
    
    public static TestSuite getTestSuite(final RequestBean request, final ResponseBean response)
            throws TestException{
        final String script = request.getTestScript();
        if(Util.isStrEmpty(script)){
            return null;
        }
        try{
            GroovyClassLoader gcl = new GroovyClassLoader();

            Class testClass = gcl.parseClass(script, "__GenRESTTestCase__");

            TestSuite suite = new GroovyTestSuite();

            final RoRequestBean roRequest = new RoRequestBean(request);
            final RoResponseBean roResponse = new RoResponseBean(response);

            Method[] m_arr = testClass.getDeclaredMethods();
            for(int i=0; i<m_arr.length; i++){
                /*
                Test for following:
                 * 1. Modifier should be public
                 * 2. Method should not be abstract
                 * 3. Return type should be void
                 * 4. Method name should start with `test'
                 * 5. Method should not expect any parameter
                 */
                int modifiers = m_arr[i].getModifiers();
                Class retType = m_arr[i].getReturnType();
                String methName = m_arr[i].getName();
                Class[] p_arr = m_arr[i].getParameterTypes();
                if(!Modifier.isPublic(modifiers) ||
                        Modifier.isAbstract(modifiers) ||
                        !retType.equals(Void.TYPE) ||
                        !methName.startsWith("test") ||
                        p_arr.length != 0){
                    continue;
                }
                RESTTestCase test = (RESTTestCase)GroovyTestSuite.createTest(testClass, methName);
                test.setRoRequestBean(roRequest);
                test.setRoResponseBean(roResponse);
                suite.addTest(test);
            }
            return suite;
        }
        catch(CompilationFailedException ex){
            throw new TestException("", ex);
        }
        catch(ClassCastException ex){
            throw new TestException("", ex);
        }
    }

    public static String execute(final TestSuite suite){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TestRunner runner = new TestRunner(new PrintStream(baos));
        TestResult result = runner.doRun(suite);

        byte[] bresult = baos.toByteArray();
        return new String(bresult);
    }
}
