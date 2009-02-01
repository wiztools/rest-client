package org.wiztools.restclient;

import java.util.Hashtable;
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

    private static final ResourceBundle rb = ResourceBundle.getBundle("difw");

    public static <T> T of(Class<T> c) throws ImplementationLoadException{
        return of(c, false);
    }

    public static <T> T of(Class<T> c, boolean newInstance) throws ImplementationLoadException{
        System.out.println("CLASS: " + c.getName());
        try{
            final String implClassStr = rb.getString(c.getName());
            System.out.println("IMPL CLASS: " + implClassStr);
            if(newInstance){
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
