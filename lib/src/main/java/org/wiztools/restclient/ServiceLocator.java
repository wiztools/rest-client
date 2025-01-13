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

    public static boolean traceLog = true;

    private final static Map<String, Object> singletonObjs = new HashMap<>();
    private static synchronized <T> Object getSingletonInst(Class<T> c) {
        String cName = c.getCanonicalName();
        Object inst = singletonObjs.get(cName);
        if(inst != null) {
            if(traceLog) System.out.println("[singleton-inst-available]"+cName);
            return inst;
        } else {
            inst = getInst(c, true);
            singletonObjs.put(cName, inst);
            return inst;
        }
    }

    private static <T> Object getInst(Class<T> c) {
        return getInst(c, false);
    }

    private static <T> Object getInst(Class<T> c, boolean isSingleton) {
        String cName = c.getCanonicalName();
        try {
            try {
                Constructor<?> cnst = c.getDeclaredConstructor();
                cnst.setAccessible(true);
                final Object inst = cnst.newInstance();
                if(traceLog) System.out.println("[inst-created]"+cName);
                if(isSingleton) {
                    singletonObjs.put(cName, inst);
                }
                fillInst(inst);
                return inst;
            } catch(NoSuchMethodException ex) {
                if(traceLog) System.out.println("[no-no-param-declared-cnstrtr]:"+cName);
                for(Constructor<?> dc: c.getDeclaredConstructors()) {
                    if(dc.isAnnotationPresent(Inject.class)) {
                        List<Object> cnstParams = new ArrayList<>();
                        for(Parameter p: dc.getParameters()) {
                            cnstParams.add(getInstance(p.getType()));
                        }
                        dc.setAccessible(true);
                        final Object inst = dc.newInstance(cnstParams.toArray(Object[]::new));
                        if(isSingleton) {
                            singletonObjs.put(cName, inst);
                        }
                        fillInst(inst);
                        return inst;
                    }
                }
            }
            throw new RuntimeException("[no-declared-cnstrtr]:"+c.getCanonicalName());
        } catch(IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException ex) {
            throw new RuntimeException("[srvc-loc-get]"+cName, ex);
        }
    }

    private static <T> void fillInst(Object inst) {
        Class<?> c = inst.getClass();
        final String cName = c.getCanonicalName();
        try {
            // @Inject annotation processing:
            for(Field f: c.getDeclaredFields()) {
                if(f.isAnnotationPresent(Inject.class)) {
                    if(traceLog) System.out.println("[inject-present]"+cName+":"+f.getName()+":"+f.getType());
                    f.setAccessible(true);
                    f.set(inst, getInstance(f.getType()));
                }
            }

            // @PostConstruct annotation processing:
            for(Method m: c.getDeclaredMethods()) {
                if(m.isAnnotationPresent(PostConstruct.class)) {
                    if(traceLog) System.out.println("[post-construct-invoke]"+cName+":"+m.getName());
                    m.setAccessible(true);
                    m.invoke(inst);
                }
            }
        } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException("[srvc-loc-fill]:"+cName, ex);
        }
    }

    public static <T> T getInstance(Class<T> c) {
        String cName = c.getCanonicalName();
        if(traceLog) System.out.println("[getInstance()]"+cName);

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
