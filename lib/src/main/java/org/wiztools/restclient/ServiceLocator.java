package org.wiztools.restclient;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
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
            final Object o = inst;
            new Thread() { // start a thread to fill the object!
                public void run() {
                    fillInst(o);
                }
            }.start();
            return inst;
        }
    }

    private static <T> Object getInst(Class<T> c) {
        String cName = c.getCanonicalName();
        System.out.println("getInst():"+cName);
        try {
            try {
                Constructor<?> cnst = c.getDeclaredConstructor();
                cnst.setAccessible(true);
                Object inst = cnst.newInstance();
                System.out.println("inst-created");
                return inst;
            } catch(NoSuchMethodException ex) {
                System.out.println("[no-no-param-declared-cnstrtr]:"+cName);
                for(Constructor<?> dc: c.getDeclaredConstructors()) {
                    if(dc.isAnnotationPresent(Inject.class)) {
                        List<Object> cnstParams = new ArrayList<>();
                        for(Parameter p: dc.getParameters()) {
                            cnstParams.add(getInstance(p.getType()));
                        }
                        dc.setAccessible(true);
                        return dc.newInstance(cnstParams.toArray(Object[]::new));
                    }
                }
            }
            throw new RuntimeException("[no-declared-cnstrtr]:"+c.getCanonicalName());
        } catch(IllegalAccessException ex) {
            throw new RuntimeException("[srvc-loc-get]:"+cName, ex);
        } catch(IllegalArgumentException ex) {
            throw new RuntimeException("[srvc-loc-get]:"+cName, ex);
        } catch(InstantiationException ex) {
            throw new RuntimeException("[srvc-loc-get]:"+cName, ex);
        } catch(InvocationTargetException ex) {
            throw new RuntimeException("[srvc-loc-get]:"+cName, ex);
        }
    }

    private static <T> void fillInst(Object inst) {
        Class<?> c = inst.getClass();
        final String cName = c.getCanonicalName();
        System.out.println("getInst():"+cName);
        try {
            // @Inject annotation processing:
            Map<Field, Object> fieldVals = new HashMap<>();
            for(Field f: c.getDeclaredFields()) {
                System.out.println(cName+":"+f.getName());
                if(f.isAnnotationPresent(Inject.class)) {
                    System.out.println("inject-present:"+cName+":"+f.getName()+":"+f.getType());
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
        } catch(IllegalAccessException ex) {
            throw new RuntimeException("[srvc-loc-fill]:"+cName, ex);
        } catch(IllegalArgumentException ex) {
            throw new RuntimeException("[srvc-loc-fill]:"+cName, ex);
        } catch(InvocationTargetException ex) {
            throw new RuntimeException("[srvc-loc-fill]:"+cName, ex);
        }
    }

    public static <T> T getInstance(Class<T> c) {
        String cName = c.getCanonicalName();
        System.out.println("getInstance():"+cName);

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
    }
}
