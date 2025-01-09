package org.wiztools.restclient;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
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
        System.out.println("getInst():"+c.getCanonicalName());
        try {
            Constructor<?> cnst = c.getDeclaredConstructor();
            cnst.setAccessible(true);
            Object inst = cnst.newInstance();
            System.out.println("inst-created");

            // @Inject annotation processing:
            Map<Field, Object> fieldVals = new HashMap<>();
            System.out.println("Fields:"+ c.getDeclaredFields().length);
            for(Field f: c.getDeclaredFields()) {
                System.out.println(c.getCanonicalName()+":"+f.getName());
                if(f.isAnnotationPresent(Inject.class)) {
                    System.out.println("inject-present:"+c.getCanonicalName()+":"+f.getName()+":"+f.getType());
                    fieldVals.put(f, getInstance(f.getType()));
                }
            }
            for(Map.Entry<Field, Object> e: fieldVals.entrySet()) {
                Field f = e.getKey();
                f.setAccessible(true);
                f.set(inst, e.getValue());
            }

            // @PostConstruct annotation processing:
            for(Method m: c.getMethods()) {
                if(m.isAnnotationPresent(PostConstruct.class)) {
                    m.setAccessible(true);
                    m.invoke(inst);
                }
            }

            // Return the created instance:
            return inst;
        } catch(NoSuchMethodException ex) {
            throw new RuntimeException(ex);
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

    private static List<String> cyclicalClass = new LinkedList<>();
    public static <T> T getInstance(Class<T> c) {
        String cName = c.getCanonicalName();
        System.out.println("getInstance():"+cName);
        if (cyclicalClass.contains(cName)) {
            // cyclical, return:
            System.out.println("cyclical:"+cName);
            return null;
        }
        cyclicalClass.addLast(c.getCanonicalName());

        try {
            if (c.isAnnotationPresent(Singleton.class)) {
                return (T)getSingletonInst(c);
            } else if(c.isInterface() && c.isAnnotationPresent(ImplementedBy.class)) {
                ImplementedBy ann = c.getAnnotation(ImplementedBy.class);
                Class<?> implClass = ann.value();
                if(implClass.isAnnotationPresent(Singleton.class)) {
                    return (T)getSingletonInst(implClass);
                } else {
                    return (T)getInst(implClass);
                }
            } else {
                return (T)getInst(c);
            }
        } finally {
            cyclicalClass.removeLast();
        }
    }
}
