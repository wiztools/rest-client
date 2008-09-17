/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonNode;
import org.codehaus.jackson.map.JsonTypeMapper;

/**
 *
 * @author subwiz
 */
public final class JSONUtil {
    
    private JSONUtil(){}
    
    public static String indentJSON(final String jsonIn){
        JsonFactory fac = new JsonFactory();
        try{
            JsonParser parser = fac.createJsonParser(new StringReader(jsonIn));
            JsonTypeMapper mapper = new JsonTypeMapper();
            JsonNode node = mapper.read(parser);
            StringWriter out = new StringWriter();
            JsonGenerator gen = fac.createJsonGenerator(out);
            gen.useDefaultPrettyPrinter();
            node.writeTo(gen);
            return out.toString();
        }
        catch(IOException ex){
            assert true: "Should not come here!";
        }
        return null;
    }

}
