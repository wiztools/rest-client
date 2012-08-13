/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.bean;

/**
 *
 * @author Subhash
 */
public class TestException extends Exception {
    public TestException(final String msg, final Throwable err){
        super(msg, err);
    }
}
