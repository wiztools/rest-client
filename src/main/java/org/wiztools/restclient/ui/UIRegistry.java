package org.wiztools.restclient.ui;

/**
 * Class holding the RESTView class instance for use with other independent classes.
 * @author subwiz
 */
final class UIRegistry {
    
    private static UIRegistry _instance;
    
    private UIRegistry(){}
    
    public static UIRegistry getInstance(){
        if(_instance == null){
            _instance = new UIRegistry();
        }
        return _instance;
    }
    
    public RESTView view;
}
