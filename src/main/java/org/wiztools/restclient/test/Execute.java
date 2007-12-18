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
    public static void execute(RequestBean request, ResponseBean response, View view){
        final String script = request.getTestScript();
        if(Util.isStrEmpty(script)){
            return;
        }
        try {
            GroovyClassLoader gcl = new GroovyClassLoader();

            Class testClass = gcl.parseClass(script, "__GenRESTTestCase__");

            RESTTestCase testCase = (RESTTestCase) testClass.newInstance();
            testCase.setRoRequestBean(new RoRequestBean(request));
            testCase.setRoResponseBean(new RoResponseBean(response));
            
            GroovyTestSuite suite = new GroovyTestSuite();
            suite.addTest(testCase);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            TestRunner runner = new TestRunner(new PrintStream(baos));
            TestResult result = runner.doRun(suite);
            byte[] bresult = baos.toByteArray();
            view.doTestResult(new String(bresult));
        } 
        catch(CompilationFailedException ex){
            Logger.getLogger(Execute.class.getName()).log(Level.SEVERE, null, ex);
            view.doError(Util.getStackTrace(ex));
        }
        catch(ClassCastException ex){
            view.doError(Util.getStackTrace(ex));
        }
        catch (InstantiationException ex) {
            Logger.getLogger(Execute.class.getName()).log(Level.SEVERE, null, ex);
            view.doError(Util.getStackTrace(ex));
        }
        catch (IllegalAccessException ex) {
            Logger.getLogger(Execute.class.getName()).log(Level.SEVERE, null, ex);
            view.doError(Util.getStackTrace(ex));
        }
    }
}
