package org.wiztools.restclient;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonNode;
import org.codehaus.jackson.map.JsonTypeMapper;

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
            JsonTypeMapper mapper = new JsonTypeMapper();
            JsonNode node = null;
            try{
                node = mapper.read(parser);
            }
            catch(JsonParseException ex){
                throw new JSONParseException(ex.getMessage());
            }
            StringWriter out = new StringWriter();
            JsonGenerator gen = fac.createJsonGenerator(out);
            gen.useDefaultPrettyPrinter();
            node.writeTo(gen);
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
