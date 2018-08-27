package org.wiztools.restclient.util;

import org.wiztools.commons.MultiValueMap;

public class MultiEntryUtil {
    public static MultiEntryRes parse(String str) {
        MultiEntryRes res = new MultiEntryRes();

        String[] line_arr = str.split("\\n");
        for (String line: line_arr) {
            int index = line.indexOf(':');
            if ((index > -1) && (index != 0) && (index != (line.length() - 1))) {
                String key = line.substring(0, index);
                String value = line.substring(index + 1);
                key = key.trim();
                value = value.trim();
                if ("".equals(key) || "".equals(value)) {
                    res.addInvalidLine(line);
                } else {
                    res.addEntry(key, value);
                }
            } else {
                if (!"".equals(line.trim())) { // Add only non-blank line
                    res.addInvalidLine(line);
                }
            }
        }

        return res;
    }

    public static String join(MultiValueMap<String, String> map) {
        StringBuilder out = new StringBuilder();
        for(String key: map.keySet()) {
            for(String v: map.get(key)) {
                out.append(key)
                    .append(": ").append(v);
            }
        }
        return out.toString();
    }
}