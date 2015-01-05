package org.wiztools.restclient.ui;

import java.util.List;
import java.util.Map;

public class RequestHeaderTablePanel extends TwoColumnTablePanel {

    public RequestHeaderTablePanel(final String[] title, final RESTUserInterface ui, final List<String> autocompleteStringsForKeys, final Map<String, List<String>> autocompleteStringsForValues) {
        super(title, ui, autocompleteStringsForKeys, autocompleteStringsForValues);
    }
}
