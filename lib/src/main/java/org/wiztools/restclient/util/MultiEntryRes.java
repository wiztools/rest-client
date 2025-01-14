package org.wiztools.restclient.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiEntryRes {
    private List<String> linesNotMatching = new ArrayList<>();
    private Map<String, String> keyValMap = new LinkedHashMap<>();

    public void addInvalidLine(String line) {
        linesNotMatching.add(line);
    }

    public void addEntry(String key, String value) {
        keyValMap.put(key, value);
    }

    public List<String> getInvalidLines() {
        return linesNotMatching;
    }

    public Map<String, String> getEntries() {
        return keyValMap;
    }
}