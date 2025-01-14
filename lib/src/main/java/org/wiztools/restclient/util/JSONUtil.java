package org.wiztools.restclient.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        jsonObjMapper.enable(
            DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS,
            DeserializationFeature.USE_BIG_INTEGER_FOR_INTS
        );
    }

    public static String indentJSON(final String jsonIn) throws JSONParseException{
        JsonFactory fac = new JsonFactory();
        try{
            JsonParser parser = fac.createParser(new StringReader(jsonIn));
            JsonNode node = null;
            try{
                node = jsonObjMapper.readTree(parser);
            }
            catch(JsonParseException ex){
                throw new JSONParseException(ex.getMessage());
            }
            StringWriter out = new StringWriter();

            // Create pretty printer:
            JsonGenerator gen = fac.createGenerator(out);
            DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
            pp.indentArraysWith(new DefaultPrettyPrinter.FixedSpaceIndenter());
            gen.setPrettyPrinter(pp);

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
