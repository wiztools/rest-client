package org.wiztools.restclient.ui;

import java.util.List;
import org.wiztools.commons.MultiValueMap;

/**
 *
 * @author Subhash
 */
interface MultiEntryAdd {
    public void add(MultiValueMap<String, String> keyValuePair, List<String> invalidLines);
}
