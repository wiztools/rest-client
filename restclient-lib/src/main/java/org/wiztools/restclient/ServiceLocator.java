package org.wiztools.restclient;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.mycila.inject.jsr250.Jsr250;

/**
 *
 * @author subwiz
 */
public class ServiceLocator {
    private static final Injector injector =  Guice.createInjector(
            Stage.PRODUCTION, Jsr250.newJsr250Module());

    private ServiceLocator() {}
    
    public static <T> T getInstance(Class<T> c) {
        return injector.getInstance(c);
    }
}
