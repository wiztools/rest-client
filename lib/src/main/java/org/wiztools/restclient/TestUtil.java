package org.wiztools.restclient;

import groovy.lang.GroovyClassLoader;
import groovy.util.GroovyTestSuite;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.codehaus.groovy.control.CompilationFailedException;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.*;
import org.wiztools.restclient.util.ConfigUtil;

/**
 *
 * @author schandran
 */
public class TestUtil {
    
    private static final Logger LOG = Logger.getLogger(TestUtil.class.getName());
    
    public static TestSuite getTestSuite(final Request request, final Response response)
            throws TestException{
        final String script = request.getTestScript();
        if(StringUtil.isEmpty(script)){
            return null;
        }
        try{
            GroovyClassLoader gcl = new GroovyClassLoader(TestUtil.class.getClassLoader());
            File[] dependencies = ConfigUtil.getTestDependencies();
            for(File dependency: dependencies) {
                gcl.addClasspath(dependency.getAbsolutePath());
            }

            Class testClass = gcl.parseClass(script, "__GenRESTTestCase__");

            TestSuite suite = new GroovyTestSuite();

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
                test.setRequest(request);
                test.setResponse(response);
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
    
    private static final int getLineNumber(String str){
        Pattern p = Pattern.compile("\\(__GenRESTTestCase__:([0-9]+)\\)");
        Matcher m = p.matcher(str);
        if(m.find()){
            String lineNumberStr = m.group(1);
            return Integer.parseInt(lineNumberStr);
        }
        return 0;
    }

    public static org.wiztools.restclient.bean.TestResult execute(final TestSuite suite){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TestRunner runner = new TestRunner(new PrintStream(baos));
        TestResult result = runner.doRun(suite);
        
        TestResultBean resultBean = new TestResultBean();
        
        final int runCount = result.runCount();
        final int failureCount = result.failureCount();
        final int errorCount = result.errorCount();
        
        resultBean.setRunCount(runCount);
        resultBean.setFailureCount(failureCount);
        resultBean.setErrorCount(errorCount);
        
        if(failureCount > 0){
            List<TestExceptionResult> l = new ArrayList<TestExceptionResult>();
            Enumeration<TestFailure> failures = result.failures();
            while(failures.hasMoreElements()){
                TestFailure failure = failures.nextElement();

                TestExceptionResultBean t = new TestExceptionResultBean();
                t.setExceptionMessage(failure.exceptionMessage());
                t.setLineNumber(getLineNumber(failure.trace()));
                l.add(t);
            }
            resultBean.setFailures(l);
        }
        
        if(errorCount > 0){
            List<TestExceptionResult> l = new ArrayList<TestExceptionResult>();
            Enumeration<TestFailure> errors = result.errors();
            while(errors.hasMoreElements()){
                TestFailure error = errors.nextElement();
                
                TestExceptionResultBean t = new TestExceptionResultBean();
                t.setExceptionMessage(error.exceptionMessage());
                t.setLineNumber(getLineNumber(error.trace()));
                l.add(t);
            }
            resultBean.setErrors(l);
        }

        byte[] bresult = baos.toByteArray();
        resultBean.setMessage(new String(bresult));

        return resultBean;
    }
}
