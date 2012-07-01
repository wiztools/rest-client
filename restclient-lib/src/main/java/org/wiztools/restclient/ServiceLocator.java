package org.wiztools.restclient;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 *
 * @author subwiz
 */
public class ServiceLocator {
    private static final Injector injector =  Guice.createInjector();

    private ServiceLocator() {}
    
    public static <T> T getInstance(Class<T> c) {
        return injector.getInstance(c);
    }
}
