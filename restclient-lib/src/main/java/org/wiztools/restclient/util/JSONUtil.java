package org.wiztools.restclient.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author subwiz
 */
public final class JSONUtil {
    
    private static final Logger LOG = Logger.getLogger(JSONUtil.class.getName());
    
    private JSONUtil(){}
    
    public static class JSONParseException extends Exception{
        public JSONParseException(String message){
            super(message);
        }
    }
    
    // Jackson Object Mapper used in indent operation:
    private static final ObjectMapper jsonObjMapper = new ObjectMapper();
    static {
        jsonObjMapper.enable(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS);
    }
    
    public static String indentJSON(final String jsonIn) throws JSONParseException{
        JsonFactory fac = new JsonFactory();
        try{
            JsonParser parser = fac.createJsonParser(new StringReader(jsonIn));
            JsonNode node = null;
            try{
                node = jsonObjMapper.readTree(parser);
            }
            catch(JsonParseException ex){
                throw new JSONParseException(ex.getMessage());
            }
            StringWriter out = new StringWriter();

            // Create pretty printer:
            JsonGenerator gen = fac.createJsonGenerator(out);
            gen.useDefaultPrettyPrinter();

            // Now write:
            jsonObjMapper.writeTree(gen, node);
            
            gen.flush();
            gen.close();
            return out.toString();
        }
        catch(IOException ex){
            LOG.log(Level.SEVERE, null, ex);
        }
        return jsonIn;
    }

}
