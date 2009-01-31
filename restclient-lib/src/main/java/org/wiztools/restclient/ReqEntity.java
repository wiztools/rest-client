/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

/**
 *
 * @author subwiz
 */
public interface ReqEntity extends Cloneable {

    String getBody();

    String getCharSet();

    String getContentType();

    String getContentTypeCharsetFormatted();

    Object clone();
}
