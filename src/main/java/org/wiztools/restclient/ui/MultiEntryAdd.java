/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient.ui;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Subhash
 */
public interface MultiEntryAdd {
    public void add(Map<String, String> keyValuePair, List<String> invalidLines);
}
