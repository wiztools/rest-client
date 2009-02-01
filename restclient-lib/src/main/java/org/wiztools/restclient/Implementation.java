package org.wiztools.restclient;

import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 *
 * @author subwiz
 */
public class Implementation {

    private static final Logger LOG = Logger.getLogger(Implementation.class.getName());

    /**
     * This is the variable holding the object cache
     */
    private static final Hashtable<String, Object> ht = new Hashtable<String, Object>();

    private static final ResourceBundle rb = ResourceBundle.getBundle("org.wiztools.restclient.implementation");

    public static <T> T of(Class<T> c) throws ImplementationLoadException{
        try{
            final String className = c.getName();
            final String implClassStr = rb.getString(className);
            // Default class creation behavior:
            boolean isSingleton = false;
            try{
                String isSingletonStr = rb.getString(className + ".singleton");
                isSingleton = Boolean.getBoolean(isSingletonStr);
            }
            catch(MissingResourceException ex){
                LOG.finest("Singleton property not set for class: " + className);
            }
            if(!isSingleton){
                return (T) Class.forName(implClassStr).newInstance();
            }
            T o = (T)ht.get(c.getName());
            if(o == null){
                o = (T) Class.forName(implClassStr).newInstance();
                ht.put(c.getName(), o);
            }
            else{
                LOG.finest("Object already available in cache: " + c.getName());
            }
            return o;
        }
        catch(ClassNotFoundException ex){
            throw new ImplementationLoadException(ex);
        }
        catch(InstantiationException ex){
            throw new ImplementationLoadException(ex);
        }
        catch(IllegalAccessException ex){
            throw new ImplementationLoadException(ex);
        }
    }
}
