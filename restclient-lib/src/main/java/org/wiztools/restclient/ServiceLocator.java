package org.wiztools.restclient;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

/**
 *
 * @author subwiz
 */
public class ServiceLocator {
    private ServiceLocator() {}

    private static Map<String, Object> singletonObjs = new HashMap<>();
    private static synchronized <T> Object getSingletonInst(Class<T> c) {
        String cName = c.getCanonicalName();
        Object inst = singletonObjs.get(cName);
        if(inst != null) {
            System.out.println("inst-available:"+cName);
            return inst;
        } else {
            inst = getInst(c);
            singletonObjs.put(cName, inst);
            return inst;
        }
    }

    private static <T> Object getInst(Class<T> c) {
        try {
            return c.getConstructors()[0].newInstance();
        } catch(IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch(IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch(InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch(InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T getInstance(Class<T> c) {
        System.out.println(c.getCanonicalName());
        if (c.isAnnotationPresent(Singleton.class)) {
            return (T)getSingletonInst(c);
        } else if(c.isInterface() && c.isAnnotationPresent(ImplementedBy.class)) {
            ImplementedBy ann = c.getAnnotation(ImplementedBy.class);
            return (T)getInst(ann.value());
        } else {
            throw new RuntimeException("[ServiceLocator-404]:"+c.getCanonicalName());
        }
    }
}
