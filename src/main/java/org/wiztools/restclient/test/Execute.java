/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.test;

import groovy.lang.GroovyClassLoader;
import groovy.util.GroovyTestSuite;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
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
public class Execute {
    
    private static final Logger LOG = Logger.getLogger(Execute.class.getName());
    
    public static void execute(RequestBean request, ResponseBean response, View view){
        final String script = request.getTestScript();
        if(Util.isStrEmpty(script)){
            return;
        }
        try {
            GroovyClassLoader gcl = new GroovyClassLoader();

            Class testClass = gcl.parseClass(script, "__GenRESTTestCase__");

            TestSuite suite = new GroovyTestSuite();
            
            RESTTestCase testCase = (RESTTestCase) testClass.newInstance();
            testCase.setRoRequestBean(new RoRequestBean(request));
            testCase.setRoResponseBean(new RoResponseBean(response));
            
            suite.addTest(testCase);
            LOG.log(Level.INFO, "Test count: " + suite.countTestCases());
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            TestRunner runner = new TestRunner(new PrintStream(baos));
            TestResult result = runner.doRun(suite);
            
            byte[] bresult = baos.toByteArray();
            view.doTestResult(new String(bresult));
        } 
        catch(CompilationFailedException ex){
            LOG.log(Level.SEVERE, null, ex);
            view.doError(Util.getStackTrace(ex));
        }
        catch(ClassCastException ex){
            view.doError(Util.getStackTrace(ex));
        }
        catch (InstantiationException ex) {
            LOG.log(Level.SEVERE, null, ex);
            view.doError(Util.getStackTrace(ex));
        }
        catch (IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
            view.doError(Util.getStackTrace(ex));
        }
    }
}
