package org.wiztools.restclient.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author subwiz
 */
public final class JSONUtil {
    
    private JSONUtil(){}
    
    public static class JSONParseException extends Exception{
        public JSONParseException(String message){
            super(message);
        }
    }
    
    public static String indentJSON(final String jsonIn) throws JSONParseException{
        JsonFactory fac = new JsonFactory();
        try{
            JsonParser parser = fac.createJsonParser(new StringReader(jsonIn));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = null;
            try{
                node = mapper.readTree(parser);
            }
            catch(JsonParseException ex){
                throw new JSONParseException(ex.getMessage());
            }
            StringWriter out = new StringWriter();

            // Create pretty printer:
            JsonGenerator gen = fac.createJsonGenerator(out);
            gen.useDefaultPrettyPrinter();

            // Now write:
            mapper.writeTree(gen, node);
            
            gen.flush();
            gen.close();
            return out.toString();
        }
        catch(IOException ex){
            ex.printStackTrace();
            assert true: ex;
        }
        return jsonIn;
    }

}
